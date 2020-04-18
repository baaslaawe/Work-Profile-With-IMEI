/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lk6.simpleworkprofile;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Build;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import static android.app.admin.DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED;

class PostProvisioningHelper {

    private static final String PREFS = "post-provisioning";
    private static final String PREF_DONE = "done";
    private static final String TAG = "HELPER";

    private final Context mContext;
    private final DevicePolicyManager mDevicePolicyManager;
    private final SharedPreferences mSharedPrefs;


    PostProvisioningHelper(@NonNull Context context) {
        mContext = context;
        mDevicePolicyManager =
                (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mSharedPrefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void completeProvisioning() {

        Log.e(TAG, "onCompleteProv start");

        if (isDone()) {
            Log.e(TAG, "onCompleteProv stop because is DONE");
            return;
        }

        markPostProvisioningDone();

        Log.e(TAG, "onCompleteProv start begin");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            autoGrantRequestedPermissionsToSelf();
        }

        ComponentName componentName = BasicDeviceAdminReceiver.getComponentName(mContext);
        // This is the name for the newly created managed profile.
        mDevicePolicyManager.setProfileName(
                componentName,
                mContext.getString(R.string.profile_name)
        );
        // We enable the profile here.
        mDevicePolicyManager.setProfileEnabled(componentName);


    }

    private void markPostProvisioningDone() {
        mSharedPrefs.edit().putBoolean(PREF_DONE, true).commit();
    }

    public boolean isDone() {
        return mSharedPrefs.getBoolean(PREF_DONE, false);
    }

    private void autoGrantRequestedPermissionsToSelf() {
        String packageName = mContext.getPackageName();
        ComponentName adminComponentName = BasicDeviceAdminReceiver.getComponentName(mContext);

        List<String> permissions = getRuntimePermissions(mContext.getPackageManager(), packageName);
        for (String permission : permissions) {
            boolean success = mDevicePolicyManager.setPermissionGrantState(adminComponentName,
                    packageName, permission, PERMISSION_GRANT_STATE_GRANTED);
            Log.d(TAG, "Auto-granting " + permission + ", success: " + success);
            if (!success) {
                Log.e(TAG, "Failed to auto grant permission to self: " + permission);
            } else {
            }
        }
    }

    private List<String> getRuntimePermissions(PackageManager packageManager, String packageName) {
        List<String> permissions = new ArrayList<>();
        PackageInfo packageInfo;
        try {
            packageInfo =
                    packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Could not retrieve info about the package: " + packageName, e);
            return permissions;
        }

        if (packageInfo != null && packageInfo.requestedPermissions != null) {
            for (String requestedPerm : packageInfo.requestedPermissions) {
                if (isRuntimePermission(packageManager, requestedPerm)) {
                    permissions.add(requestedPerm);
                }
            }
        }
        return permissions;
    }

    private boolean isRuntimePermission(PackageManager packageManager, String permission) {
        try {
            PermissionInfo pInfo = packageManager.getPermissionInfo(permission, 0);
            if (pInfo != null) {
                if ((pInfo.protectionLevel & PermissionInfo.PROTECTION_MASK_BASE)
                        == PermissionInfo.PROTECTION_DANGEROUS) {
                    return true;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.i(TAG, "Could not retrieve info about the permission: " + permission);
        }
        return false;
    }

    public void removeProfile() {
        if (null == mContext ) {
            return;
        }
        DevicePolicyManager manager =
                (DevicePolicyManager) mContext.getSystemService(Context.DEVICE_POLICY_SERVICE);
        manager.wipeData(0);
        // The screen turns off here
    }

}
