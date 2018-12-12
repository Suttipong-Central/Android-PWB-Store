package cenergy.central.com.pwb_store.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.fragment.LoginFragment;
import cenergy.central.com.pwb_store.manager.bus.event.LoginSuccessBus;
import cenergy.central.com.pwb_store.manager.network.NetworkReceiver;
import cenergy.central.com.pwb_store.view.NetworkStateView;

public class LoginActivity extends AppCompatActivity implements NetworkReceiver.NetworkStateLister {
    private static final String TAG = LoginActivity.class.getSimpleName();
    public static final String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    private NetworkReceiver onNetworkReceived = new NetworkReceiver(this);
    private NetworkStateView stateView;
    @Subscribe
    public void onEvent(LoginSuccessBus loginSuccessBus) {
        if (loginSuccessBus.isSuccess()) {
            Intent intent = new Intent(this, MainActivity.class);
            ActivityCompat.startActivity(this, intent,
                    ActivityOptionsCompat
                            .makeBasic()
                            .toBundle());
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFullScreen();
        setContentView(R.layout.activity_login);
        initView(savedInstanceState);
    }

    private void initView(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        stateView = findViewById(R.id.network_state_View);
        if (savedInstanceState == null) {
            //Load Fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, LoginFragment.newInstance())
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(onNetworkReceived, new IntentFilter(CONNECTIVITY_ACTION)); // register broadcast
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(onNetworkReceived); // unregister broadcast
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    public void onNetworkStateChange(@NonNull NetworkInfo.State state) {
        switch (state) {
            case CONNECTED:
                stateView.onConnected();
                break;
            case UNKNOWN:
            case CONNECTING:
                stateView.onConnecting();
                break;
            case DISCONNECTED:
            default:
                stateView.onDisconnected();
                break;
        }
    }

    public void initFullScreen() {
        //for new api versions.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

}
