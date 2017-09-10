package cenergy.central.com.pwb_store.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.DrawerAdapter;
import cenergy.central.com.pwb_store.fragment.CategoryFragment;
import cenergy.central.com.pwb_store.fragment.LoadingFragment;
import cenergy.central.com.pwb_store.fragment.ProductListFragment;
import cenergy.central.com.pwb_store.fragment.SearchSuggestionFragment;
import cenergy.central.com.pwb_store.manager.HttpManager;
import cenergy.central.com.pwb_store.manager.UserInfoManager;
import cenergy.central.com.pwb_store.manager.bus.event.BackSearchBus;
import cenergy.central.com.pwb_store.manager.bus.event.BarcodeBus;
import cenergy.central.com.pwb_store.manager.bus.event.CategoryBus;
import cenergy.central.com.pwb_store.manager.bus.event.DrawItemBus;
import cenergy.central.com.pwb_store.manager.bus.event.HomeBus;
import cenergy.central.com.pwb_store.manager.bus.event.ProductBackBus;
import cenergy.central.com.pwb_store.manager.bus.event.SearchEventBus;
import cenergy.central.com.pwb_store.model.APIError;
import cenergy.central.com.pwb_store.model.Category;
import cenergy.central.com.pwb_store.model.CategoryDao;
import cenergy.central.com.pwb_store.model.DrawerDao;
import cenergy.central.com.pwb_store.model.DrawerItem;
import cenergy.central.com.pwb_store.model.StoreDao;
import cenergy.central.com.pwb_store.model.StoreList;
import cenergy.central.com.pwb_store.model.response.TokenResponse;
import cenergy.central.com.pwb_store.utils.APIErrorUtils;
import cenergy.central.com.pwb_store.utils.DialogUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String ARG_CATEGORY = "ARG_CATEGORY";
    private static final String ARG_DRAWER_LIST = "ARG_DRAWER_LIST";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;

    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    @BindView(R.id.recycler_view_menu)
    RecyclerView mRecyclerViewMenu;

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
            if (response.isSuccessful()){
                mCategoryDao = new CategoryDao(response.body());
                if (mDrawerItemList.size() == 0) {
                    for (Category category : mCategoryDao.getCategoryList()) {

                        mDrawerItemList.add(new DrawerItem(category.getDepartmentNameEN(), category.getDepartmentId(),
                                category.getParentId(), category.getRootDeptId(), category.getDepartmentNameEN()));
                        Log.d(TAG, "Detail : " + mDrawerItemList.toString());
                    }
                    mDrawerDao = new DrawerDao(mDrawerItemList);
                    mDrawerDao.setStoreDao(mStoreDao);
                    if (UserInfoManager.getInstance().getUserId() == null || UserInfoManager.getInstance().getUserId().equalsIgnoreCase("")){
                        Log.d(TAG, "User : " + UserInfoManager.getInstance().getUserId().toString());
                            storeId = "00096";
                        if (!mStoreDao.isStoreEmpty()) {
                            if (mStoreDao.isStoreListItemListAvailable()) {
                                List<StoreList> storeLists = mStoreDao.getStoreLists();
                                for (StoreList headerStore :
                                        storeLists) {
                                    headerStore.setSelected(headerStore.getStoreId().equalsIgnoreCase(storeId));
                                }
                            }
                        }
                    }else {
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
            }else {
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

    Callback<List<StoreList>> CALLBACK_STORE_LIST = new Callback<List<StoreList>>() {
        @Override
        public void onResponse(Call<List<StoreList>> call, Response<List<StoreList>> response) {
            if (response.isSuccessful()){
                mStoreDao = new StoreDao(response.body());

                HttpManager.getInstance().getCategoryService().getCategories().enqueue(CALLBACK_CATEGORY);
            }
        }

        @Override
        public void onFailure(Call<List<StoreList>> call, Throwable t) {
            Log.e(TAG, "onFailure: ", t);
        }
    };

    @Subscribe
    public void onEvent(DrawItemBus drawItemBus) {
        Toast.makeText(this,""+ drawItemBus.getDrawerItem().getTitle(), Toast.LENGTH_SHORT).show();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction
                .replace(R.id.container, ProductListFragment.newInstance(drawItemBus.getDrawerItem().getDepartmentNameEN(), false,
                        drawItemBus.getDrawerItem().getDepartmentId(), storeId))
                .commit();
    }

    @Subscribe
    public void onEvent(HomeBus homeBus){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction
                .replace(R.id.container, CategoryFragment.newInstance(mCategoryDao))
                .commit();
    }

    @Subscribe
    public void onEvent(CategoryBus categoryBus){
        if (categoryBus.getCategory().getTitle().equalsIgnoreCase("Change Language to Thai")){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction
                    .replace(R.id.container, CategoryFragment.newInstance(mCategoryDao))
                    .commit();
        }else if (categoryBus.getCategory().getTitle().equalsIgnoreCase("Compare")){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction
                    .replace(R.id.container, CategoryFragment.newInstance(mCategoryDao))
                    .commit();
        }else {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction
                    .replace(R.id.container, ProductListFragment.newInstance(categoryBus.getCategory().getDepartmentNameEN(), false,
                            categoryBus.getCategory().getDepartmentId(), storeId))
                    .commit();
        }

    }

    @Subscribe
    public void onEvent(ProductBackBus productBackBus){
        if (productBackBus.isHome() == true){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction
                    .replace(R.id.container, CategoryFragment.newInstance(mCategoryDao))
                    .commit();
        }
    }

    @Subscribe
    public void onEvent(BarcodeBus barcodeBus){
        if (barcodeBus.isBarcode() == true){
            IntentIntegrator integrator = new IntentIntegrator(MainActivity.this).setCaptureActivity(BarcodeScanActivity.class);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            integrator.initiateScan();
        }
    }

    @Subscribe
    public void onEvent(SearchEventBus searchEventBus){
        hideSoftKeyboard(searchEventBus.getView());
        if (searchEventBus.isClick() == true){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction
                    .replace(R.id.container, SearchSuggestionFragment.newInstance())
                    .commit();
        }
    }

    @Subscribe
    public void onEvent(BackSearchBus backSearchBus){
        if (backSearchBus.isClick() == true){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction
                    .replace(R.id.container, CategoryFragment.newInstance(mCategoryDao))
                    .commit();
        }
    }

    final Callback<TokenResponse> CALLBACK_CREATE_TOKEN = new Callback<TokenResponse>() {
        @Override
        public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
            if (response.isSuccessful()){
                UserInfoManager.getInstance().setCreateToken(response.body());
            }
        }

        @Override
        public void onFailure(Call<TokenResponse> call, Throwable t) {
            Log.e(TAG, "onFailure: ", t);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        if (savedInstanceState == null){
            showProgressDialog();
            HttpManager.getInstance().getStoreService().getStore().enqueue(CALLBACK_STORE_LIST);
//            HttpManager.getInstance().getCategoryService().getCategories().enqueue(CALLBACK_CATEGORY);
//            if (UserInfoManager.getInstance().getUserToken() == null){
//                HttpManager.getInstance().getTokenService().createToken("V1VTTklVNWx0UEllL3ZkMlJLbmViQjVDdkhVcFdOeUJXdysvSm1FbHRrTWI5T2FFU1FvMHB3PT0=", UserInfoManager.getInstance().getUUID(), UserInfoManager.getInstance().getUUID(), 00010)
//                        .enqueue(CALLBACK_CREATE_TOKEN);
//            }
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
        mRecyclerViewMenu.setLayoutManager(mLayoutManager);
        mRecyclerViewMenu.setAdapter(mAdapter);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawer,
                mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mNavigationView.setItemIconTintList(null);
        //mockData();

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
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCategoryDao = savedInstanceState.getParcelable(ARG_CATEGORY);
        mDrawerItemList = savedInstanceState.getParcelableArrayList(ARG_DRAWER_LIST);
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
