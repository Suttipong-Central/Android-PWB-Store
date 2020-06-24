package cenergy.central.com.pwb_store.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.fragment.SearchSuggestionFragment;
import cenergy.central.com.pwb_store.manager.bus.event.BarcodeBus;
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage;
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager;
import cenergy.central.com.pwb_store.realm.RealmController;
import cenergy.central.com.pwb_store.utils.Analytics;
import cenergy.central.com.pwb_store.utils.Screen;
import cenergy.central.com.pwb_store.view.LanguageButton;
import cenergy.central.com.pwb_store.view.NetworkStateView;
import cenergy.central.com.pwb_store.view.ProductCompareView;

/**
 * Created by napabhat on 7/11/2017 AD.
 */

public class SearchActivity extends BaseActivity implements ProductCompareView.ProductCompareViewListener {
    public static final String TAG = SearchActivity.class.getSimpleName();

    private Analytics analytics;

    private LanguageButton languageButton;
    private PreferenceManager preferenceManager;
    private NetworkStateView networkStateView;
    private ProductCompareView productCompareView;

    @Subscribe
    public void onEvent(BarcodeBus barcodeBus){
        if (barcodeBus.isBarcode()){
            IntentIntegrator integrator = new IntentIntegrator(this).setCaptureActivity(BarcodeScanActivity.class);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            integrator.initiateScan();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        analytics = new Analytics(this);
        preferenceManager = new PreferenceManager(this);
        languageButton = findViewById(R.id.switch_language_button);
        productCompareView = findViewById(R.id.productCompareView);

        observeCompareProducts();
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

        mToolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (analytics != null) {
            analytics.trackScreen(Screen.SEARCH);
        }
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
                if (analytics != null) {
                    analytics.trackSearchByBarcode(result.getContents());
                }
                Intent intent = new Intent(SearchActivity.this, ProductDetailActivity.class);
                intent.putExtra(ProductDetailActivity.ARG_PRODUCT_ID, result.getContents());
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

    @Nullable
    @Override
    public ProductCompareView getProductCompareView() {
        productCompareView.addProductCompareViewListener(this);
        return productCompareView;
    }

    @Override
    public void resetCompareProducts() {
        RealmController.getInstance().deleteAllCompareProduct();
    }

    @Override
    public void openComparePage() {
        CompareActivity.Companion.startCompareActivity(this, productCompareView);
    }
}
