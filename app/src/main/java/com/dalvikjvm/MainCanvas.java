/**
 *	This file is part of DalvikJVM.
 *
 *	DalvikJVM is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	DalvikJVM is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with DalvikJVM.  If not, see <http://www.gnu.org/licenses/>.
 *
 *	Authors: see <https://github.com/DalvikJVM/DalvikJVM>
 */

package com.dalvikjvm;

import android.content.Context;
import android.graphics.*;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.android.awt.AndroidGraphics;
import java.awt.*;
import java.awt.Point;
import java.awt.event.InputEvent;

public class MainCanvas extends View {
    Paint paint;
    Rect dstRect;
    Bitmap mouseBitmap = null;
    float mouseX;
    float mouseY;
    float[] lastTouchX;
    float[] lastTouchY;
    long[] pressTime;
    int mouseMode = MODE_TOUCHPAD;

    int mouseIndex = -1;
    int joystickIndex = -1;
    int scrollWheelIndex = -1;
    boolean[] mouseDown;
    boolean[] mousePressed;
    boolean[] mouseReleased;
    int[] mouseUIButtonPressed;
    boolean mouseConsumed;
    boolean canTouchpadClick;

    private Event lastHorizJoystickEvent = null;
    private Event lastVertJoystickEvent = null;

    public static final int MODE_TOUCHSCREEN = 0;
    public static final int MODE_TOUCHPAD = 1;

    private float JOYSTICK_DEADZONE = 0.25f;
    private float JOYSTICK_BORDER_SIZE = 24.0f;
    private float INPUT_SEPERATOR_SIZE = 16.0f;

    private final int TOUCHPAD_LEFTCLICK_TIME = 200;
    private final int TOUCHPAD_RIGHTCLICK_TIME = 300;
    private float TOUCHPAD_MOVE_DEADZONE = DalvikJVM.convertDpToPixel(5);
    public static final int TOUCHPAD_CLICK_DELAY = 50;

    public static final int KEY_REPEAT_DELAY = 50;
    public static final int MOUSE_WHEEL_REPEAT_DELAY = 75;

    private int realWidth = -1;
    private int realHeight;

    private int touchpadClickCount;
    private long touchpadClickTime;

    private long lastScrollTime;

    public static MainCanvas instance;

    public static String statusText;
    public static Object statusTextLock = new Object();

    public static void setStatusText(String text) {
        synchronized (statusTextLock) {
            statusText = text;
        }
    }

    public MainCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);

        instance = this;

        paint = new Paint(Paint.HINTING_ON);
        setBackgroundColor(Color.BLACK);

        mouseDown = new boolean[256];
        mousePressed = new boolean[256];
        mouseReleased = new boolean[256];
        lastTouchX = new float[256];
        lastTouchY = new float[256];
        pressTime = new long[256];
        mouseUIButtonPressed = new int[256];

        for (int i = 0; i < mouseDown.length; i++) {
            mouseUIButtonPressed[i] = -1;
        }

        lastScrollTime = System.currentTimeMillis();
    }

    private boolean drawButton(int id, Canvas canvas, int x, int y, int w, int h, String str, boolean down) {
        w = Math.max(w, (int) DalvikJVM.convertDpToPixel(64.0f));

        boolean isPressed = false;
        Rect dimensions = new Rect(x, y, x + w, y + h);

        boolean isHeld = false;
        int touchIndex = -1;
        for (int i = 0; i < mousePressed.length; i++) {
            if (mouseUIButtonPressed[i] == id) {
                isHeld = mouseDown[i] && dimensions.contains((int)lastTouchX[i], (int)lastTouchY[i]);
                touchIndex = i;
                break;
            }

            isPressed = mousePressed[i] && dimensions.contains((int)lastTouchX[i], (int)lastTouchY[i]);
            if (isPressed) {
                if (i == mouseIndex) {
                    mouseIndex = -1;
                    if (touchpadClickCount == 1)
                        touchpadClickCount -= 1;
                }
                touchIndex = i;
                break;
            }
            if (isHeld) {
                if (i == mouseIndex || i == joystickIndex || i == scrollWheelIndex)
                    isHeld = false;

                if (mouseUIButtonPressed[i] != id) {
                        isHeld = false;
                }

                if (isHeld) {
                    break;
                }
            }
            isHeld = false;
        }

        int color = Color.BLUE;
        if (isHeld)
            color = Color.WHITE;
        paint.setColor(color);
        paint.setAlpha(64);
        canvas.drawRect(dimensions, paint);
        paint.setColor(Color.WHITE);
        paint.setTextSize(DalvikJVM.convertDpToPixel(16.0f));
        Rect renderBounds = new Rect();
        paint.getTextBounds(str, 0, str.length(), renderBounds);
        canvas.drawText(str, x + (w / 2) - (paint.measureText(str) / 2), y + (h / 2) + (renderBounds.height() / 4), paint);
        paint.setAlpha(255);

        if (touchIndex == -1)
            return false;

        if (down) {
            if (isPressed)
                mouseUIButtonPressed[touchIndex] = id;
            return isHeld;
        } else {
            if (isPressed)
                mouseUIButtonPressed[touchIndex] = id;
            boolean ret = mouseReleased[touchIndex] && dimensions.contains((int)lastTouchX[touchIndex], (int)lastTouchY[touchIndex]);
            return ret;
        }
    }

    private void setJoystickEvent(Event newEvent, boolean vertical) {
        boolean released = false;
        long delay = 0;

        Event lastJoystickEvent = lastHorizJoystickEvent;
        if (vertical)
            lastJoystickEvent = lastVertJoystickEvent;

        if (newEvent != null && lastJoystickEvent != null && newEvent._getKey() == lastJoystickEvent._getKey()) {
            lastJoystickEvent.id = Event.KEY_PRESS;
            long diff = System.currentTimeMillis() - lastJoystickEvent.when;
            if (diff >= KEY_REPEAT_DELAY) {
                lastJoystickEvent.when = System.currentTimeMillis();
                DalvikJVM.instance.addEventQueue(lastJoystickEvent);
            }
            return;
        }

        if (lastJoystickEvent != null) {
            lastJoystickEvent.id = Event.KEY_RELEASE;
            lastJoystickEvent.when = System.currentTimeMillis();
            DalvikJVM.instance.addEventQueue(lastJoystickEvent);
            released = true;
            delay = lastJoystickEvent.when + DalvikJVM.KEYBOARD_DELAY;
            System.out.println("key up");
        }

        if (vertical)
            lastVertJoystickEvent = newEvent;
        else
            lastHorizJoystickEvent = newEvent;
        lastJoystickEvent = newEvent;

        if (lastJoystickEvent != null) {
            if (released)
                lastJoystickEvent.when = delay;
            DalvikJVM.instance.addEventQueue(lastJoystickEvent);
            System.out.println("key down");
        }
    }

    private boolean drawJoystick(Canvas canvas, int x, int y, int radius, int innerRadius) {
        int dpRadius = (int) DalvikJVM.convertDpToPixel(radius);
        int color = Color.BLUE;
        if (joystickIndex != -1)
            color = Color.WHITE;
        paint.setColor(color);
        paint.setAlpha(64);
        canvas.drawCircle(x, y, dpRadius, paint);

        Rect dimensions = new Rect(x - dpRadius, y - dpRadius, x + dpRadius, y + dpRadius);

        if (joystickIndex == -1) {
            for (int i = 0; i < mousePressed.length; i++) {
                if (mousePressed[i] && dimensions.contains((int) lastTouchX[i], (int) lastTouchY[i])) {
                    joystickIndex = i;
                    break;
                }
            }
        }

        if (joystickIndex != -1) {
                    float axisX = (lastTouchX[joystickIndex] - x) / dpRadius;
                    float axisY = (lastTouchY[joystickIndex] - y) / dpRadius;
                    axisX = Math.max(Math.min(axisX, 1.0f), -1.0f);
                    axisY = Math.max(Math.min(axisY, 1.0f), -1.0f);
                    x += axisX * dpRadius;
                    y += axisY * dpRadius;

                    Event evt = null;
                    if (Math.abs(axisX) >= JOYSTICK_DEADZONE) {
                        if (axisX < 0.0f)
                            evt = DalvikJVM.instance.convertAndroidKeycode(KeyEvent.KEYCODE_DPAD_UP, Event.KEY_PRESS, false);
                        else if (axisX > 0.0f)
                            evt = DalvikJVM.instance.convertAndroidKeycode(KeyEvent.KEYCODE_DPAD_DOWN, Event.KEY_PRESS, false);
                        setJoystickEvent(evt, false);
                    }

                    if (Math.abs(axisY) >= JOYSTICK_DEADZONE) {
                        if (axisY < 0.0f)
                            evt = DalvikJVM.instance.convertAndroidKeycode(KeyEvent.KEYCODE_DPAD_RIGHT, Event.KEY_PRESS, false);
                        else if (axisY > 0.0f)
                            evt = DalvikJVM.instance.convertAndroidKeycode(KeyEvent.KEYCODE_DPAD_LEFT, Event.KEY_PRESS, false);
                        setJoystickEvent(evt, true);
                    }

                    canvas.drawCircle(x, y, DalvikJVM.convertDpToPixel(innerRadius), paint);
                    paint.setAlpha(255);
                    paint.setColor(Color.WHITE);

                    if (joystickIndex == mouseIndex && mousePressed[joystickIndex]) {
                        mouseIndex = -1;
                        if (touchpadClickCount == 1)
                            touchpadClickCount -= 1;
                    }

                    return true;
        }

        canvas.drawCircle(x, y, DalvikJVM.convertDpToPixel(innerRadius), paint);
        setJoystickEvent(null, false);
        setJoystickEvent(null, true);
        paint.setAlpha(255);
        paint.setColor(Color.WHITE);

        return false;
    }

    private boolean drawScrollWheel(Canvas canvas, int x, int y, int radius, int innerRadius) {
        int dpRadius = (int) DalvikJVM.convertDpToPixel(radius);
        int dpInnerRadius = (int) DalvikJVM.convertDpToPixel(innerRadius);
        int color = Color.BLUE;
        if (scrollWheelIndex != -1)
            color = Color.WHITE;
        paint.setColor(color);
        paint.setAlpha(64);

        RectF dimensions = new RectF(x - dpInnerRadius, y - dpRadius, x + dpInnerRadius, y + dpRadius);

        canvas.drawOval(dimensions, paint);

        if (scrollWheelIndex == -1) {
            for (int i = 0; i < mousePressed.length; i++) {
                if (mousePressed[i] && dimensions.contains((int) lastTouchX[i], (int) lastTouchY[i])) {
                    scrollWheelIndex = i;
                    break;
                }
            }
        }

        if (scrollWheelIndex != -1) {
            float axisY = (lastTouchY[scrollWheelIndex] - y) / dpRadius;
            axisY = Math.max(Math.min(axisY, 1.0f), -1.0f);
            y += axisY * dpRadius;

            Event evt = null;
            if (Math.abs(axisY) >= JOYSTICK_DEADZONE) {
                if (axisY < 0.0f)
                    evt = new Event(DalvikJVM.getTarget(), Event._MOUSE_SCROLL_UP, null);
                else if (axisY > 0.0f)
                    evt = new Event(DalvikJVM.getTarget(), Event._MOUSE_SCROLL_DOWN, null);
            }

            long elapsed = System.currentTimeMillis() - lastScrollTime;
            if (evt != null && elapsed >= 0) {
                float mX = mouseX - dstRect.left;
                float mY = mouseY - dstRect.top;
                float xScale = mX / dstRect.width();
                float yScale = mY / dstRect.height();
                boolean shift = DalvikJVM.instance.isShiftPressed();
                boolean ctrl = DalvikJVM.instance.isCtrlPressed();
                boolean alt = DalvikJVM.instance.isAltPressed();
                evt.x = (int)(DalvikJVM.getTarget().getWidth() * xScale);
                evt.y = (int)(DalvikJVM.getTarget().getHeight() * yScale);
                evt._awtAmount = 3;

                if (shift)
                    evt.modifiers |= InputEvent.SHIFT_DOWN_MASK;
                if (ctrl)
                    evt.modifiers |= InputEvent.CTRL_DOWN_MASK;
                if (alt)
                    evt.modifiers |= InputEvent.ALT_DOWN_MASK;

                float invAxisY = (1.0f - Math.abs(axisY) + JOYSTICK_DEADZONE) * Math.signum(axisY);
                long delayTime = (long)Math.abs(MOUSE_WHEEL_REPEAT_DELAY * invAxisY);
                System.out.println("Axis: " + invAxisY);
                System.out.println("Delay: " + delayTime);
                lastScrollTime = System.currentTimeMillis() + delayTime;
                DalvikJVM.instance.addEventQueue(evt);
            }

            canvas.drawCircle(x, y, DalvikJVM.convertDpToPixel(innerRadius), paint);
            paint.setAlpha(255);
            paint.setColor(Color.WHITE);

            if (scrollWheelIndex == mouseIndex && mousePressed[scrollWheelIndex]) {
                mouseIndex = -1;
                if (touchpadClickCount == 1)
                    touchpadClickCount -= 1;
            }

            return true;
        }

        canvas.drawCircle(x, y, DalvikJVM.convertDpToPixel(innerRadius), paint);
        paint.setAlpha(255);
        paint.setColor(Color.WHITE);

        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mouseBitmap == null) {
            mouseBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.cursor);
            mouseX = getWidth() / 2;
            mouseY = getHeight() / 2;
        }

        if (realWidth == -1) {
            realWidth = getWidth();
            realHeight = getHeight();
        }

        invalidate();

        synchronized (statusTextLock) {
            if (statusText != null) {
                paint.setTextSize(DalvikJVM.convertDpToPixel(16.0f));
                paint.setColor(Color.WHITE);
                canvas.drawText(statusText, 0, getHeight() - 128, paint);
            }
        }

        // Process java event queue
        DalvikJVM.instance.processEventQueue();

        if (DalvikJVM.getTarget() == null)
            return;

        if (mouseMode == MODE_TOUCHPAD && canTouchpadClick && touchpadClickCount > 0) {
            long elapsed = System.currentTimeMillis() - touchpadClickTime;

            if (touchpadClickCount >= 2 && mouseIndex != -1) {
                float x = mouseX - dstRect.left;
                float y = mouseY - dstRect.top;
                float xScale = x / dstRect.width();
                float yScale = y / dstRect.height();
                boolean shift = DalvikJVM.instance.isShiftPressed();
                boolean ctrl = DalvikJVM.instance.isCtrlPressed();
                boolean alt = DalvikJVM.instance.isAltPressed();
                Event evt = new Event(DalvikJVM.getTarget(), Event.MOUSE_DOWN, null);
                Point translatePoint = DalvikJVM.getTarget().getGraphics()._getTranslate();
                evt.x = (int) (DalvikJVM.getTarget().getWidth() * xScale) - translatePoint.x;
                evt.y = (int) (DalvikJVM.getTarget().getHeight() * yScale) - translatePoint.y;

                if (shift)
                    evt.modifiers |= InputEvent.SHIFT_DOWN_MASK;
                if (ctrl)
                    evt.modifiers |= InputEvent.CTRL_DOWN_MASK;
                if (alt)
                    evt.modifiers |= InputEvent.ALT_DOWN_MASK;
                DalvikJVM.instance.addEventQueue(evt);
                canTouchpadClick = false;
            }

            if (elapsed >= TOUCHPAD_LEFTCLICK_TIME) {
                if (mouseIndex == -1) {
                    float x = mouseX - dstRect.left;
                    float y = mouseY - dstRect.top;
                    float xScale = x / dstRect.width();
                    float yScale = y / dstRect.height();
                    boolean shift = DalvikJVM.instance.isShiftPressed();
                    boolean ctrl = DalvikJVM.instance.isCtrlPressed();
                    boolean alt = DalvikJVM.instance.isAltPressed();
                    Event evt = new Event(DalvikJVM.getTarget(), Event.MOUSE_DOWN, null);
                    Point translatePoint = DalvikJVM.getTarget().getGraphics()._getTranslate();
                    evt.x = (int) (DalvikJVM.getTarget().getWidth() * xScale) - translatePoint.x;
                    evt.y = (int) (DalvikJVM.getTarget().getHeight() * yScale) - translatePoint.y;

                    if (shift)
                        evt.modifiers |= InputEvent.SHIFT_DOWN_MASK;
                    if (ctrl)
                        evt.modifiers |= InputEvent.CTRL_DOWN_MASK;
                    if (alt)
                        evt.modifiers |= InputEvent.ALT_DOWN_MASK;

                    DalvikJVM.instance.addEventQueue(evt);
                    Event upEvent = new Event(evt.target, Event.MOUSE_UP, evt.arg);
                    upEvent.x = evt.x;
                    upEvent.y = evt.y;
                    upEvent.modifiers = evt.modifiers;
                    upEvent.when = evt.when + TOUCHPAD_CLICK_DELAY;
                    DalvikJVM.instance.addEventQueue(upEvent);
                    canTouchpadClick = false;
                    touchpadClickCount = 0;
                }
            }
        }

        if (mouseIndex != -1 && mouseMode == MODE_TOUCHPAD && canTouchpadClick) {
            long elapsed = System.currentTimeMillis() - pressTime[mouseIndex];
            if (elapsed >= TOUCHPAD_RIGHTCLICK_TIME) {
                float x = mouseX - dstRect.left;
                float y = mouseY - dstRect.top;
                float xScale = x / dstRect.width();
                float yScale = y / dstRect.height();
                boolean shift = DalvikJVM.instance.isShiftPressed();
                boolean ctrl = DalvikJVM.instance.isCtrlPressed();
                boolean alt = DalvikJVM.instance.isAltPressed();
                Event evt = new Event(DalvikJVM.getTarget(), Event.MOUSE_DOWN, null);
                Point translatePoint = DalvikJVM.getTarget().getGraphics()._getTranslate();
                evt.x = (int) (DalvikJVM.getTarget().getWidth() * xScale) - translatePoint.x;
                evt.y = (int) (DalvikJVM.getTarget().getHeight() * yScale) - translatePoint.y;
                evt.modifiers |= Event.META_MASK;

                if (shift)
                    evt.modifiers |= InputEvent.SHIFT_DOWN_MASK;
                if (ctrl)
                    evt.modifiers |= InputEvent.CTRL_DOWN_MASK;
                if (alt)
                    evt.modifiers |= InputEvent.ALT_DOWN_MASK;

                evt.modifiers |= InputEvent.BUTTON3_DOWN_MASK | InputEvent.META_DOWN_MASK;

                DalvikJVM.instance.addEventQueue(evt);
                Event upEvent = new Event(DalvikJVM.getTarget(), Event.MOUSE_UP, null);
                upEvent.x = evt.x;
                upEvent.y = evt.y;
                upEvent.when = evt.when + TOUCHPAD_CLICK_DELAY;
                upEvent.modifiers = evt.modifiers;
                DalvikJVM.instance.addEventQueue(upEvent);
                canTouchpadClick = false;
                touchpadClickCount = 0;
            }
        }

        AndroidGraphics graphics = (AndroidGraphics) DalvikJVM.getTarget().getGraphics();
        Rect srcRect = new Rect(0, 0, DalvikJVM.getTarget().getWidth(), DalvikJVM.getTarget().getHeight());
        float scale = (float)srcRect.width() / srcRect.height();

        // Initialize java buffer
        Rect r = new Rect();
        View rootview = DalvikJVM.instance.getWindow().getDecorView();
        rootview.getWindowVisibleDisplayFrame(r);
        int w = (int)(getHeight() * scale);
        int h = getHeight();
        int x = (getWidth() / 2) - (w / 2);
        int y = 0;
        int yOffset = 0;

        if (r.height() < getHeight()) {
            int yMin = 0;
            int yMax = (int)(-getHeight() + r.height());
            yOffset = (int)(-mouseY + (r.height() / 2.0f));
            yOffset = Math.min(Math.max(yOffset, yMax), yMin);
        }

        dstRect = new Rect(x, y, x + w, y + h);
        Rect drawDstRect = new Rect(dstRect.left, dstRect.top + yOffset, dstRect.right, dstRect.bottom + yOffset);

        // Draw java buffer
        DalvikJVM.getTarget().repaint();
        DalvikJVM.getTarget()._paint(canvas, paint);
        graphics._present(canvas, paint, srcRect, drawDstRect);

        // Draw Mouse
        float mouseScale = 0.3725f;
        float mouseCoordScaleX = (mouseX / realWidth) * getWidth();
        float mouseCoordScaleY = (mouseY / realHeight) * getHeight();
        int scaledWidth = (int)(mouseBitmap.getWidth() * mouseScale);
        int scaledHeight = (int)(mouseBitmap.getHeight() * mouseScale);
        canvas.drawBitmap(mouseBitmap,  new Rect(0, 0, mouseBitmap.getWidth(), mouseBitmap.getHeight()),
                new Rect((int)mouseCoordScaleX, (int)mouseCoordScaleY + yOffset, (int)mouseCoordScaleX + scaledWidth, (int)mouseCoordScaleY + scaledHeight + yOffset), paint);

        if (DalvikJVM.instance.hasFocus) {
            // Draw app ui
            if (drawButton(0, canvas, 0, 0, dstRect.left - 4, (int) DalvikJVM.convertDpToPixel(64.0f), "Keyboard", false)) {
                DalvikJVM.inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                //mouseConsumed = true;
            }

            if (r.height() >= getHeight()) {
                // Arrow key joystick
                int radius = 64;
                int innerRadius = 48;
                int joyX = (int) DalvikJVM.convertDpToPixel(radius) + (int) DalvikJVM.convertDpToPixel(JOYSTICK_BORDER_SIZE);
                int joyY = getHeight() - (int) DalvikJVM.convertDpToPixel(radius) - (int) DalvikJVM.convertDpToPixel(JOYSTICK_BORDER_SIZE);// - (getHeight() - r.height());
                if (drawJoystick(canvas, joyX, joyY, radius, innerRadius)) {
                    //mouseConsumed = true;
                }

                innerRadius = 32;
                joyX = (int) DalvikJVM.convertDpToPixel(radius) + (int) DalvikJVM.convertDpToPixel(JOYSTICK_BORDER_SIZE);
                joyY -= (DalvikJVM.convertDpToPixel(radius) * 2) + DalvikJVM.convertDpToPixel(INPUT_SEPERATOR_SIZE);
                if (drawScrollWheel(canvas, joyX, joyY, radius, innerRadius)) {
                }

                // Virtual Shift
                /*int width = (int)MainActivity.convertDpToPixel(128);
                int height = (int)MainActivity.convertDpToPixel(64);
                MainActivity.instance.virtualShift = drawButton(1, canvas, joyX - width / 2, joyY - (int)MainActivity.convertDpToPixel(radius) - height - (int)MainActivity.convertDpToPixel(8), width, height, "LShift", true);*/
            }
        }

        // Reset mouse pressed state
        for (int i = 0; i < mousePressed.length; i++) {
            if (mouseReleased[i])
                mouseUIButtonPressed[i] = -1;
            mousePressed[i] = false;
            mouseReleased[i] = false;
        }

        if (statusText != null) {
            canvas.drawText(statusText, 0, getHeight() - 128, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (DalvikJVM.getTarget() == null)
            return super.onTouchEvent(event);

        int touchIndex = -1;
        if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN || event.getActionMasked() == MotionEvent.ACTION_POINTER_UP)
            touchIndex = event.getPointerId(event.getActionIndex());

        for (int i = 0; i < event.getPointerCount(); i++) {
            int eventIndex = event.getPointerId(i);

            if (touchIndex != -1)
                eventIndex = touchIndex;

            if (eventIndex == mouseIndex && mouseConsumed) {
                mouseConsumed = false;
                pressTime[mouseIndex] -= TOUCHPAD_LEFTCLICK_TIME;
                if (event.getPointerCount() <= 1) {
                    lastTouchX[mouseIndex] = event.getX();
                    lastTouchY[mouseIndex] = event.getY();
                } else {
                    lastTouchX[mouseIndex] = event.getX(eventIndex);
                    lastTouchY[mouseIndex] = event.getY(eventIndex);
                }

                if (event.getActionMasked() == MotionEvent.ACTION_UP || event.getActionMasked() == MotionEvent.ACTION_POINTER_UP)
                    mouseDown[mouseIndex] = false;
                continue;
            }

            boolean updateLastTouch = true;

            int action = event.getActionMasked();

            if (eventIndex == mouseIndex) {
                if (mouseMode == MODE_TOUCHSCREEN) {
                    mouseX = event.getX(eventIndex);
                    mouseY = event.getY(eventIndex);
                } else if (mouseMode == MODE_TOUCHPAD && action == MotionEvent.ACTION_MOVE) {
                    float diffX = 0.0f;
                    float diffY = 0.0f;
                    if (event.getPointerCount() <= 1) {
                        diffX = event.getX() - lastTouchX[mouseIndex];
                        diffY = event.getY() - lastTouchY[mouseIndex];
                    } else {
                        if (eventIndex < event.getPointerCount()) {
                            diffX = event.getX(eventIndex) - lastTouchX[mouseIndex];
                            diffY = event.getY(eventIndex) - lastTouchY[mouseIndex];
                        }
                    }

                    if (canTouchpadClick && Math.abs(diffX) >= TOUCHPAD_MOVE_DEADZONE || Math.abs(diffY) >= TOUCHPAD_MOVE_DEADZONE)
                        canTouchpadClick = false;

                    if (!canTouchpadClick) {
                        mouseX += diffX;
                        mouseY += diffY;
                    } else {
                        updateLastTouch = false;
                    }
                }

                mouseX = Math.min(Math.max(0, mouseX), getWidth());
                mouseY = Math.min(Math.max(0, mouseY), getHeight());
            }

            float x = mouseX - dstRect.left;
            float y = mouseY - dstRect.top;
            float xScale = x / dstRect.width();
            float yScale = y / dstRect.height();
            boolean shift = DalvikJVM.instance.isShiftPressed();
            boolean ctrl = DalvikJVM.instance.isCtrlPressed();
            boolean alt = DalvikJVM.instance.isAltPressed();

            Point translatePoint = DalvikJVM.getTarget().getGraphics()._getTranslate();
            Event evt = new Event(DalvikJVM.getTarget(), 0, null);
            evt.x = (int) (DalvikJVM.getTarget().getWidth() * xScale) - translatePoint.x;
            evt.y = (int) (DalvikJVM.getTarget().getHeight() * yScale) - translatePoint.y;

            int buttonMask = InputEvent.BUTTON1_DOWN_MASK;

            if (shift)
                evt.modifiers |= InputEvent.SHIFT_DOWN_MASK;
            if (ctrl)
                evt.modifiers |= InputEvent.CTRL_DOWN_MASK;
            if (alt)
                evt.modifiers |= InputEvent.ALT_DOWN_MASK;

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    mouseReleased[eventIndex] = false;
                    mousePressed[eventIndex] = true;
                    mouseDown[eventIndex] = true;
                    pressTime[eventIndex] = System.currentTimeMillis();

                    if (mouseIndex == -1)
                        mouseIndex = eventIndex;

                    if (eventIndex == mouseIndex) {
                        if (mouseMode == MODE_TOUCHSCREEN) {
                            evt.id = Event.MOUSE_DOWN;
                            DalvikJVM.instance.addEventQueue(evt);
                        }

                        if (mouseMode == MODE_TOUCHPAD) {
                            if (!canTouchpadClick) {
                                touchpadClickTime = System.currentTimeMillis();
                                canTouchpadClick = true;
                            } else {
                                touchpadClickCount += 1;
                            }
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP: {
                    long elapsed = System.currentTimeMillis() - pressTime[eventIndex];
                    mouseReleased[eventIndex] = true;
                    mousePressed[eventIndex] = false;
                    mouseDown[eventIndex] = false;

                    if (eventIndex == joystickIndex)
                        joystickIndex = -1;

                    if (eventIndex == scrollWheelIndex)
                        scrollWheelIndex = -1;

                    if (eventIndex == mouseIndex) {
                        if (mouseMode == MODE_TOUCHSCREEN) {
                            evt.id = Event.MOUSE_UP;
                            DalvikJVM.instance.addEventQueue(evt);
                        }

                        if (touchpadClickCount == 0 && canTouchpadClick && mouseMode == MODE_TOUCHPAD) {
                            touchpadClickCount += 1;
                        }

                        if (!canTouchpadClick && mouseMode == MODE_TOUCHPAD && touchpadClickCount >= 2) {
                            evt.id = Event.MOUSE_UP;
                            DalvikJVM.instance.addEventQueue(evt);
                            touchpadClickCount = 0;
                        }

                        mouseIndex = -1;
                    }

                    break;
                }
                case MotionEvent.ACTION_MOVE:
                    if (eventIndex == mouseIndex) {
                        if (mouseMode == MODE_TOUCHSCREEN) {
                            evt.id = Event.MOUSE_DRAG;
                            DalvikJVM.instance.addEventQueue(evt);
                        }

                        if (mouseMode == MODE_TOUCHPAD) {
                            evt.id = Event.MOUSE_MOVE;
                            if (!canTouchpadClick && touchpadClickCount >= 2)
                                evt.id = Event.MOUSE_DRAG;
                            DalvikJVM.instance.addEventQueue(evt);
                        }
                    }
                    break;
            }

            evt.modifiers |= buttonMask;

            if (updateLastTouch) {
                if (event.getPointerCount() <= 1) {
                    lastTouchX[eventIndex] = event.getX();
                    lastTouchY[eventIndex] = event.getY();
                } else {
                    if (eventIndex < event.getPointerCount()) {
                        lastTouchX[eventIndex] = event.getX(eventIndex);
                        lastTouchY[eventIndex] = event.getY(eventIndex);
                    }
                }
            }

            if (touchIndex != -1)
                break;
        }

        return true;
    }
}
