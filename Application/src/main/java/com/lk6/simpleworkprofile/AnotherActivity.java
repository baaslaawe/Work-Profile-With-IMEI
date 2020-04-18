package com.lk6.simpleworkprofile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class AnotherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another);

        final PostProvisioningHelper helper = new PostProvisioningHelper(this);
        helper.completeProvisioning();

        try {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String imei = telephonyManager.getImei();
            Toast.makeText(this, "IMEI FROM WORK PROFILE : "+imei, Toast.LENGTH_LONG).show();
            Log.e("IMEI", "IMEI: "+imei);
        } catch (Exception e ){

        }

        helper.removeProfile();

    }
}
