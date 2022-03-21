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

package java.android.net;

import java.net.Inet4Address;
import java.net.InetAddress;

public class DetourInetAddress {
    public static InetAddress getByName(String name) {
        try {
            InetAddress[] addr = InetAddress.getAllByName(name);
            for (int i = 0; i < addr.length; i++) {
                if (addr[i] instanceof Inet4Address)
                    return addr[i];
            }

            return addr[0];
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
