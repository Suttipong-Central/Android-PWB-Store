package cenergy.central.com.pwb_store.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import org.jetbrains.annotations.NotNull;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.fragment.ProductListFragment;
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage;
import cenergy.central.com.pwb_store.view.LanguageButton;
import cenergy.central.com.pwb_store.view.NetworkStateView;

public class ProductListActivity extends BaseActivity {

    public static final String ARG_SEARCH = "ARG_SEARCH";
    public static final String ARG_PRODUCT_ID = "ARG_PRODUCT_ID";
    public static final String ARG_KEY_WORD = "ARG_KEY_WORD";

    private boolean isSearch;
    private LanguageButton languageButton;
    private NetworkStateView networkStateView;
    private String keyWord;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        languageButton = findViewById(R.id.switch_language_button);

        handleChangeLanguage();
        initView();

        Intent mIntent = getIntent();
        Bundle extras = mIntent.getExtras();
        if (extras != null) {
            isSearch = extras.getBoolean(ARG_SEARCH);
            keyWord = extras.getString(ARG_KEY_WORD);
        }
        startProductListFragment(keyWord, isSearch);
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
    public void onChangedLanguage(@NotNull AppLanguage lang) {
        super.onChangedLanguage(lang);
        startProductListFragment(keyWord, isSearch);
    }

    @Nullable
    @Override
    public LanguageButton getSwitchButton() {
        return languageButton;
    }

    private void initView() {
        networkStateView = findViewById(R.id.network_state_View);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void startProductListFragment(String keyWord, boolean isSearch) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction
                .replace(R.id.container, ProductListFragment.newInstance(keyWord, isSearch, "0" , "", null, keyWord))
                .commit();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_UPDATE_LANGUAGE);
        finish();
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public NetworkStateView getStateView() {
        return networkStateView;
    }
}
