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

public class AudioFormat {
    public static class Encoding {
        public static Encoding ULAW = new Encoding("ULAW");
        public static Encoding PCM_SIGNED = new Encoding("PCM_SIGNED");

        public Encoding(String name) {
        }
    }

    protected float sampleRate;
    protected int channels;
    protected int sampleSize;

    public AudioFormat(Encoding encoding, float sampleRate, int sampleSizeInBits, int channels, int frameSize, float frameRate, boolean bigEndian) {
        this.sampleRate = sampleRate;
        this.channels = channels;
        this.sampleSize = sampleSizeInBits;
    }

    public float getSampleRate() {
        return sampleRate;
    }

    public int getChannels() {
        return channels;
    }

    public int getSampleSizeInBits() {
        return sampleSize;
    }
}
