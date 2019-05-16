package cenergy.central.com.pwb_store.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import cenergy.central.com.pwb_store.BuildConfig;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.fragment.SearchSuggestionFragment;
import cenergy.central.com.pwb_store.manager.bus.event.BarcodeBus;
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage;
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager;
import cenergy.central.com.pwb_store.view.LanguageButton;
import cenergy.central.com.pwb_store.view.NetworkStateView;

/**
 * Created by napabhat on 7/11/2017 AD.
 */

public class SearchActivity extends BaseActivity {
    public static final String TAG = SearchActivity.class.getSimpleName();

    private LanguageButton languageButton;
    private PreferenceManager preferenceManager;
    private NetworkStateView networkStateView;

    @Subscribe
    public void onEvent(BarcodeBus barcodeBus){
        if (barcodeBus.isBarcode() == true){
            IntentIntegrator integrator = new IntentIntegrator(this).setCaptureActivity(BarcodeScanActivity.class);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            integrator.initiateScan();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        preferenceManager = new PreferenceManager(this);
        languageButton = findViewById(R.id.switch_language_button);

        handleChangeLanguage();
        initView();
        //TODO ยังไม่มี Suggestion รอ API
        startSearchSuggestion();
    }

    private void startSearchSuggestion() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction
                .replace(R.id.container, SearchSuggestionFragment.newInstance())
                .commit();
    }

    private void initView() {
        networkStateView = findViewById(R.id.network_state_View);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                //TODO แก้Barcode
                Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                Log.d(TAG, "barcode : " + result.getContents());
                Intent intent = new Intent(SearchActivity.this, ProductDetailActivity.class);
                intent.putExtra(ProductDetailActivity.ARG_PRODUCT_ID, result.getContents());
                intent.putExtra(ProductDetailActivity.ARG_IS_BARCODE, true);
                startActivityForResult(intent, REQUEST_UPDATE_LANGUAGE);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

        if (resultCode == BaseActivity.RESULT_UPDATE_LANGUAGE) {
                if (getSwitchButton() != null) {
                    getSwitchButton().setDefaultLanguage(preferenceManager.getDefaultLanguage());
                }
        }
    }

    @Override
    public void onChangedLanguage(@NotNull AppLanguage lang) {
        super.onChangedLanguage(lang);
        startSearchSuggestion();
    }

    @Nullable
    @Override
    public LanguageButton getSwitchButton() {
        return languageButton;
    }

    @Nullable
    @Override
    public NetworkStateView getStateView() {
        return networkStateView;
    }
}
