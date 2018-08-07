package cenergy.central.com.pwb_store.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import cenergy.central.com.pwb_store.fragment.CategoryFragment;
import cenergy.central.com.pwb_store.fragment.ProductListFragment;
import cenergy.central.com.pwb_store.fragment.SubHeaderProductFragment;
import cenergy.central.com.pwb_store.manager.ApiResponseCallback;
import cenergy.central.com.pwb_store.manager.HttpManagerMagento;
import cenergy.central.com.pwb_store.manager.HttpManagerMagentoOld;
import cenergy.central.com.pwb_store.manager.UserInfoManager;
import cenergy.central.com.pwb_store.manager.bus.event.BackSearchBus;
import cenergy.central.com.pwb_store.manager.bus.event.BarcodeBus;
import cenergy.central.com.pwb_store.manager.bus.event.CategoryBus;
import cenergy.central.com.pwb_store.manager.bus.event.CompareMenuBus;
import cenergy.central.com.pwb_store.manager.bus.event.DrawItemBus;
import cenergy.central.com.pwb_store.manager.bus.event.HomeBus;
import cenergy.central.com.pwb_store.manager.bus.event.ProductBackBus;
import cenergy.central.com.pwb_store.manager.bus.event.ProductFilterHeaderBus;
import cenergy.central.com.pwb_store.manager.bus.event.SearchEventBus;
import cenergy.central.com.pwb_store.model.APIError;
import cenergy.central.com.pwb_store.model.Category;
import cenergy.central.com.pwb_store.model.CategoryDao;
import cenergy.central.com.pwb_store.model.DrawerDao;
import cenergy.central.com.pwb_store.model.DrawerItem;
import cenergy.central.com.pwb_store.model.ProductFilterHeader;
import cenergy.central.com.pwb_store.model.StoreDao;
import cenergy.central.com.pwb_store.model.StoreList;
import cenergy.central.com.pwb_store.model.response.TokenResponse;
import cenergy.central.com.pwb_store.utils.APIErrorUtils;
import cenergy.central.com.pwb_store.utils.DialogUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String ARG_CATEGORY = "ARG_CATEGORY";
    private static final String ARG_DRAWER_LIST = "ARG_DRAWER_LIST";
    private static final String ARG_STORE_ID = "ARG_STORE_ID";
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

    final Callback<List<Category>> CALLBACK_CATEGORY = new Callback<List<Category>>() {
        @Override
        public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
            if (response.isSuccessful()) {
                mCategoryDao = new CategoryDao(response.body());
                if (mDrawerItemList.size() == 0) {
                    for (Category category : mCategoryDao.getCategoryList()) {

//                        mDrawerItemList.add(new DrawerItem(category.getDepartmentNameEN(), category.getDepartmentId(),
//                                category.getParentId(), category.getRootDeptId(), category.getDepartmentNameEN()));
                        mDrawerItemList.add(new DrawerItem(category.getDepartmentName(), category.getId(), category));
                        Log.d(TAG, "Detail : " + mDrawerItemList.toString());
                    }
                    mDrawerDao = new DrawerDao(mDrawerItemList);
                    mDrawerDao.setStoreDao(mStoreDao);
                    if (UserInfoManager.getInstance().getUserId() == null ||
                            UserInfoManager.getInstance().getUserId().equalsIgnoreCase("")) {
                        Log.d(TAG, "User : " + UserInfoManager.getInstance().getUserId().toString());
                        storeId = "00096";
                        UserInfoManager.getInstance().setUserIdLogin(storeId);
                        for (StoreList storeList : mStoreDao.getStoreLists()) {
                            if (storeList.getStoreId().equalsIgnoreCase(storeId)) {
                                UserInfoManager.getInstance().setStore(storeList);
                            }
                        }
                        if (!mStoreDao.isStoreEmpty()) {
                            if (mStoreDao.isStoreListItemListAvailable()) {
                                List<StoreList> storeLists = mStoreDao.getStoreLists();
                                for (StoreList headerStore :
                                        storeLists) {
                                    headerStore.setSelected(headerStore.getStoreId().equalsIgnoreCase(storeId));
                                }
                            }
                        }
                    } else {
                        Log.d(TAG, "User : " + UserInfoManager.getInstance().getUserId().toString());
                        storeId = UserInfoManager.getInstance().getUserId();
                        if (!mStoreDao.isStoreEmpty()) {
                            if (mStoreDao.isStoreListItemListAvailable()) {
                                List<StoreList> storeLists = mStoreDao.getStoreLists();
                                for (StoreList headerStore :
                                        storeLists) {
                                    headerStore.setSelected(headerStore.getStoreId().equalsIgnoreCase(storeId));
                                }
                            }
                        }
                    }
                    mAdapter.setDrawItem(mDrawerDao);
                } else {
                    mAdapter.setDrawItem(mDrawerDao);
                }
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction
                        .replace(R.id.container, CategoryFragment.newInstance(mCategoryDao))
                        .commit();
                mProgressDialog.dismiss();

//                if (UserInfoManager.getInstance().getUserToken().equalsIgnoreCase("")){
//                    UserInfoManager.getInstance().setToken(true);
////                    HttpManagerHDL.getInstance().getTokenService().createToken(getResources().getString(R.string.secret),
////                            UserInfoManager.getInstance().getImei(),
////                            UserInfoManager.getInstance().getImei(),
////                            UserInfoManager.getInstance().getUserId())
////                            .enqueue(CALLBACK_CREATE_TOKEN);
//                    CreateTokenRequest createTokenRequest = new CreateTokenRequest(UserInfoManager.getInstance().getImei(),
//                            UserInfoManager.getInstance().getImei(), "", UserInfoManager.getInstance().getUserId());
//                    HttpManagerHDL.getInstance().getTokenService().createToken(getResources().getString(R.string.secret),
//                            "application/json",
//                            createTokenRequest).enqueue(CALLBACK_CREATE_TOKEN);
//                } else {
//                    UserInfoManager.getInstance().setToken(false);
////                    String token = UserInfoManager.getInstance().getUserToken();
////                    Log.d(TAG, "UserToken : " + UserInfoManager.getInstance().getUserToken());
//                }
            } else {
                APIError error = APIErrorUtils.parseError(response);
                Log.e(TAG, "onResponse: " + error.getErrorMessage());
                showAlertDialog(error.getErrorMessage(), false);
                mProgressDialog.dismiss();
            }
        }

        @Override
        public void onFailure(Call<List<Category>> call, Throwable t) {
            Log.e(TAG, "onFailure: ", t);
            mProgressDialog.dismiss();
        }
    };

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
                mProgressDialog.dismiss();
            }
        }

        @Override
        public void onFailure(Call<List<StoreList>> call, Throwable t) {
            Log.e(TAG, "onFailure: ", t);
            mProgressDialog.dismiss();
        }
    };

    @Subscribe
    public void onEvent(DrawItemBus drawItemBus) {
        DrawerItem drawerItem = drawItemBus.getDrawerItem();
        Toast.makeText(this, "" + drawItemBus.getDrawerItem().getTitle(), Toast.LENGTH_SHORT).show();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction
                .replace(R.id.container, ProductListFragment.newInstance(drawerItem.getTitle(), false,
                        drawerItem.getId(), storeId, drawerItem.getCategory(), ""))
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

    @Subscribe
    public void onEvent(ProductFilterHeaderBus header) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction
                .replace(R.id.container, SubHeaderProductFragment.Companion.newInstance(header.getProductFilterHeader()))
                .commit();
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
                    mProgressDialog.dismiss();
                }

            }
        }

        @Override
        public void onFailure(Call<TokenResponse> call, Throwable t) {
            Log.e(TAG, "onFailure: ", t);
            mProgressDialog.dismiss();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        if (savedInstanceState == null) {
            showProgressDialog();
            HttpManagerMagentoOld.getInstance().getStoreService().getStore().enqueue(CALLBACK_STORE_LIST);

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

        mAdapter = new DrawerAdapter(getApplicationContext());
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
                //TODO แก้Barcode
                Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                Log.d(TAG, "barcode : " + result.getContents());
                Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
                intent.putExtra(ProductDetailActivity.ARG_PRODUCT_ID, result.getContents());
                intent.putExtra(ProductDetailActivity.ARG_IS_BARCODE, true);
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
            supportFinishAfterTransition();
        }
    }

    private void retrieveCategories() {
        HttpManagerMagento.Companion.getInstance().retrieveCategories(new ApiResponseCallback<Category>() {
            @Override
            public void success(@Nullable Category category) {
                if (mDrawerItemList.size() == 0 && category != null) {
                    mDrawerItemList.add(new DrawerItem(category.getDepartmentName(), category.getId(), category));
                    Log.d(TAG, "Detail : " + mDrawerItemList.toString());
                    mDrawerDao = new DrawerDao(mDrawerItemList);
                    mDrawerDao.setStoreDao(mStoreDao);
                    if (UserInfoManager.getInstance().getUserId() == null ||
                            UserInfoManager.getInstance().getUserId().equalsIgnoreCase("")) {
                        Log.d(TAG, "User : " + UserInfoManager.getInstance().getUserId());
                        storeId = "00096";
                        UserInfoManager.getInstance().setUserIdLogin(storeId);
                        for (StoreList storeList : mStoreDao.getStoreLists()) {
                            if (storeList.getStoreId().equalsIgnoreCase(storeId)) {
                                UserInfoManager.getInstance().setStore(storeList);
                            }
                        }
                        if (!mStoreDao.isStoreEmpty()) {
                            if (mStoreDao.isStoreListItemListAvailable()) {
                                List<StoreList> storeLists = mStoreDao.getStoreLists();
                                for (StoreList headerStore :
                                        storeLists) {
                                    headerStore.setSelected(headerStore.getStoreId().equalsIgnoreCase(storeId));
                                }
                            }
                        }
                    } else {
                        Log.d(TAG, "User : " + UserInfoManager.getInstance().getUserId());
                        storeId = UserInfoManager.getInstance().getUserId();
                        if (!mStoreDao.isStoreEmpty()) {
                            if (mStoreDao.isStoreListItemListAvailable()) {
                                List<StoreList> storeLists = mStoreDao.getStoreLists();
                                for (StoreList headerStore :
                                        storeLists) {
                                    headerStore.setSelected(headerStore.getStoreId().equalsIgnoreCase(storeId));
                                }
                            }
                        }
                    }
                    mAdapter.setDrawItem(mDrawerDao);
                } else {
                    mAdapter.setDrawItem(mDrawerDao);
                }
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction
                        .replace(R.id.container, CategoryFragment.newInstance(mCategoryDao))
                        .commit();
                mProgressDialog.dismiss();
            }

            @Override
            public void failure(@NotNull APIError error) {
                Log.e(TAG, "onFailure: " + error.getErrorUserMessage());
                mProgressDialog.dismiss();
            }
        });
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

}
