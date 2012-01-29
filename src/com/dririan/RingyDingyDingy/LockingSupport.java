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

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class LockingSupport {
    private static LockingSupport _instance;

    private Context context;
    private DevicePolicyManager policyManager;
    private ComponentName deviceAdmin;

    public static LockingSupport getInstance(Context context) {
        if(_instance == null) {
            _instance = new LockingSupport();
            _instance.context = context;
            _instance.policyManager = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            _instance.deviceAdmin = new ComponentName(_instance.context, DeviceAdmin.class);
        }

        return _instance;
    }

    public Intent getActivationIntent() {
        if(!this.isActive()) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, this.deviceAdmin);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, this.context.getString(R.string.device_admin_prompt));
            return intent;
        }
        return null;
    }

    public boolean isActive() {
        return this.policyManager.isAdminActive(this.deviceAdmin);
    }

    public void lock() {
        if(this.isActive()) {
            long lockTime = policyManager.getMaximumTimeToLock(deviceAdmin);
            policyManager.setMaximumTimeToLock(deviceAdmin, 1);
            policyManager.lockNow();
            policyManager.setMaximumTimeToLock(deviceAdmin, lockTime);
        }
    }

    public void removeAdmin() {
        if(this.isActive()) {
            policyManager.removeActiveAdmin(deviceAdmin);
        }
    }

}
