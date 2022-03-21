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

package java.awt;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;

import javax.imageio.ImageIO;
import java.android.awt.AndroidComponentPeer;
import java.android.awt.AndroidGraphics;
import java.awt.dnd.DropTarget;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;
import java.awt.peer.ComponentPeer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public abstract class Component implements ImageObserver {
    Component _parent;
    protected List<Component> children = new ArrayList<Component>();
    protected List<ComponentListener> componentListeners = new ArrayList<ComponentListener>();
    protected List<MouseListener> mouseListeners = new ArrayList<MouseListener>();
    protected List<MouseMotionListener> mouseMotionListeners = new ArrayList<MouseMotionListener>();
    protected List<MouseWheelListener> mouseWheelListeners = new ArrayList<MouseWheelListener>();
    protected List<KeyListener> keyListeners = new ArrayList<KeyListener>();
    protected List<FocusListener> focusListeners = new ArrayList<FocusListener>();
    Point locationPoint = new Point();
    Dimension sizeDimension;
    ImageObserver observer;
    Image image;
    Graphics graphics = null;
    ComponentPeer peer = new AndroidComponentPeer();
    boolean mouseHovering = false;
    boolean visible;

    protected Color backgroundColor;
    protected Color foregroundColor;

    Object childLock = new Object();

    protected Dimension preferredSize;

    public Component() {
        preferredSize = new Dimension(1, 1);
        sizeDimension = new Dimension(1, 1);
    }

    public void setFocusable(boolean focusable) {
        System.out.println("Unimplemented method Component.setFocusable(" + focusable + ") called");
    }

    public Dimension size() {
        return sizeDimension;
    }

    public ComponentPeer getPeer() {
        return peer;
    }

    public void _setParent(Component component) {
        if (_parent != null) {
            _parent.remove(this);
        }
        _parent = component;
        if (_parent != null) {
            setSize(_parent.getWidth(), _parent.getHeight());
        }
    }

    public void _paint(android.graphics.Canvas canvas, Paint paint) {
        if (_parent == null) {
            AndroidGraphics graphics = (AndroidGraphics)getGraphics();
            canvas = graphics._getCanvas();
            paint = graphics._getPaint();
        } else {
            AndroidGraphics graphics = (AndroidGraphics) getGraphics();
            Bitmap bitmap = graphics._getBitmap();
            Rect srcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            int x1 = locationPoint.x;
            int y1 = locationPoint.y;
            int x2 = x1 + srcRect.width();
            int y2 = y1 + srcRect.height();
            Rect dstRect = new Rect(x1, y1, x2, y2);
            System.out.println("DRAW_LAYER " + this);
            canvas.drawBitmap(bitmap, srcRect, dstRect, paint);
        }

        synchronized (childLock) {
            for (Component child : children)
                child._paint(canvas, paint);
        }
    }

    private void _mouseEnter(Component component, Event e) {
        if (mouseHovering)
            return;

        mouseHovering = true;
        Event evt = new Event(e.target, Event.MOUSE_ENTER, e.arg);
        evt.x = e.x;
        evt.y = e.y;
        evt.modifiers = e.modifiers;
        evt._awtModifier = e._awtModifier;
        component.dispatchComponentEvent(evt);
    }

    private void _mouseExit(Component component, Event e) {
        if (!mouseHovering)
            return;

        mouseHovering = false;
        Event evt = new Event(e.target, Event.MOUSE_EXIT, e.arg);
        evt.x = e.x;
        evt.y = e.y;
        evt.modifiers = e.modifiers;
        evt._awtModifier = e._awtModifier;
        component.dispatchComponentEvent(evt);
    }

    public void setBounds(int x, int y, int width, int height) {
        setLocation(x, y);
        setSize(width, height);
    }

    public void dispatchComponentEvent(Event event) {
        MouseEvent mouseEvent = null;
        MouseWheelEvent mouseWheelEvent = null;
        KeyEvent keyEvent = null;
        FocusEvent focusEvent = null;

        int mouseButton = MouseEvent.BUTTON1;
        if ((event._awtModifier & InputEvent.BUTTON3_DOWN_MASK) == InputEvent.BUTTON3_DOWN_MASK)
            mouseButton = MouseEvent.BUTTON3;

        switch (event.id) {
            case Event.GOT_FOCUS:
                focusEvent = new FocusEvent((Component)event.arg, FocusEvent.FOCUS_GAINED);
                break;
            case Event.LOST_FOCUS:
                focusEvent = new FocusEvent((Component)event.arg, FocusEvent.FOCUS_LOST);
                break;
            case Event.KEY_PRESS:
                keyDown(event, event.key);
                keyEvent = new KeyEvent(this, KeyEvent.KEY_PRESSED, event.when, event._awtModifier,
                                            event._awtKey==-1?event.key:event._awtKey, event._keyChar);
                break;
            case Event.KEY_RELEASE:
                keyUp(event, event.key);
                keyEvent = new KeyEvent(this, KeyEvent.KEY_RELEASED, event.when, event._awtModifier,
                                            event._awtKey==-1?event.key:event._awtKey, event._keyChar);
                break;
            case Event.MOUSE_DOWN:
                mouseDown(event, event.x, event.y);
                mouseEvent = new MouseEvent(this, MouseEvent.MOUSE_PRESSED, event.when, event._awtModifier, event.x, event.y, 1, false, mouseButton);
                break;
            case Event.MOUSE_UP:
                mouseUp(event, event.x, event.y);
                mouseEvent = new MouseEvent(this, MouseEvent.MOUSE_RELEASED, event.when, event._awtModifier, event.x, event.y, 1, false, mouseButton);
                break;
            case Event.MOUSE_MOVE:
                mouseMove(event, event.x, event.y);
                mouseEvent = new MouseEvent(this, MouseEvent.MOUSE_MOVED, event.when, event._awtModifier, event.x, event.y, 1, false, 0);
                break;
            case Event.MOUSE_DRAG:
                mouseDrag(event, event.x, event.y);
                mouseEvent = new MouseEvent(this, MouseEvent.MOUSE_DRAGGED, event.when, event._awtModifier, event.x, event.y, 1, false, mouseButton);
                break;
            case Event.MOUSE_ENTER:
                mouseEvent = new MouseEvent(this, MouseEvent.MOUSE_ENTERED, event.when, event._awtModifier, event.x, event.y, 0, false, 0);
                break;
            case Event.MOUSE_EXIT:
                mouseEvent = new MouseEvent(this, MouseEvent.MOUSE_EXITED, event.when, event._awtModifier, event.x, event.y, 0, false, 0);
                break;
            case Event._MOUSE_SCROLL_UP:
                mouseWheelEvent = new MouseWheelEvent(this, MouseEvent.MOUSE_WHEEL, event.when, event._awtModifier, event.x, event.y, 0, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, event._awtAmount, -1);
                break;
            case Event._MOUSE_SCROLL_DOWN:
                mouseWheelEvent = new MouseWheelEvent(this, MouseEvent.MOUSE_WHEEL, event.when, event._awtModifier, event.x, event.y, 0, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, event._awtAmount, 1);
                break;
        }

        if (mouseEvent != null) {
            if (_withinBounds(event.x, event.y)) {
                _mouseEnter(this, event);
                _handleMouseEvent(this, mouseEvent);
            } else {
                _mouseExit(this, event);
                if (event.id == Event.MOUSE_EXIT)
                    _handleMouseEvent(this, mouseEvent);
            }
        }
        if (mouseWheelEvent != null) {
            if (_withinBounds(event.x, event.y))
                _handleMouseWheelEvent(this, mouseWheelEvent);
        }
        if (keyEvent != null)
            _handleKeyboardEvent(this, keyEvent);
        if (focusEvent != null)
            _handleFocusEvent(this, focusEvent);
    }

    public boolean _withinBounds(int x, int y) {
        return  (x >= 0 && x < sizeDimension.width) && (y >= 0 && y < sizeDimension.height);
    }

    public Image getImage(URL url, String name) {
        try {
            URL finalURL = new URL(url.toURI().toString() + name);
            BufferedImage image = ImageIO.read(finalURL.openConnection().getInputStream());
            System.out.println(image + ": " + finalURL);
            return image;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public MouseListener[] getMouseListeners() {
        MouseListener[] array = new MouseListener[mouseListeners.size()];
        for (int i = 0; i < mouseListeners.size(); i++)
            array[i] = mouseListeners.get(i);
        return array;
    }

    public void _handleKeyboardEvent(Component component, KeyEvent event) {
        for (KeyListener listener : component.keyListeners) {
            switch (event.getID()) {
                case KeyEvent.KEY_PRESSED:
                    listener.keyPressed(event);
                    if (event.getKeyChar() != '\n') {
                        event.id = KeyEvent.KEY_TYPED;
                        listener.keyTyped(event);
                    }
                    break;
                case KeyEvent.KEY_RELEASED:
                    listener.keyReleased(event);
                    break;
            }
        }

        synchronized (childLock) {
            for (Component child : component.children)
                _handleKeyboardEvent(child, event);
        }
    }

    public void _handleMouseEvent(Component component, MouseEvent event) {
        event.translatePoint(-component.getX(), -component.getY());

        for (MouseListener listener : component.mouseListeners) {
            switch (event.getID()) {
                case MouseEvent.MOUSE_PRESSED:
                    listener.mousePressed(event);
                    break;
                case MouseEvent.MOUSE_RELEASED:
                    listener.mouseReleased(event);
                    break;
                case MouseEvent.MOUSE_ENTERED:
                    listener.mouseEntered(event);
                    break;
                case MouseEvent.MOUSE_EXITED:
                    listener.mouseExited(event);
                    break;
            }
        }

        for (MouseMotionListener listener : component.mouseMotionListeners) {
            switch (event.getID()) {
                case MouseEvent.MOUSE_MOVED:
                    listener.mouseMoved(event);
                    break;
                case MouseEvent.MOUSE_DRAGGED:
                    listener.mouseDragged(event);
                    break;
            }
        }

        synchronized (childLock) {
            for (Component child : component.children)
                _handleMouseEvent(child, event);
        }
    }

    public void _handleMouseWheelEvent(Component component, MouseWheelEvent event) {
        event.translatePoint(-component.getX(), -component.getY());

        for (MouseWheelListener listener : component.mouseWheelListeners) {
            switch (event.getID()) {
                case MouseEvent.MOUSE_WHEEL:
                    System.out.println("Listener event fired");
                    listener.mouseWheelMoved(event);
                    break;
            }
        }

        synchronized (childLock) {
            for (Component child : component.children)
                _handleMouseWheelEvent(child, event);
        }
    }

    public void _handleFocusEvent(Component component, FocusEvent event) {
        for (FocusListener listener : component.focusListeners) {
            switch (event.getID()) {
                case KeyEvent.KEY_PRESSED:
                    listener.focusGained(event);
                    break;
                case KeyEvent.KEY_RELEASED:
                    listener.focusLost(event);
                    break;
            }
        }

        synchronized (childLock) {
            for (Component child : component.children)
                _handleFocusEvent(child, event);
        }
    }

    public void dispatchEvent(AWTEvent event) {
    }

    public FontMetrics getFontMetrics(Font font) {
        return font.getFontMetrics();
    }

    public Component add(Component comp) {
        if (comp == null)
            return null;

        synchronized (childLock) {
            if (!children.contains(comp)) {
                children.add(comp);
                comp._setParent(this);
            }
        }
        System.out.println("Unimplemented method Component.add(" + comp + ") called");
        return comp;
    }

    public void remove(Component comp) {
        synchronized (childLock) {
            if (children.contains(comp))
                children.remove(comp);
        }
    }

    public void setDropTarget(DropTarget dt) {
        System.out.println("Unimplemented method Component.setDropTarget(" + dt + ")");
    }

    public void addComponentListener(ComponentListener listener) {
        if (!componentListeners.contains(listener)) {
            componentListeners.add(listener);
            // TODO: Check event id, etc. and if it's actually called here
            ComponentEvent e = new ComponentEvent(this, 0);
            listener.componentResized(e);
        }
    }

    public void removeComponentListener(ComponentListener listener) {
        if (componentListeners.contains(listener))
            componentListeners.remove(listener);
    }

    public void addKeyListener(KeyListener listener) {
        if (!keyListeners.contains(listener))
            keyListeners.add(listener);
    }

    public void removeKeyListener(KeyListener listener) {
        if (keyListeners.contains(listener))
            keyListeners.remove(listener);
    }

    public void addFocusListener(FocusListener listener) {
        if (!focusListeners.contains(listener))
            focusListeners.add(listener);
    }

    public void removeFocusListener(FocusListener listener) {
        if (focusListeners.contains(listener))
            focusListeners.remove(listener);
    }

    public void addMouseListener(MouseListener listener) {
        if (!mouseListeners.contains(listener))
            mouseListeners.add(listener);
    }

    public void removeMouseListener(MouseListener listener) {
        if (mouseListeners.contains(listener))
            mouseListeners.remove(listener);
    }

    public void addMouseMotionListener(MouseMotionListener listener) {
        if (!mouseMotionListeners.contains(listener))
            mouseMotionListeners.add(listener);
    }

    public void removeMouseMotionListener(MouseMotionListener listener) {
        if (mouseMotionListeners.contains(listener))
            mouseMotionListeners.remove(listener);
    }

    public void addMouseWheelListener(MouseWheelListener listener) {
        if (!mouseWheelListeners.contains(listener))
            mouseWheelListeners.add(listener);
    }

    public void removeMouseWheelListener(MouseWheelListener listener) {
        if (mouseWheelListeners.contains(listener))
            mouseWheelListeners.remove(listener);
    }

    public void setBackground(Color c) {
        backgroundColor = c;
        System.out.println("SET_BACKGROUND: " + c.getRGB());
    }

    public Color getBackground() {
        return backgroundColor;
    }

    public void setForeground(Color c) {
        foregroundColor = c;
    }

    public Color getForeground() {
        return foregroundColor;
    }

    public void setLocation(int x, int y) {
        locationPoint.x = x;
        locationPoint.y = y;
    }

    public void setFocusTraversalKeysEnabled(boolean focusTraversalKeysEnabled) {
        System.out.println("Unimplemented method Component.setFocusTraversalKeysEnabled(" + focusTraversalKeysEnabled + ") called");
    }

    public void setSize(int w, int h) {
        if (w == sizeDimension.width && h == sizeDimension.height)
            return;

        for (Component child : children) {
            // TODO: Handled by layout
            child.setSize(w, h);
        }

        sizeDimension.width = w;
        sizeDimension.height = h;
        graphics = getGraphics();
        if (graphics instanceof AndroidGraphics) {
            AndroidGraphics androidGraphics = (AndroidGraphics)graphics;
            androidGraphics.setSize(w, h);
        }

        for (final ComponentListener listener : componentListeners) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ComponentEvent e = new ComponentEvent(this, 0);
                    listener.componentResized(e);
                }
            }).start();
        }

        System.out.println("Unimplemented method Component.setSize(" + w + ", " + h + ") called");
    }

    public void setVisible(boolean show) {
        visible = show;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean hasFocus() {
        System.out.println("Unimplemented method Component.hasFocus() called");
        return true;
    }

    public void requestFocus() {
        System.out.println("Unimplemented method Component.requestFocus() called");
    }

    public void repaint() {
        // TODO: Look at this or some shit
        System.out.println("REPAINT");
        update(getGraphics());

        for (Component component : children)
            component.repaint();
    }

    public void paint(Graphics g) {
        if (backgroundColor != null) {
            Color prevColor = g.getColor();
            g.setColor(getBackground());
            g.fillRect(getX(), getY(), getWidth(), getHeight());
            g.setColor(prevColor);
        }
    }

    public void update(Graphics g) {
        paint(g);
    }

    public Graphics getGraphics() {
        if (graphics == null)
            graphics = new AndroidGraphics(this);
        return graphics;
    }

    public void setPreferredSize(Dimension preferredSize) {
        this.preferredSize.width = preferredSize.width;
        this.preferredSize.height = preferredSize.height;
    }

    public Dimension getPreferredSize() {
        return preferredSize;
    }

    public void setMaximumSize(Dimension maxSize) {
        System.out.println("Unimplemented method Component.setMaximumSize(" + maxSize.width + ", " + maxSize.height + ") called");
    }

    public Toolkit getToolkit() {
        return Toolkit.getDefaultToolkit();
    }

    public Image createImage(ImageProducer producer) {
        if (producer instanceof MemoryImageSource)
            return producer.createImage();

        BufferedImage image = new BufferedImage(getWidth(), getHeight());
        producer.addConsumer(image);
        return image;
    }

    public Image createImage(int width, int height) {
        return new BufferedImage(getWidth(), getHeight());
    }

    public boolean prepareImage(Image image, ImageObserver observer) {
        this.observer = observer;
        this.image = image;
        return true;
    }

    public Point getLocation() {
        return locationPoint;
    }

    public int getX() {
        return locationPoint.x;
    }

    public int getY() {
        return locationPoint.y;
    }

    public int getWidth() {
        if (getSize() == null)
            return 0;
        return getSize().width;
    }

    public int getHeight() {
        if (getSize() == null)
            return 0;
        return getSize().height;
    }

    public Dimension getSize() {
        return sizeDimension;
    }

    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
        if ((infoflags & (FRAMEBITS | ALLBITS)) != 0) {
            // TODO: This is nowhere near done
            //repaint();
            return true;
        }
        return false;
    }

    public boolean mouseUp(Event evt, int x, int y) {
        return false;
    }

    public boolean mouseDown(Event evt, int x, int y) {
        return false;
    }

    public boolean mouseMove(Event evt, int x, int y) {
        return false;
    }

    public boolean mouseDrag(Event evt, int x, int y) {
        return false;
    }

    public boolean keyDown(Event evt, int key) {
        return false;
    }

    public boolean keyUp(Event evt, int key) {
        return false;
    }
}
