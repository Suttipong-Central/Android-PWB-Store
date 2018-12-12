package cenergy.central.com.pwb_store.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.fragment.SpecFragment;
import cenergy.central.com.pwb_store.manager.bus.event.UpdateBadgeBus;
import cenergy.central.com.pwb_store.model.Product;
import cenergy.central.com.pwb_store.model.ProductDetail;
import cenergy.central.com.pwb_store.model.SpecDao;
import cenergy.central.com.pwb_store.realm.RealmController;
import cenergy.central.com.pwb_store.utils.DialogUtils;
import cenergy.central.com.pwb_store.view.PowerBuyCompareView;
import io.realm.Realm;

/**
 * Created by napabhat on 7/26/2017 AD.
 */

public class SpecActivity extends AppCompatActivity implements PowerBuyCompareView.OnClickListener{

    private static final String TAG = SpecActivity.class.getSimpleName();
    public static final String ARG_SPEC_DAO = "ARG_SPEC_DAO";
    public static final String ARG_PRODUCT_DETAIL = "ARG_PRODUCT_DETAIL";
    public static final String ARG_PRODUCT_ID = "ARG_PRODUCT_ID";
    public static final String ARG_PRODUCT = "ARG_PRODUCT";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.button_compare)
    PowerBuyCompareView mBuyCompareView;

    private Realm mRealm;
    private SpecDao mSpecDao;
    private ProductDetail mProductDetail;
    private ProgressDialog mProgressDialog;
    private String productId;
    private Product product;
    private RealmController database = RealmController.getInstance();

//    final Callback<ProductDetail> CALLBACK_PRODUCT_DETAIL = new Callback<ProductDetail>() {
//        @Override
//        public void onResponse(Call<ProductDetail> call, Response<ProductDetail> response) {
//            if (response.isSuccessful()){
//                mProductDetail = response.body();
//                if (mProductDetail != null){
//                    ExtensionProductDetail extensionProductDetail = mProductDetail.getExtensionProductDetail();
//                    if (extensionProductDetail.getSpecItems() != null){
//                        mSpecDao = new SpecDao(extensionProductDetail.getSpecItems());
//                        mProductDetail.setSpecDao(mSpecDao);
//                    }
//
//                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                    fragmentTransaction
//                            .replace(R.id.container, SpecFragment.newInstance(mSpecDao, mProductDetail))
//                            .commit();
//                    mProgressDialog.dismiss();
//                }else {
//                    //MockData();
//                    mProgressDialog.dismiss();
//                }
//            }else {
//                APIError error = APIErrorUtils.parseError(response);
//                Log.e(TAG, "onResponse: " + error.getErrorMessage());
//                showAlertDialog(error.getErrorMessage(), false);
//                mProgressDialog.dismiss();
//            }
//        }
//
//        @Override
//        public void onFailure(Call<ProductDetail> call, Throwable t) {
//            Log.e(TAG, "onFailure: ", t);
//            mProgressDialog.dismiss();
//        }
//    };

    @Subscribe
    public void onEvent(UpdateBadgeBus updateBadgeBus){
        if (updateBadgeBus.isUpdate()){
            long count = database.getCompareProducts().size();
            mBuyCompareView.updateCartCount((int) count);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spec);

        initView();

        Intent mIntent = getIntent();
        Bundle extras = mIntent.getExtras();
        if (extras != null) {
            mSpecDao = extras.getParcelable("ARG_SPEC_DAO");
            productId = extras.getString(ARG_PRODUCT_ID);

            product = extras.getParcelable(ARG_PRODUCT);
        }

        if (product != null){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction
                    .replace(R.id.container, SpecFragment.newInstance(product))
                    .commit();
        }
//        if (savedInstanceState == null){
//            showProgressDialog();
//            HttpManagerMagentoOld.getInstance().getProductService().getProductDetailMagento(productId, UserInfoManager.getInstance().getUserId(),
//                    getString(R.string.product_detail)).enqueue(CALLBACK_PRODUCT_DETAIL);
//
//        } else {
//            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//            fragmentTransaction
//                    .replace(R.id.container, SpecFragment.newInstance(mSpecDao, mProductDetail))
//                    .commit();
//            showProgressDialog();
//            HttpManagerMagentoOld.getInstance().getProductService().getProductDetailMagento(productId, UserInfoManager.getInstance().getUserId(),
//                    getString(R.string.product_detail)).enqueue(CALLBACK_PRODUCT_DETAIL);
//        }

    }

    private void initView() {
        ButterKnife.bind(this);
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

        //get realm instance
        this.mRealm = database.getRealm();
        int count = database.getCompareProducts().size();

        mBuyCompareView.setListener(this);
        mBuyCompareView.updateCartCount(count);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_SPEC_DAO, mSpecDao);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mSpecDao = savedInstanceState.getParcelable(ARG_SPEC_DAO);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        int count = database.getCompareProducts().size();
        mBuyCompareView.updateCartCount(count);
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            finish();
        }
    }

    @Override
    public void onShoppingBagClick(View view) {
        Intent intent = new Intent(this, CompareActivity.class);
        ActivityCompat.startActivity(this, intent,
                ActivityOptionsCompat
                        .makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight())
                        .toBundle());
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

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtils.createProgressDialog(this);
            mProgressDialog.show();
        } else {
            mProgressDialog.show();
        }
    }
}
