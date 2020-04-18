/*
 * Copyright (C) 2014 The Android Open Source Project
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

import android.app.Activity;
import android.app.admin.DeviceAdminReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;


/**
 * Handles events related to managed profile.
 */
public class BasicDeviceAdminReceiver extends DeviceAdminReceiver {

    private final static String TAG = "RECEIVER";

    /**
     * Called on the new profile when managed profile provisioning has completed. Managed profile
     * provisioning is the process of setting up the device so that it has a separate profile which
     * is managed by the mobile device management(mdm) application that triggered the provisioning.
     * Note that the managed profile is not fully visible until it is enabled.
     */
    @Override
    public void onProfileProvisioningComplete(@NonNull Context context, @NonNull Intent intent) {

        final PostProvisioningHelper helper = new PostProvisioningHelper(context);
        helper.completeProvisioning();

        Log.e(TAG, "onProfileProvisioningComplete");

        if (helper.isDone()) {
            Log.e(TAG, "onProfileProvisioningComplete helper is done");

            try {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String imei = telephonyManager.getImei();
                Toast.makeText(context, "IMEI FROM WORK PROFILE : "+imei, Toast.LENGTH_LONG).show();
                Log.e("IMEI", "IMEI: "+imei);
            } catch (Exception e ){

            }

            helper.removeProfile();

        }
    }


    /**
     * Generates a {@link ComponentName} that is used throughout the app.
     *
     * @return a {@link ComponentName}
     */
    public static ComponentName getComponentName(Context context) {
        return new ComponentName(context.getApplicationContext(), BasicDeviceAdminReceiver.class);
    }

}
