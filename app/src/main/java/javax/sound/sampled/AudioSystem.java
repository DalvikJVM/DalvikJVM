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

package javax.sound.sampled;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import sun.audio.AudioPlayer;

import java.android.sound.AndroidClip;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class AudioSystem {
    public static Clip getClip() {
        return new AndroidClip();
    }

    public static AudioInputStream getAudioInputStream(InputStream inputStream) {
        return new AudioInputStream(inputStream);
    }

    public static AudioInputStream getAudioInputStream(File file) {
        AudioInputStream audioInputStream = null;
        try {
            audioInputStream = getAudioInputStream(new FileInputStream(file));
        } catch (Exception e) {
        }
        return audioInputStream;
    }

    public static Line getLine(Line.Info info) throws LineUnavailableException {
        if (info instanceof DataLine.Info) {
            final DataLine.Info dataInfo = (DataLine.Info)info;
            final javax.sound.sampled.AudioFormat format = dataInfo.getFormats()[0];

            return new SourceDataLine() {
                byte[] data;
                byte[] offData;
                int dataOffset;

                AudioTrack audioTrack;
                Thread thread;
                Object lock = new Object();
                boolean runThread;
                boolean stopAudio;
                boolean flushThread;

                final int OFFBUFFER_SIZE = 4096;
                final int SLEEP_TIME_MS = 5;

                @Override
                public void start() {
                    dataOffset = 0;
                    runThread = true;
                    thread.start();
                }

                @Override
                public int available() {
                    int ret;
                    synchronized (lock) {
                        ret = data.length - (dataOffset + 1);
                    }
                    return ret;
                }

                @Override
                public void open() throws LineUnavailableException {
                    if (audioTrack != null)
                        return;

                    audioTrack = new AudioTrack(
                            AudioManager.STREAM_MUSIC, (int)format.getSampleRate(),
                            format.getChannels() == 2?AudioFormat.CHANNEL_CONFIGURATION_STEREO:AudioFormat.CHANNEL_CONFIGURATION_MONO,
                            AudioFormat.ENCODING_PCM_16BIT,
                            dataInfo.getMinBufferSize(),
                            AudioTrack.MODE_STREAM
                    );

                    data = new byte[dataInfo.getMinBufferSize()];
                    offData = new byte[OFFBUFFER_SIZE];

                    thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (runThread) {
                                long start = System.currentTimeMillis();

                                if (stopAudio) {
                                    audioTrack.stop();
                                    audioTrack.flush();
                                    stopAudio = false;
                                }

                                int safeDataOffset;
                                synchronized (lock) {
                                    safeDataOffset = dataOffset;
                                }

                                if (safeDataOffset > 0) {
                                    int writeBytes = 0;
                                    synchronized (lock) {
                                        writeBytes = Math.min(offData.length, dataOffset);
                                        System.arraycopy(data, 0, offData, 0, writeBytes);
                                        System.arraycopy(data, writeBytes, data, 0, dataOffset - writeBytes);
                                        dataOffset -= writeBytes;
                                    }

                                    if (writeBytes > 0) {
                                        int writeLength = audioTrack.write(offData, 0, writeBytes);
                                        if (writeLength > 0) {
                                            if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_STOPPED)
                                                audioTrack.play();
                                        }
                                    }
                                }

                                if (flushThread) {
                                    audioTrack.flush();
                                    flushThread = false;
                                }

                                long elapsed = System.currentTimeMillis() - start;
                                long sleepTime = SLEEP_TIME_MS - elapsed;
                                if (sleepTime > 0)
                                    try { Thread.sleep(sleepTime); } catch (Exception e) {}
                            }
                        }
                    });
                }

                @Override
                public void close() throws Exception {
                    stopAudio = true;
                    runThread = false;
                }

                @Override
                public void flush() {
                    synchronized (lock) {
                        dataOffset = 0;
                    }
                    flushThread = true;
                }

                @Override
                public int write(byte[] b, int off, int len) {
                    synchronized (lock) {
                        len = Math.min(len, available());
                        System.arraycopy(b, off, data, dataOffset, len);
                        dataOffset += len;
                    }
                    return len;
                }
            };
        }

        return null;
    }
}
