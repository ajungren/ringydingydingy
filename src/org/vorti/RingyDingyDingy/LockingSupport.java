package org.vorti.RingyDingyDingy;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class LockingSupport {

    private static LockingSupport _instance;
    private Context context;
    private DevicePolicyManager policyManager;
    ComponentName deviceAdmin;

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
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, Resources.getString(R.string.device_admin_prompt, this.context));
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

