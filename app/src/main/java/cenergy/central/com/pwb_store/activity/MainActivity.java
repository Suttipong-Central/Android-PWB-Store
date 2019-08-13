package cenergy.central.com.pwb_store.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import cenergy.central.com.pwb_store.CategoryUtils;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.CategoryAdapter;
import cenergy.central.com.pwb_store.adapter.DrawerAdapter;
import cenergy.central.com.pwb_store.adapter.interfaces.MenuDrawerClickListener;
import cenergy.central.com.pwb_store.extensions.ConvertersKt;
import cenergy.central.com.pwb_store.fragment.CategoryFragment;
import cenergy.central.com.pwb_store.fragment.ProductListFragment;
import cenergy.central.com.pwb_store.fragment.SubHeaderProductFragment;
import cenergy.central.com.pwb_store.manager.ApiResponseCallback;
import cenergy.central.com.pwb_store.manager.HttpManagerMagento;
import cenergy.central.com.pwb_store.manager.bus.event.BarcodeBus;
import cenergy.central.com.pwb_store.manager.bus.event.CategoryTwoBus;
import cenergy.central.com.pwb_store.manager.bus.event.CompareMenuBus;
import cenergy.central.com.pwb_store.manager.bus.event.DrawItemBus;
import cenergy.central.com.pwb_store.manager.bus.event.HomeBus;
import cenergy.central.com.pwb_store.manager.bus.event.ProductFilterHeaderBus;
import cenergy.central.com.pwb_store.manager.bus.event.ProductFilterSubHeaderBus;
import cenergy.central.com.pwb_store.manager.bus.event.SearchEventBus;
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage;
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager;
import cenergy.central.com.pwb_store.model.APIError;
import cenergy.central.com.pwb_store.model.Category;
import cenergy.central.com.pwb_store.model.CategoryDao;
import cenergy.central.com.pwb_store.model.DrawerDao;
import cenergy.central.com.pwb_store.model.DrawerItem;
import cenergy.central.com.pwb_store.model.ProductFilterHeader;
import cenergy.central.com.pwb_store.model.ProductFilterSubHeader;
import cenergy.central.com.pwb_store.model.StoreDao;
import cenergy.central.com.pwb_store.realm.RealmController;
import cenergy.central.com.pwb_store.utils.DialogUtils;
import cenergy.central.com.pwb_store.view.LanguageButton;
import cenergy.central.com.pwb_store.view.NetworkStateView;

public class MainActivity extends BaseActivity implements MenuDrawerClickListener,
        CategoryAdapter.CategoryAdapterListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String ARG_CATEGORY = "ARG_CATEGORY";
    private static final String ARG_DRAWER_LIST = "ARG_DRAWER_LIST";
    private static final String ARG_STORE_ID = "ARG_STORE_ID";
    // new arg
    private static final String ARG_FILTER_CATEGORY_1 = "arg_filter_category_1";
    private static final String ARG_FILTER_CATEGORY_2 = "arg_filter_category_2";

    private static final String TAG_FRAGMENT_CATEGORY_DEFAULT = "category_default";
    private static final String TAG_FRAGMENT_SUB_HEADER = "category_sub_header";
    private static final String TAG_FRAGMENT_PRODUCT_LIST = "product_list";
    private static final int TIME_TO_WAIT = 2000;

    //private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 999;

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerAdapter mAdapter;
    private GridLayoutManager mLayoutManager;
    private ArrayList<DrawerItem> mDrawerItemList = new ArrayList<>();
    private StoreDao mStoreDao;
    private DrawerDao mDrawerDao;
    private CategoryDao mCategoryDao;
    private String storeId;
    private ProgressDialog mProgressDialog;
    private LanguageButton languageButton;
    private NetworkStateView networkStateView;

    public static Handler handler = new Handler();
    private RealmController database = RealmController.getInstance();

    private ProductFilterHeader productFilterHeader;
    private Category categoryLv1;
    private ProductFilterSubHeader productFilterSubHeader;
    private Category categoryLv2;
    private Fragment currentFragment;

    @Subscribe
    public void onEvent(DrawItemBus drawItemBus) {
        DrawerItem drawerItem = drawItemBus.getDrawerItem();
        this.categoryLv1 = drawerItem.getCategory();
        handleStartCategoryLv1();

//        if (this.productFilterHeader.getProductFilterSubHeaders().isEmpty()){
//            startProductListFragment(this.productFilterHeader);
//        } else {
//            startCategoryLvTwoFragment(this.productFilterHeader);
//        }
    }

    @Subscribe
    public void onEvent(HomeBus homeBus) {
        startCategoryFragment();
    }

    // Event from onClick back button in product list
    @Subscribe
    public void onEvent(CategoryTwoBus categoryTwoBus) {
//        startCategoryLvTwoFragment(this.categoryLv1);
//        if (this.productFilterHeader.getProductFilterSubHeaders().isEmpty()){
//            startCategoryFragment();
//        } else {
//            startCategoryLvTwoFragment(this.productFilterHeader);
//        }
        onBackPressed();
    }

    // Event from onClick category item
    @Subscribe
    public void onEvent(ProductFilterHeaderBus productFilterHeaderBus) {
//        this.productFilterHeader = productFilterHeaderBus.getProductFilterHeader();
//        if (this.productFilterHeader.getProductFilterSubHeaders().isEmpty()){
//            startProductListFragment(this.productFilterHeader);
//        } else {
//            startCategoryLvTwoFragment(this.productFilterHeader);
//        }
    }

    // Event from onClick product filter sub header item
    @Subscribe
    public void onEvent(ProductFilterSubHeaderBus productFilterSubHeaderBus) {
//        if (productFilterSubHeaderBus.getProductFilterSubHeader().getName().equalsIgnoreCase("Change Language to Thai")) {
//            startCategoryFragment();
//        } else if (productFilterSubHeaderBus.getProductFilterSubHeader().getName().equalsIgnoreCase("Compare")) {
//            startCategoryFragment();
//        } else {
//            this.productFilterSubHeader = productFilterSubHeaderBus.getProductFilterSubHeader();
//            startProductListFragment(this.productFilterSubHeader);
//        }
    }

    @Subscribe
    public void onEvent(BarcodeBus barcodeBus) {
        if (barcodeBus.isBarcode()) {
            IntentIntegrator integrator = new IntentIntegrator(MainActivity.this).setCaptureActivity(BarcodeScanActivity.class);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            integrator.initiateScan();
        }
    }

    @Subscribe
    public void onEvent(SearchEventBus searchEventBus) {
        if (searchEventBus.getKeyword().length() > 0) {
            Intent intent = new Intent(this, ProductListActivity.class);
            intent.putExtra(ProductListActivity.ARG_KEY_WORD, searchEventBus.getKeyword());
            intent.putExtra(ProductListActivity.ARG_SEARCH, searchEventBus.isClick());
            startActivityForResult(intent, REQUEST_UPDATE_LANGUAGE);
        }
    }

    @Subscribe
    public void onEvent(CompareMenuBus compareMenuBus) {
        if (database.getCompareProducts().size() > 0 ){
            Intent intent = new Intent(this, CompareActivity.class);
            ActivityCompat.startActivityForResult(this, intent,
                    REQUEST_UPDATE_LANGUAGE, ActivityOptionsCompat
                            .makeScaleUpAnimation(compareMenuBus.getView(), 0, 0,
                                    compareMenuBus.getView().getWidth(), compareMenuBus.getView().getHeight())
                            .toBundle());
        } else {
            showAlertDialog("", getString(R.string.please_add_compare));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        languageButton = findViewById(R.id.switch_language_button);

        handleChangeLanguage();
        initView();
        retrieveCategories();
    }

    private void initView() {
        networkStateView = findViewById(R.id.network_state_View);
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        RecyclerView recyclerViewMenu = findViewById(R.id.recycler_view_menu);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mAdapter = new DrawerAdapter(this);
        mLayoutManager = new GridLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false);
        recyclerViewMenu.setLayoutManager(mLayoutManager);
        recyclerViewMenu.setAdapter(mAdapter);

        mDrawerToggle = new ActionBarDrawerToggle(
                MainActivity.this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtils.createProgressDialog(this);
            mProgressDialog.show();
        } else {
            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_FILTER_CATEGORY_1, productFilterHeader);
        outState.putParcelable(ARG_FILTER_CATEGORY_2, productFilterSubHeader);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        productFilterHeader = savedInstanceState.getParcelable(ARG_FILTER_CATEGORY_1);
        productFilterSubHeader = savedInstanceState.getParcelable(ARG_FILTER_CATEGORY_2);
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
                Log.d(TAG, "barcode : " + result.getContents());
                Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
                intent.putExtra(ProductDetailActivity.ARG_PRODUCT_ID, result.getContents());
                intent.putExtra(ProductDetailActivity.ARG_IS_BARCODE, true);
                ActivityCompat.startActivityForResult(MainActivity.this, intent, REQUEST_UPDATE_LANGUAGE,
                        ActivityOptionsCompat
                                .makeScaleUpAnimation(toolbar, 0, 0, toolbar.getWidth(), toolbar.getHeight())
                                .toBundle());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == REQUEST_UPDATE_LANGUAGE) {
            if (getSwitchButton() != null) {
                getSwitchButton().setDefaultLanguage(getPreferenceManager().getDefaultLanguage());
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            super.onBackPressed();
        } else {
            if (getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_SUB_HEADER) != null) {
                startCategoryFragment();
            } else if (getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_PRODUCT_LIST) != null) {
//                if (this.productFilterHeader.getProductFilterSubHeaders().isEmpty()){
//                    startCategoryFragment();
//                } else {
//                    startCategoryLvTwoFragment(this.categoryLv1);
//                }
                handlePLPBackPress();
            } else {
                supportFinishAfterTransition();
            }
        }
    }

    private void handlePLPBackPress() {
        if(categoryLv2 == null){
            startCategoryFragment();
        } else {
            startCategoryLvTwoFragment(this.categoryLv1);
        }
    }

    private void updatePLP(String parentId) {
        HttpManagerMagento.Companion.getInstance(this).retrieveCategory(parentId, true,
                new ApiResponseCallback<List<Category>>() {
            @Override
            public void success(@Nullable final List<Category> categories) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (currentFragment instanceof ProductListFragment && categories != null) {
                                for (Category category : categories) {
                                    if (category.getId().equals(categoryLv2.getId())) {
                                        categoryLv2 = category; // force be new data th/en
                                    }
                                }
                            startProductListFragment(categoryLv2); // force reopen PLP
                            dismissProgressDialog();
                        }
                    }
                });
            }

            @Override
            public void failure(@NotNull APIError error) {
                Log.e(TAG, "onFailure: " + error.getErrorUserMessage());
                dismissProgressDialog();
            }
        });
    }

    private void retrieveCategories() {
        // currentFragment null?
        showProgressDialog();
        HttpManagerMagento.Companion.getInstance(this).retrieveCategory(CategoryUtils.SUPER_PARENT_ID, false
                , new ApiResponseCallback<List<Category>>() {
                    @Override
                    public void success(@Nullable final List<Category> categories) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                handleCategories(categories);
                            }
                        });
                    }

                    @Override
                    public void failure(@NotNull APIError error) {
                        Log.e(TAG, "onFailure: " + error.getErrorUserMessage());
                        dismissProgressDialog();
                    }
                });
    }

    private void handleCategories(List<Category> categories) {
        mCategoryDao = new CategoryDao(categories);
        createDrawerMenu(categories);

        if (categoryLv1 != null) {
            for (Category category : categories) {
                if (category.getId().equals(categoryLv1.getId())) {
                    categoryLv1 = category; // force be new data th/en
                }
            }
        }

        // check current page
        if (currentFragment instanceof CategoryFragment) {
            ((CategoryFragment) currentFragment).foreRefresh();
            dismissProgressDialog();
        } else if (currentFragment instanceof SubHeaderProductFragment){
            if (categoryLv1 != null) {
                ((SubHeaderProductFragment) currentFragment).foreRefresh(categoryLv1);
            }
            dismissProgressDialog();
        } else if (currentFragment instanceof ProductListFragment) {
            // TODO: fore refresh product list
            if(categoryLv2 != null){
                updatePLP(categoryLv2.getParentId()); // update categoryLv2 and update PLP
            } else {
                startProductListFragment(categoryLv1); // force reopen PLP with special category
                dismissProgressDialog();
            }
        } else {
            startCategoryFragment();
            dismissProgressDialog();
        }
    }

    private void createDrawerMenu(List<Category> categories) {
        mAdapter.clearItems();
        mDrawerItemList.clear(); // clear drawer
        mDrawerDao = new DrawerDao(mDrawerItemList);
        // TODO: ignore getStores
        if (mDrawerItemList.size() == 0 && categories != null) {
            for (Category category : categories) {
                mDrawerItemList.add(new DrawerItem(category.getDepartmentName(), category.getId(), category));
                Log.i(TAG, "Menu : " + mDrawerItemList.toString());
            }
            mAdapter.setDrawItem(mDrawerDao);
        } else {
            mAdapter.setDrawItem(mDrawerDao);
        }
    }

    private void showAlertDialog(String message, final boolean shouldCloseActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (shouldCloseActivity) finish();
                    }
                });

        builder.show();
    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        builder.show();
    }

    private void showAlertLogoutDialog(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok_alert), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userLogout();
                    }
                })
                .setNegativeButton(getString(R.string.cancel_alert), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.show();
    }

    // region {@link implement MenuDrawerClickListener}
    @Override
    public void onMenuClickedItem(@NotNull DrawerAdapter.DrawerAction action) {
        switch (action) {
            case ACTION_CART: {
                String cartId = new PreferenceManager(this).getCartId();
                int count = database.getCacheCartItems().size();
                if (cartId != null && count > 0) {
                    ShoppingCartActivity.Companion.startActivity(this, cartId);
                } else {
                    showAlertDialog("", getResources().getString(R.string.not_have_products_in_cart));
                }
            }
            break;
            case ACTION_HISTORY: {
                HistoryActivity.Companion.startActivity(this);
            }
            break;
            case ACTION_LOGOUT: {
                showAlertLogoutDialog(getResources().getString(R.string.user_logout));
            }
            break;
        }
    }

    private void userLogout() {
        clearData();
    }

    private void clearData() {
        PreferenceManager preferenceManager = new PreferenceManager(this);
        preferenceManager.userLogout();
        database.userLogout();

        // post delay start login
        showProgressDialog();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startLogin();
            }
        }, TIME_TO_WAIT);
    }

    private void startLogin() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            dismissProgressDialog();
        }
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void startCategoryFragment() {
        currentFragment = CategoryFragment.newInstance(mCategoryDao);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, currentFragment, TAG_FRAGMENT_CATEGORY_DEFAULT)
                .commitAllowingStateLoss();
    }

    private void startCategoryLvTwoFragment(Category categoryLv1) {
        currentFragment = SubHeaderProductFragment.Companion.newInstance(categoryLv1);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, currentFragment, TAG_FRAGMENT_SUB_HEADER)
                .commitAllowingStateLoss();
    }

    private void startProductListFragment(Category category) {
        currentFragment = ProductListFragment.newInstance(category, false, "");
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, currentFragment, TAG_FRAGMENT_PRODUCT_LIST)
                .commitAllowingStateLoss();
    }

    private void dismissProgressDialog() {
        if (!isFinishing() && mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /*
    *
    * override method from BaseActivity
    * on language change
    * on network connection change
    * */
    @Override
    public void onChangedLanguage(@NotNull AppLanguage lang) {
        drawer.closeDrawers();
        super.onChangedLanguage(lang);
        retrieveCategories();
    }

    @Override
    public void onNetworkStateChange(@NotNull NetworkInfo.State state) {
        drawer.closeDrawers();
        super.onNetworkStateChange(state);
        // isConnected?
        if (getCurrentState() == NetworkInfo.State.CONNECTED && getForceRefresh()) {
            retrieveCategories();
            setForceRefresh(false);
        }
    }

    @Nullable
    @Override
    public LanguageButton getSwitchButton() {
        return languageButton;
    }

    @Nullable
    @Override
    public NetworkStateView getStateView() { return networkStateView; }

    // region {@link implement CategoryAdapter.CategoryAdapterListener}
    @Override
    public void onClickedCategoryLv1(Category category) {
        this.categoryLv1 = category;
        handleStartCategoryLv1();
    }

    @Override
    public void onClickedCategoryLv2(Category category) {
        this.categoryLv2 = category;
        startProductListFragment(categoryLv2);
    }
    // endregion


    private void handleStartCategoryLv1() {
        if(ConvertersKt.isSpecial(categoryLv1)){
            this.categoryLv2 = null; //Clear categoryLv2
            startProductListFragment(categoryLv1);
        } else {
            startCategoryLvTwoFragment(categoryLv1);
        }
    }
}
