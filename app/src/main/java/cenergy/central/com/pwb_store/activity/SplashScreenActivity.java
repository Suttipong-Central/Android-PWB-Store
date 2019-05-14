package cenergy.central.com.pwb_store.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;

import activity.LoginActivity;
import cenergy.central.com.pwb_store.realm.RealmController;

public class SplashScreenActivity extends AppCompatActivity {
    private RealmController database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = RealmController.getInstance();
        initView();
    }

    private void initView() {
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
