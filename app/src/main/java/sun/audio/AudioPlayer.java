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

package sun.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import com.dalvikjvm.G711UCodec;

import java.io.InputStream;

public class AudioPlayer extends Thread {
    public static final AudioPlayer player = getAudioPlayer();
    private AudioTrack audioTrack;
    private InputStream stream;
    private int bufferSize;

    public final int BITRATE = 8000;

    public AudioPlayer() {
        bufferSize = AudioTrack.getMinBufferSize(
                BITRATE,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT
        );
        audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC, BITRATE,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM
        );
        start();
    }

    private static AudioPlayer getAudioPlayer() {
        AudioPlayer ret = new AudioPlayer();
        return ret;
    }

    public synchronized void start(InputStream in) {
        stream = in;
    }

    public void run() {
        byte[] audioBuffer = new byte[bufferSize];
        short[] decodeBuffer = new short[bufferSize];
        while (true) {
            try {
                if (stream != null) {
                    while (true) {
                        int len = stream.read(audioBuffer);
                        if (len > 0) {
                            G711UCodec.decode(decodeBuffer, audioBuffer, len, 0);
                            audioTrack.write(decodeBuffer, 0, len);

                            if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_STOPPED)
                                audioTrack.play();
                        } else {
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try { Thread.sleep(100); } catch (Exception e) {}
        }
    }
}
