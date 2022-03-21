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

import android.content.Intent;
import android.net.Uri;
import com.dalvikjvm.MainActivity;

import java.net.URI;

public class Desktop {
    private static Desktop instance = new Desktop();

    public static class Action {
        public static final Action BROWSE = new Action();
    }

    public static boolean isDesktopSupported() {
        return true;
    }

    public boolean isSupported(Action action) {
        return true;
    }

    public void browse(URI location) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(location.toString()));
        MainActivity.instance.startActivity(browserIntent);
    }

    public static Desktop getDesktop() {
        return instance;
    }
}
