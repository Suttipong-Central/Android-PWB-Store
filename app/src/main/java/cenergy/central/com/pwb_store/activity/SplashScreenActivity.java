package cenergy.central.com.pwb_store.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager;
import cenergy.central.com.pwb_store.realm.RealmController;
import cenergy.central.com.pwb_store.realm.seeder.DistrictSeeder;
import cenergy.central.com.pwb_store.realm.seeder.PostcodeSeeder;
import cenergy.central.com.pwb_store.realm.seeder.ProvinceSeeder;
import cenergy.central.com.pwb_store.realm.seeder.SubDistrictSeeder;

public class SplashScreenActivity extends AppCompatActivity {
    private RealmController database = RealmController.getInstance();
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(this);
        initView();
    }

    private void initView() {
        forward();
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
        if (database.getUserToken() != null) {
            // start main page
            Intent intent = new Intent(this, MainActivity.class);
            ActivityCompat.startActivity(this, intent,
                    ActivityOptionsCompat
                            .makeBasic()
                            .toBundle());
        } else {
            // start login page
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        finish();
    }
}
