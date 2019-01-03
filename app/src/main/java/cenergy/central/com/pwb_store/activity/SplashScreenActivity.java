package cenergy.central.com.pwb_store.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.UserInfoManager;
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager;
import cenergy.central.com.pwb_store.realm.RealmController;
import cenergy.central.com.pwb_store.realm.seeder.DistrictSeeder;
import cenergy.central.com.pwb_store.realm.seeder.PostcodeSeeder;
import cenergy.central.com.pwb_store.realm.seeder.ProvinceSeeder;
import cenergy.central.com.pwb_store.realm.seeder.SubDistrictSeeder;

/**
 * Created by napabhat on 9/21/2017 AD.
 */

public class SplashScreenActivity extends AppCompatActivity {
    private static final String TAG = SplashScreenActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 999;

    private TelephonyManager mTelephonyManager;
    private RealmController database = RealmController.getInstance();
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(this);
        initView();
    }

    private void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                        PERMISSIONS_REQUEST_READ_PHONE_STATE);
            } else {
                forward();
            }
        } else {
            forward();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            forward();
        }
    }

    private void forward() {
        Boolean hasProvinces = database.getProvinces() != null;
        Boolean hasDistricts = database.getDistricts() != null;
        Boolean hasSubDistricts = database.getSubDistricts() != null;
        Boolean hasPostcode = database.getSubDistricts() != null;
        if (preferenceManager.isAddressLoaded() && hasProvinces && hasDistricts && hasSubDistricts && hasPostcode) {
            start();
        } else {
            storeAddressRawJson();
        }
    }

    private void storeAddressRawJson() {
        // clear database
        database.deleteProvinces();
        database.deleteDistricts();
        database.deleteSubDistricts();
        database.deletePostcodes();

        // seeder
        new ProvinceSeeder(this, database, R.raw.seed_province).seed();
        new DistrictSeeder(this, database, R.raw.seed_district).seed();
        new SubDistrictSeeder(this, database, R.raw.seed_sub_district).seed();
        new PostcodeSeeder(this, database, R.raw.seed_postcode).seed();

        preferenceManager.setAddressLoaded(true);

        start();
    }

    private void start() {
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String deviceId = mTelephonyManager.getDeviceId();
        Log.d(TAG, "DeviceImei " + deviceId);

        if (database.getUserToken() != null) {
            // start main page
            Intent intent = new Intent(this, MainActivity.class);
            ActivityCompat.startActivity(this, intent,
                    ActivityOptionsCompat
                            .makeBasic()
                            .toBundle());
        } else {
            // start login page
            UserInfoManager.getInstance().setKeyImei(deviceId);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        finish();
    }
}
