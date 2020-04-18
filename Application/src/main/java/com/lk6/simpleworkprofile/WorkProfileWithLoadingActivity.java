package com.lk6.simpleworkprofile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class WorkProfileWithLoadingActivity extends AppCompatActivity {

    private static final int REQUEST_PROVISION_MANAGED_PROFILE = 1;
    private boolean TRY_AGAIN = true;
    private final static String TAG = "LoadingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_profile_with_loading);

        Log.e(TAG, "onCreate");

        startWorKProfile();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.e(TAG, "onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }

    private void startWorKProfile() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    123);
        } else {

            Intent intent = new Intent(DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE);

            // Use a different intent extra below M to configure the admin component.
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                intent.putExtra(
                        DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME,
                        getApplicationContext().getPackageName()
                );
            } else {
                final ComponentName component = new ComponentName(this,
                        BasicDeviceAdminReceiver.class.getName());
                intent.putExtra(
                        DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME,
                        component
                );
            }

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_PROVISION_MANAGED_PROFILE);
            } else {
                Toast.makeText(this, "Device provisioning is not enabled. Stopping.",
                        Toast.LENGTH_SHORT).show();
            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PROVISION_MANAGED_PROFILE) {
            if (resultCode == Activity.RESULT_OK) {
//                Toast.makeText(this, "Provisioning done.", Toast.LENGTH_SHORT).show();
                Log.e("LoadingActivity", "provisioning done");

            } else {
//                Toast.makeText(this, "Provisioning failed.", Toast.LENGTH_SHORT).show();
                Log.e("LoadingActivity", "provisioning failed");
                if(TRY_AGAIN) {
                    startWorKProfile();
                    TRY_AGAIN = false;
                } else {
                    TRY_AGAIN = true;
                    return;
                }
            }
            return;
        }
    }
}
