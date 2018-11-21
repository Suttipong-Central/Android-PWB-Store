package cenergy.central.com.pwb_store.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.fragment.ProductListFragment;
import cenergy.central.com.pwb_store.view.PowerBuyShoppingCartView;

/**
 * Created by napabhat on 7/6/2017 AD.
 */

public class ProductListActivity extends AppCompatActivity {

    public static final String ARG_SEARCH = "ARG_SEARCH";
    public static final String ARG_PRODUCT_ID = "ARG_PRODUCT_ID";
    public static final String ARG_KEY_WORD = "ARG_KEY_WORD";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private ImageView searchButton;
    private PowerBuyShoppingCartView cartButton;

    private boolean isSearch;
    private String productId;
    private String keyWord;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        initView();

        Intent mIntent = getIntent();
        Bundle extras = mIntent.getExtras();
        if (extras != null) {
            isSearch = extras.getBoolean(ARG_SEARCH);
            productId = extras.getString(ARG_PRODUCT_ID);
            keyWord = extras.getString(ARG_KEY_WORD);
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction
                .replace(R.id.container, ProductListFragment.newInstance(keyWord, isSearch, "0" , "", null, keyWord))
                .commit();
    }

    private void initView() {
        ButterKnife.bind(this);
        searchButton = findViewById(R.id.search_button);
        cartButton = findViewById(R.id.shopping_cart_compare);
        searchButton.setVisibility(View.GONE);
        cartButton.setVisibility(View.GONE);
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
}
