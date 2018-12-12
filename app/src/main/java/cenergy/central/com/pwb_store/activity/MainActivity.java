package cenergy.central.com.pwb_store.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.DrawerAdapter;
import cenergy.central.com.pwb_store.adapter.interfaces.MenuDrawerClickListener;
import cenergy.central.com.pwb_store.fragment.CategoryFragment;
import cenergy.central.com.pwb_store.fragment.ProductListFragment;
import cenergy.central.com.pwb_store.fragment.SubHeaderProductFragment;
import cenergy.central.com.pwb_store.manager.ApiResponseCallback;
import cenergy.central.com.pwb_store.manager.HttpManagerMagento;
import cenergy.central.com.pwb_store.manager.UserInfoManager;
import cenergy.central.com.pwb_store.manager.bus.event.BackSearchBus;
import cenergy.central.com.pwb_store.manager.bus.event.BarcodeBus;
import cenergy.central.com.pwb_store.manager.bus.event.CategoryBus;
import cenergy.central.com.pwb_store.manager.bus.event.CompareMenuBus;
import cenergy.central.com.pwb_store.manager.bus.event.DrawItemBus;
import cenergy.central.com.pwb_store.manager.bus.event.HomeBus;
import cenergy.central.com.pwb_store.manager.bus.event.ProductBackBus;
import cenergy.central.com.pwb_store.manager.bus.event.ProductFilterHeaderBus;
import cenergy.central.com.pwb_store.manager.bus.event.ProductFilterSubHeaderBus;
import cenergy.central.com.pwb_store.manager.bus.event.SearchEventBus;
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager;
import cenergy.central.com.pwb_store.model.APIError;
import cenergy.central.com.pwb_store.model.Category;
import cenergy.central.com.pwb_store.model.CategoryDao;
import cenergy.central.com.pwb_store.model.DrawerDao;
import cenergy.central.com.pwb_store.model.DrawerItem;
import cenergy.central.com.pwb_store.model.ProductFilterHeader;
import cenergy.central.com.pwb_store.model.StoreDao;
import cenergy.central.com.pwb_store.model.StoreList;
import cenergy.central.com.pwb_store.model.response.TokenResponse;
import cenergy.central.com.pwb_store.realm.RealmController;
import cenergy.central.com.pwb_store.utils.APIErrorUtils;
import cenergy.central.com.pwb_store.utils.DialogUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MenuDrawerClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String ARG_CATEGORY = "ARG_CATEGORY";
    private static final String ARG_DRAWER_LIST = "ARG_DRAWER_LIST";
    private static final String ARG_STORE_ID = "ARG_STORE_ID";
    private static final String TAG_FRAGMENT_CATEGORY_DEFAULT = "category_default";
    private static final String TAG_FRAGMENT_SUB_HEADER = "category_sub_header";
    private static final int TIME_TO_WAIT = 2000;

    //private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 999;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerAdapter mAdapter;
    private GridLayoutManager mLayoutManager;
    private ArrayList<DrawerItem> mDrawerItemList = new ArrayList<>();
    private StoreDao mStoreDao;
    private DrawerDao mDrawerDao;
    private CategoryDao mCategoryDao;
    private String storeId;
    private ProgressDialog mProgressDialog;
    public static Handler handler = new Handler();
    private RealmController database = RealmController.getInstance();

    final Callback<List<StoreList>> CALLBACK_STORE_LIST = new Callback<List<StoreList>>() {
        @Override
        public void onResponse(Call<List<StoreList>> call, Response<List<StoreList>> response) {
            if (response.isSuccessful()) {
                mStoreDao = new StoreDao(response.body());
//                HttpManagerMagentoOld.getInstance().getCategoryService().getCategories().enqueue(CALLBACK_CATEGORY);
                retrieveCategories();
            } else {
                APIError error = APIErrorUtils.parseError(response);
                Log.e(TAG, "onResponse: " + error.getErrorMessage());
                showAlertDialog(error.getErrorMessage(), false);
                dismissProgressDialog();
            }
        }

        @Override
        public void onFailure(Call<List<StoreList>> call, Throwable t) {
            Log.e(TAG, "onFailure: ", t);
            dismissProgressDialog();
        }
    };

    @Subscribe
    public void onEvent(DrawItemBus drawItemBus) {
        DrawerItem drawerItem = drawItemBus.getDrawerItem();
        Toast.makeText(this, "" + drawItemBus.getDrawerItem().getTitle(), Toast.LENGTH_SHORT).show();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction
//                .replace(R.id.container, ProductListFragment.newInstance(drawerItem.getTitle(), false,
//                        drawerItem.getProductFilterHeader().getId(), storeId, "", drawerItem.getProductFilterHeader()))
//                .commit();
        fragmentTransaction
                .replace(R.id.container, SubHeaderProductFragment.Companion.newInstance(drawerItem.getProductFilterHeader()))
                .commit();
    }

    @Subscribe
    public void onEvent(HomeBus homeBus) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction
                .replace(R.id.container, CategoryFragment.newInstance(mCategoryDao))
                .commit();
    }

    @Subscribe
    public void onEvent(CategoryBus categoryBus) {
        if (categoryBus.getCategory().getDepartmentName().equalsIgnoreCase("Change Language to Thai")) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction
                    .replace(R.id.container, CategoryFragment.newInstance(mCategoryDao))
                    .commit();
        } else if (categoryBus.getCategory().getDepartmentName().equalsIgnoreCase("Compare")) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction
                    .replace(R.id.container, CategoryFragment.newInstance(mCategoryDao))
                    .commit();
        } else {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction
                    .replace(R.id.container, ProductListFragment.newInstance(categoryBus.getCategory().getDepartmentName(), false,
                            categoryBus.getCategory().getId(), storeId, categoryBus.getCategory(), ""))
                    .commit();
        }
    }

    // Event from onClick category item
    @Subscribe
    public void onEvent(ProductFilterHeaderBus productFilterHeaderBus) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction
                .replace(R.id.container,
                        SubHeaderProductFragment.Companion.newInstance(productFilterHeaderBus.getProductFilterHeader()),
                        TAG_FRAGMENT_SUB_HEADER)
                .commit();
    }

    // Event from onClick product filter sub header item
    @Subscribe
    public void onEvent(ProductFilterSubHeaderBus productFilterSubHeaderBus) {
        if (productFilterSubHeaderBus.getProductFilterSubHeader().getName().equalsIgnoreCase("Change Language to Thai")) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction
                    .replace(R.id.container, CategoryFragment.newInstance(mCategoryDao))
                    .commit();
        } else if (productFilterSubHeaderBus.getProductFilterSubHeader().getName().equalsIgnoreCase("Compare")) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction
                    .replace(R.id.container, CategoryFragment.newInstance(mCategoryDao))
                    .commit();
        } else {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction
                    .replace(R.id.container, ProductListFragment.newInstance(productFilterSubHeaderBus.getProductFilterSubHeader().getName()
                            , false, productFilterSubHeaderBus.getProductFilterSubHeader().getId(),
                            storeId, "", productFilterSubHeaderBus.getProductFilterSubHeader()))
                    .commit();
        }
    }

    @Subscribe
    public void onEvent(ProductBackBus productBackBus) {
        if (productBackBus.isHome() == true) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction
                    .replace(R.id.container, CategoryFragment.newInstance(mCategoryDao))
                    .commit();
        }
    }

    @Subscribe
    public void onEvent(BarcodeBus barcodeBus) {
        if (barcodeBus.isBarcode() == true) {
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
            startActivity(intent);
        }
    }

    @Subscribe
    public void onEvent(BackSearchBus backSearchBus) {
        if (backSearchBus.isClick() == true) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction
                    .replace(R.id.container, CategoryFragment.newInstance(mCategoryDao))
                    .commit();
        }
    }

    @Subscribe
    public void onEvent(CompareMenuBus compareMenuBus) {
        Intent intent = new Intent(this, CompareActivity.class);
        ActivityCompat.startActivity(this, intent,
                ActivityOptionsCompat
                        .makeScaleUpAnimation(compareMenuBus.getView(), 0, 0, compareMenuBus.getView().getWidth(), compareMenuBus.getView().getHeight())
                        .toBundle());
    }

    final Callback<TokenResponse> CALLBACK_CREATE_TOKEN = new Callback<TokenResponse>() {
        @Override
        public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
            if (response.isSuccessful()) {
                TokenResponse tokenResponse = response.body();
                if (tokenResponse.getResultStatus() != null) {
                    UserInfoManager.getInstance().setCreateToken(tokenResponse);
                } else {
                    APIError error = APIErrorUtils.parseError(response);
                    Log.e(TAG, "onResponse: " + error.getErrorMessage());
                    showAlertDialog(error.getErrorMessage(), false);
                    dismissProgressDialog();
                }

            }
        }

        @Override
        public void onFailure(Call<TokenResponse> call, Throwable t) {
            Log.e(TAG, "onFailure: ", t);
            dismissProgressDialog();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        if (savedInstanceState == null) {
            showProgressDialog();
//            HttpManagerMagentoOld.getInstance().getStoreService().getStore().enqueue(CALLBACK_STORE_LIST);
            // TODO: ignore getStores
            retrieveCategories();

        } else {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction
                    .replace(R.id.container, CategoryFragment.newInstance(mCategoryDao))
                    .commit();
        }

    }

    private void initView() {
        ButterKnife.bind(this);
        //initHeaderView();

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mAdapter = new DrawerAdapter(this);
        mLayoutManager = new GridLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false);
        RecyclerView recyclerViewMenu = findViewById(R.id.recycler_view_menu);
        recyclerViewMenu.setLayoutManager(mLayoutManager);
        recyclerViewMenu.setAdapter(mAdapter);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                MainActivity.this,
                drawer,
                mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
    }

//    private void initHeaderView() {
//        mDrawerHeader = mNavigationView.getHeaderView(0);
//    }

//    private void mockData(){
//                //Dummy Data
//        mDrawerItemList = new ArrayList<DrawerItem>();
//        mDrawerItemList.add(new DrawerItem("",1, 1, 1, getResources().getString(R.string.home_tv)));
//        mDrawerItemList.add(new DrawerItem("",2, 2, 2, getResources().getString(R.string.home_app)));
//        mDrawerItemList.add(new DrawerItem("",3, 3, 3, getResources().getString(R.string.home_cooling)));
//        mDrawerItemList.add(new DrawerItem("",4, 4, 4, getResources().getString(R.string.home_small)));
//        mDrawerItemList.add(new DrawerItem("",5, 5, 5, getResources().getString(R.string.home_kitchen)));
//        mDrawerItemList.add(new DrawerItem("",6, 6, 6, getResources().getString(R.string.home_health)));
//        mDrawerItemList.add(new DrawerItem("",7, 7, 7, getResources().getString(R.string.home_life)));
//        mDrawerItemList.add(new DrawerItem("",8, 8, 8, getResources().getString(R.string.home_wearable)));
//        mDrawerItemList.add(new DrawerItem("",9, 9, 9, getResources().getString(R.string.home_accessories)));
//        mDrawerItemList.add(new DrawerItem("",10, 10, 10, getResources().getString(R.string.schedule)));
//        mDrawerItemList.add(new DrawerItem("",11, 11, 11, getResources().getString(R.string.compare)));
//        mDrawerItemList.add(new DrawerItem("",12, 12, 12, getResources().getString(R.string.change)));
//        mDrawerItemList.add(new DrawerItem("",13, 13, 13, getResources().getString(R.string.help)));
//    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtils.createProgressDialog(this);
            mProgressDialog.show();
        } else {
            mProgressDialog.show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_CATEGORY, mCategoryDao);
        outState.putParcelableArrayList(ARG_DRAWER_LIST, mDrawerItemList);
        outState.putString(ARG_STORE_ID, storeId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCategoryDao = savedInstanceState.getParcelable(ARG_CATEGORY);
        mDrawerItemList = savedInstanceState.getParcelableArrayList(ARG_DRAWER_LIST);
        storeId = savedInstanceState.getString(ARG_STORE_ID);
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
//                Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                Log.d(TAG, "barcode : " + result.getContents());
                Intent intent = new Intent(MainActivity.this, ProductDetailActivity2.class);
                intent.putExtra(ProductDetailActivity2.ARG_PRODUCT_ID, result.getContents());
                intent.putExtra(ProductDetailActivity2.ARG_IS_BARCODE, true);
                ActivityCompat.startActivity(MainActivity.this, intent,
                        ActivityOptionsCompat
                                .makeScaleUpAnimation(mToolbar, 0, 0, mToolbar.getWidth(), mToolbar.getHeight())
                                .toBundle());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void hideSoftKeyboard(View view) {
        // Check if no view has focus:
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            super.onBackPressed();
        } else {
            if (getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_SUB_HEADER) != null) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container,
                        CategoryFragment.newInstance(mCategoryDao), TAG_FRAGMENT_CATEGORY_DEFAULT)
                        .commit();
            } else {
                supportFinishAfterTransition();
            }
        }
    }

    private void retrieveCategories() {
        HttpManagerMagento.Companion.getInstance(this).retrieveCategories(false, 2, 4, new ApiResponseCallback<Category>() {
            @Override
            public void success(@Nullable Category category) {
                if (!isFinishing()) {
                    if (category != null) {
                        mCategoryDao = new CategoryDao(category);
                        createDrawerMenu(category);
                    }
                    if (getSupportFragmentManager() != null) {
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction
                                .replace(R.id.container, CategoryFragment.newInstance(mCategoryDao), TAG_FRAGMENT_CATEGORY_DEFAULT)
                                .commit();
                    }
                    dismissProgressDialog();
                }
            }

            @Override
            public void failure(@NotNull APIError error) {
                Log.e(TAG, "onFailure: " + error.getErrorUserMessage());
                dismissProgressDialog();
            }
        });
    }

    private void createDrawerMenu(Category category) {
        mDrawerDao = new DrawerDao(mDrawerItemList);
        // TODO: ignore getStores
        if (mDrawerItemList.size() == 0 && category != null) {
            for (ProductFilterHeader item : category.getFilterHeaders()) {
                mDrawerItemList.add(new DrawerItem(item.getName(), item.getId(), item));
                Log.d(TAG, "Detail : " + mDrawerItemList.toString());
            }
//                    mDrawerDao.setStoreDao(mStoreDao);
//                    if (UserInfoManager.getInstance().getUserId() == null ||
//                            UserInfoManager.getInstance().getUserId().equalsIgnoreCase("")) {
//                        Log.d(TAG, "User : " + UserInfoManager.getInstance().getUserId());
//                        storeId = "00096";
//                        UserInfoManager.getInstance().setUserIdLogin(storeId);
//                        for (StoreList storeList : mStoreDao.getStoreLists()) {
//                            if (storeList.getStoreId().equalsIgnoreCase(storeId)) {
//                                UserInfoManager.getInstance().setStore(storeList);
//                            }
//                        }
//                        if (!mStoreDao.isStoreEmpty()) {
//                            if (mStoreDao.isStoreListItemListAvailable()) {
//                                List<StoreList> storeLists = mStoreDao.getStoreLists();
//                                for (StoreList headerStore :
//                                        storeLists) {
//                                    headerStore.setSelected(headerStore.getStoreId().equalsIgnoreCase(storeId));
//                                }
//                            }
//                        }
//                    } else {
//                        Log.d(TAG, "User : " + UserInfoManager.getInstance().getUserId());
//                        storeId = UserInfoManager.getInstance().getUserId();
//                        if (!mStoreDao.isStoreEmpty()) {
//                            if (mStoreDao.isStoreListItemListAvailable()) {
//                                List<StoreList> storeLists = mStoreDao.getStoreLists();
//                                for (StoreList headerStore :
//                                        storeLists) {
//                                    headerStore.setSelected(headerStore.getStoreId().equalsIgnoreCase(storeId));
//                                }
//                            }
//                        }
//                    }
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
                .setPositiveButton(getString(R.string.ok_alert), new DialogInterface.OnClickListener() {
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

    private void dismissProgressDialog() {
        if (!isFinishing() && mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
