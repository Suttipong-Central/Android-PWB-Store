package cenergy.central.com.pwb_store.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.fragment.LoginFragment;
import cenergy.central.com.pwb_store.manager.bus.event.LoginSuccessBus;
import cenergy.central.com.pwb_store.manager.network.NetworkReceiver;
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage;
import cenergy.central.com.pwb_store.view.LanguageButton;
import cenergy.central.com.pwb_store.view.NetworkStateView;

public class LoginActivity extends BaseActivity implements NetworkReceiver.NetworkStateLister {
    private static final String TAG = LoginActivity.class.getSimpleName();
    public static final String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    private NetworkReceiver onNetworkReceived = new NetworkReceiver(this);
    private NetworkStateView stateView;
    private LanguageButton languageButton;

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
        setContentView(R.layout.activity_login);
        languageButton = findViewById(R.id.switch_language_button);
        handleChangeLanguage();
        initView();
    }

    private void initView() {
        stateView = findViewById(R.id.network_state_View);
        //Load Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, LoginFragment.newInstance())
                .commit();
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

    @Nullable
    @Override
    public LanguageButton getSwitchButton() {
        return languageButton;
    }

    @Override
    public void onChangedLanguage(@NotNull AppLanguage lang) {
        Log.d("Change Language", lang.toString());
        super.onChangedLanguage(lang);
        // recreate view
        initView();
    }
}