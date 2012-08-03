/*
 * This file is part of RingyDingyDingy.
 * Copyright (C) 2011-2012 Ayron Jungren
 *
 * RingyDingyDingy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License only.
 *
 * RingyDingyDingy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with RingyDingyDingy.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dririan.RingyDingyDingy;

import android.content.Context;
import android.os.Build;

public class LogEntry {
    public Context context;
    public long id;
    public String app = null;
    public String argument = null;
    public String command;
    public String source = null;
    public String timestamp;

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        if(command.compareToIgnoreCase("ring") == 0 && argument != null) {
            stringBuilder.append("Page sent");
        }
        else {
            stringBuilder.append("Command ");
            stringBuilder.append(this.command);
            stringBuilder.append(" used");
        }

        if(this.source != null) {
            stringBuilder.append(" by ");

            // Get the contact name, if available
            boolean addedContactName = false;
            if(Build.VERSION.SDK_INT >= 5) {
                String[] contact = ContactSupport.lookupByNumber(context, source);
                if(contact[0] != null) {
                    addedContactName = true;
                    stringBuilder.append(contact[0]);
                }
            }

            if(!addedContactName)
                stringBuilder.append(source);
        }

        if(this.app != null) {
            stringBuilder.append(" using ");
            stringBuilder.append(app);
        }

        stringBuilder.append(" at ");
        stringBuilder.append(timestamp);

        if(command.compareToIgnoreCase("ring") == 0 && argument != null) {
            stringBuilder.append(": ");
            stringBuilder.append(argument);
        }

        return stringBuilder.toString();
    }
}
