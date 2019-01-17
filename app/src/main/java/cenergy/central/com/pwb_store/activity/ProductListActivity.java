package cenergy.central.com.pwb_store.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.jetbrains.annotations.NotNull;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.fragment.ProductListFragment;
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage;
import cenergy.central.com.pwb_store.view.LanguageButton;

public class ProductListActivity extends BaseActivity {

    public static final String ARG_SEARCH = "ARG_SEARCH";
    public static final String ARG_PRODUCT_ID = "ARG_PRODUCT_ID";
    public static final String ARG_KEY_WORD = "ARG_KEY_WORD";

    private boolean isSearch;
    private LanguageButton languageButton;
    private String keyWord;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        languageButton = findViewById(R.id.switch_language_button);

        handleChangeLanguage();

        Intent mIntent = getIntent();
        Bundle extras = mIntent.getExtras();
        if (extras != null) {
            isSearch = extras.getBoolean(ARG_SEARCH);
            keyWord = extras.getString(ARG_KEY_WORD);
        }

        initView();
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
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        if (isSearch){
            mToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_white));
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
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
}
