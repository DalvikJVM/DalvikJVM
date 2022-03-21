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

public interface DataLine extends Line {
    class Info extends Line.Info {
        private AudioFormat[] formats;
        private int minBufferSize;
        private int maxBufferSize;

        public Info(Class<?> lineClass, AudioFormat format) {
            super(lineClass);
            this.formats = new AudioFormat[1];
            this.formats[0] = format;
        }

        public Info(Class<?> lineClass, AudioFormat[] formats, int minBufferSize, int maxBufferSize) {
            super(lineClass);
            this.formats = formats;
            this.minBufferSize = minBufferSize;
            this.maxBufferSize = maxBufferSize;
        }

        public Info(Class<?> lineClass, AudioFormat format, int bufferSize) {
            this(lineClass, new AudioFormat[] { format }, bufferSize, bufferSize);
        }

        public AudioFormat[] getFormats() {
            return formats;
        }

        public int getMinBufferSize() {
            return minBufferSize;
        }

        public int getMaxBufferSize() {
            return maxBufferSize;
        }
    }

    void start();
    int available();
    void flush();
}
