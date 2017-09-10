package cenergy.central.com.pwb_store.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.fragment.WebViewFragment;
import cenergy.central.com.pwb_store.manager.bus.event.WebViewBackBus;

public class WebViewActivity extends AppCompatActivity {
    public static final String ARG_WEB_URL = "ARG_WEB_URL";
    //public static final String ARG_TYPE = "ARG_TYPE";
    public static final String ARG_MODE = "ARG_MODE";
    public static final String ARG_TITLE = "ARG_TITLE";
    public static final int REQUEST_CODE_PAYMENT = 98;
    public static final int REQUEST_CODE_ADD_CARD = 99;
    private static final String TAG = "WebViewActivity";
    //View Members
    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    //Data Members
    private String mWebUrl;
    private String mTitle;
    //private boolean isType;
    private int mMode;

//    @Subscribe
//    public void onEvent(WebViewTitleBus webViewTitleBus) {
//        mTextViewPageTitle.setText(webViewTitleBus.getWebTitle());
//        mTextViewUrl.setText(webViewTitleBus.getWebUrl());
//    }
//
//    @Subscribe
//    public void onEvent(WebViewEndedBus webViewEndedBus) {
//        if (webViewEndedBus.isWebViewEnded()) {
//            super.onBackPressed();
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

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
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_WEB_URL, mWebUrl);
        outState.putInt(ARG_MODE, mMode);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mWebUrl = savedInstanceState.getString(ARG_WEB_URL);
        mMode = savedInstanceState.getInt(ARG_MODE);
        mTitle = savedInstanceState.getString(ARG_TITLE);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        EventBus.getDefault().register(this);
//    }
//
//    @Override
//    protected void onPause() {
//        EventBus.getDefault().unregister(this);
//        super.onPause();
//    }

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
