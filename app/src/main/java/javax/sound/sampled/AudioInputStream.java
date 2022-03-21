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

import java.io.IOException;
import java.io.InputStream;

public class AudioInputStream extends InputStream {
    AudioFormat audioFormat;
    InputStream stream;

    public AudioInputStream(InputStream stream) {
        this.stream = stream;
        this.audioFormat = new AudioFormat(44100, 16, 2, true, true);
        System.out.println("Unimplemented method AudioInputStream.<init>(" + stream + ") called");
    }

    @Override
    public int read() throws IOException {
        System.out.println("Unimplemented method AudioInputStream.read() called");
        return stream.read();
    }

    public AudioFormat getFormat() {
        return audioFormat;
    }
}
