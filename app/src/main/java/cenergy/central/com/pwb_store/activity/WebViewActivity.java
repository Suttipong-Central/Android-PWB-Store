package cenergy.central.com.pwb_store.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import org.greenrobot.eventbus.EventBus;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.fragment.WebViewFragment;
import cenergy.central.com.pwb_store.manager.bus.event.WebViewBackBus;

public class WebViewActivity extends AppCompatActivity {
    public static final String ARG_WEB_URL = "ARG_WEB_URL";
    public static final String ARG_MODE = "ARG_MODE";
    public static final String ARG_TITLE = "ARG_TITLE";
    private static final String TAG = "WebViewActivity";
    private Toolbar mToolBar;

    //Data Members
    private String mWebUrl;
    private String mTitle;
    private int mMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        mToolBar = findViewById(R.id.toolbar);
        Intent mIntent = getIntent();
        Bundle extras = mIntent.getExtras();
        if (extras != null) {
            mWebUrl = extras.getString(ARG_WEB_URL);
            mMode = extras.getInt(ARG_MODE);
            mTitle = extras.getString(ARG_TITLE);
        }

        initView();

        if (savedInstanceState == null) {
            //Load webView Fragment
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction
                    .replace(R.id.container, WebViewFragment.newInstance(mWebUrl, mMode, mTitle))
                    .commit();

        }
    }

    private void initView() {
        setSupportActionBar(mToolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolBar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_WEB_URL, mWebUrl);
        outState.putInt(ARG_MODE, mMode);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mWebUrl = savedInstanceState.getString(ARG_WEB_URL);
        mMode = savedInstanceState.getInt(ARG_MODE);
        mTitle = savedInstanceState.getString(ARG_TITLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                switch (mMode) {
                    case WebViewFragment.MODE_HTML:
                    case WebViewFragment.MODE_URL:
                    case WebViewFragment.MODE_ADD_CARD:
                        break;
                    case WebViewFragment.MODE_PAYMENT:
                        setResult(Activity.RESULT_CANCELED);
                        break;
                }
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        switch (mMode) {
            case WebViewFragment.MODE_HTML:
            case WebViewFragment.MODE_URL:
            case WebViewFragment.MODE_ADD_CARD:
                break;
            case WebViewFragment.MODE_PAYMENT:
                setResult(Activity.RESULT_CANCELED);
                break;
        }
        EventBus.getDefault().post(new WebViewBackBus(true));
        super.onBackPressed();
    }

}
