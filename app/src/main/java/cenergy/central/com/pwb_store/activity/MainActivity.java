package cenergy.central.com.pwb_store.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cenergy.central.com.pwb_store.BuildConfig;
import cenergy.central.com.pwb_store.CategoryUtils;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.CategoryAdapter;
import cenergy.central.com.pwb_store.adapter.DrawerAdapter;
import cenergy.central.com.pwb_store.adapter.interfaces.MenuDrawerClickListener;
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
import cenergy.central.com.pwb_store.manager.bus.event.SearchEventBus;
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage;
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager;
import cenergy.central.com.pwb_store.model.APIError;
import cenergy.central.com.pwb_store.model.Category;
import cenergy.central.com.pwb_store.model.DrawerDao;
import cenergy.central.com.pwb_store.model.DrawerItem;
import cenergy.central.com.pwb_store.realm.RealmController;
import cenergy.central.com.pwb_store.utils.Analytics;
import cenergy.central.com.pwb_store.utils.DialogUtils;
import cenergy.central.com.pwb_store.utils.RemoteConfigUtils;
import cenergy.central.com.pwb_store.view.LanguageButton;
import cenergy.central.com.pwb_store.view.NetworkStateView;

public class MainActivity extends BaseActivity implements MenuDrawerClickListener,
        CategoryAdapter.CategoryAdapterListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String TAG_FRAGMENT_CATEGORY_DEFAULT = "category_default";
    private static final String TAG_FRAGMENT_SUB_HEADER = "category_sub_header";
    private static final String TAG_FRAGMENT_PRODUCT_LIST = "product_list";
    private static final int TIME_TO_WAIT = 2000;

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerAdapter mAdapter;
    private GridLayoutManager mLayoutManager;
    private ArrayList<DrawerItem> mDrawerItemList = new ArrayList<>();
    private DrawerDao mDrawerDao;
    private ProgressDialog mProgressDialog;
    private LanguageButton languageButton;
    private NetworkStateView networkStateView;

    public static Handler handler = new Handler();
    private RealmController database = RealmController.getInstance();

    private Category categoryLv1;
    private Category categoryLv2;
    private Fragment currentFragment;

    // Firebase
    private Analytics analytics;
    private FirebaseRemoteConfig fbRemoteConfig;
    private boolean isLoadingCategory = false;
    private boolean isScanBarcode = false;
    private ArrayList<String> specialCategoryIds = new ArrayList<>();

    @Subscribe
    public void onEvent(DrawItemBus drawItemBus) {
        DrawerItem drawerItem = drawItemBus.getDrawerItem();
        this.categoryLv1 = drawerItem.getCategory();
        handleStartCategoryLv1();
    }

    @Subscribe
    public void onEvent(HomeBus homeBus) {
        startCategoryFragment();
    }

    // Event from onClick back button in product list
    @Subscribe
    public void onEvent(CategoryTwoBus categoryTwoBus) {
        onBackPressed();
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
        String keyword = searchEventBus.getKeyword();
        if (keyword.length() > 0) {
            analytics.trackSearch(keyword); // tracking event

            Intent intent = new Intent(this, ProductListActivity.class);
            intent.putExtra(ProductListActivity.ARG_KEY_WORD, searchEventBus.getKeyword());
            intent.putExtra(ProductListActivity.ARG_SEARCH, searchEventBus.isClick());
            startActivityForResult(intent, REQUEST_UPDATE_LANGUAGE);
        }
    }

    @Subscribe
    public void onEvent(CompareMenuBus compareMenuBus) {
        CompareActivity.Companion.startCompareActivity(this, compareMenuBus.getView());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        languageButton = findViewById(R.id.switch_language_button);
        // setup analytic
        analytics = new Analytics(this);

        // setup firebase config
        fbRemoteConfig = RemoteConfigUtils.INSTANCE.initFirebaseRemoteConfig();
        handleChangeLanguage();
        initView();
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
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);

        if (currentFragment == null) {
            retrieveCategories(); // start
        } else if (currentFragment instanceof CategoryFragment) {
            retrieveCategories(); // refresh
        }
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
                // Set isScanBarcode is true because don't need to load category
                isScanBarcode = true;
                Log.d(TAG, "barcode : " + result.getContents());
                if (analytics != null) {
                    analytics.trackSearchByBarcode(result.getContents());
                }
                Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
                intent.putExtra(ProductDetailActivity.ARG_PRODUCT_ID, result.getContents());
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
                handlePLPBackPress();
            } else {
                supportFinishAfterTransition();
            }
        }
    }

    private void handlePLPBackPress() {
        if (categoryLv2 == null) {
            startCategoryFragment();
        } else {
            startCategoryLvTwoFragment(this.categoryLv1);
        }
    }

    private void updatePLP(String parentId) {
        HttpManagerMagento.Companion.getInstance(this).retrieveCategory(parentId,
                true, new ArrayList<>(), new ApiResponseCallback<List<Category>>() {
                    @Override
                    public void success(@Nullable final List<Category> categories) {
                        runOnUiThread(() -> {
                            if (currentFragment instanceof ProductListFragment && categories != null) {
                                for (Category category : categories) {
                                    if (category.getId().equals(categoryLv2.getId())) {
                                        categoryLv2 = category; // force be new data th/en
                                    }
                                }
                                startProductListFragment(categoryLv2); // force reopen PLP
                                dismissProgressDialog();
                            }
                        });
                    }

                    @Override
                    public void failure(@NotNull APIError error) {
                        runOnUiThread(() -> dismissProgressDialog());
                    }
                });
    }

    private void retrieveCategories() {
        if (isLoadingCategory && !isScanBarcode) return;

        showProgressDialog();
        this.isLoadingCategory = true;

        // fetch remote config for special category
        fbRemoteConfig.fetchAndActivate().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.i(TAG, "remote config -> fetch Successful");
            } else {
                Log.i(TAG, "remote config -> fetch Fail");
            }
            requestCategories();
        });
    }

    private void requestCategories() {
        String displayIds = fbRemoteConfig.getString(RemoteConfigUtils.CONFIG_KEY_DISPLAY_SPECIAL_CATEGORY_ID);
        specialCategoryIds.clear();
        if (!displayIds.trim().equals("")) {
            getPreferenceManager().setSpecialCategoryIds(displayIds);
            String[] ids = displayIds.split(",");
            specialCategoryIds.addAll(Arrays.asList(ids));
        }

        HttpManagerMagento.Companion.getInstance(this).retrieveCategory(
                CategoryUtils.SUPER_PARENT_ID, false, specialCategoryIds,
                new ApiResponseCallback<List<Category>>() {
                    @Override
                    public void success(@Nullable final List<Category> categories) {
                        runOnUiThread(() -> handleCategories(categories));
                    }

                    @Override
                    public void failure(@NotNull APIError error) {
                        runOnUiThread(() -> {
                            isLoadingCategory = false;
                            dismissProgressDialog();
                        });
                    }
                });
    }

    private void handleCategories(List<Category> categories) {
        this.isLoadingCategory = false;

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
            ((CategoryFragment) currentFragment).updateView(categories);
            dismissProgressDialog();
        } else if (currentFragment instanceof SubHeaderProductFragment) {
            if (categoryLv1 != null) {
                ((SubHeaderProductFragment) currentFragment).foreRefresh(categoryLv1);
            }
            dismissProgressDialog();
        } else if (currentFragment instanceof ProductListFragment) {
            if (categoryLv2 != null) {
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

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showAlertLogoutDialog(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok_alert), (dialog, which) -> userLogout())
                .setNegativeButton(getString(R.string.cancel_alert), (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    // region {@link implement MenuDrawerClickListener}
    @Override
    public void onMenuClickedItem(@NotNull DrawerAdapter.DrawerAction action) {
        switch (action) {
            case ACTION_CART: {
                if (database.getCacheCartItems().size() > 0) {
                    ShoppingCartActivity.Companion.startActivity(this);
                } else {
                    showAlertDialog(getResources().getString(R.string.not_have_products_in_cart));
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
        handler.postDelayed(this::startLogin, TIME_TO_WAIT);
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
        currentFragment = CategoryFragment.newInstance();
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

    /**
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
    public NetworkStateView getStateView() {
        return networkStateView;
    }

    // region {@link implement CategoryAdapter.CategoryAdapterListener}
    @Override
    public void onClickedCategoryLv1(Category category) {
        this.categoryLv1 = category;

        // tracking event
        if (analytics != null) {
            analytics.trackViewCategoryLv1(category.getId(), category.getDepartmentName());
        }

        handleStartCategoryLv1();
    }

    @Override
    public void onClickedCategoryLv2(Category category) {
        this.categoryLv2 = category;

        // tracking event
        if (analytics != null) {
            analytics.trackViewProductList(category.getId(), category.getDepartmentName());
        }

        startProductListFragment(categoryLv2);
    }
    // endregion

    private void handleStartCategoryLv1() {
        if (specialCategoryIds.contains(categoryLv1.getId())) {
            this.categoryLv2 = null; //Clear categoryLv2
            startProductListFragment(categoryLv1);
        } else {
            startCategoryLvTwoFragment(categoryLv1);
        }
    }
}
