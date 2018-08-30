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
import android.text.TextUtils;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.fragment.CompareFragment;
import cenergy.central.com.pwb_store.manager.bus.event.CompareDetailBus;
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager;
import cenergy.central.com.pwb_store.realm.RealmController;
import cenergy.central.com.pwb_store.utils.DialogUtils;
import cenergy.central.com.pwb_store.view.PowerBuyShoppingCartView;

/**
 * Created by napabhat on 7/26/2017 AD.
 */

public class CompareActivity extends AppCompatActivity implements PowerBuyShoppingCartView.OnClickListener {

    Toolbar mToolbar;
    PowerBuyShoppingCartView mBuyShoppingCartView;
    private PreferenceManager preferenceManager;
    private ProgressDialog mProgressDialog;

    @Subscribe
    public void onEvent(CompareDetailBus compareDetailBus){
//        Intent intent = new Intent(this, ProductDetailActivity.class);
//        intent.putExtra(ProductDetailActivity.ARG_PRODUCT_ID, compareDetailBus.getProductCompareList().getProductId());
//        ActivityCompat.startActivity(this, intent,
//                ActivityOptionsCompat
//                        .makeScaleUpAnimation(compareDetailBus.getView(), 0, 0, compareDetailBus.getView().getWidth(), compareDetailBus.getView().getHeight())
//                        .toBundle());

        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.ARG_PRODUCT_SKU, compareDetailBus.getCompareProduct().getSku());
        ActivityCompat.startActivity(this, intent,
                ActivityOptionsCompat
                        .makeScaleUpAnimation(compareDetailBus.getView(), 0, 0, compareDetailBus.getView().getWidth(), compareDetailBus.getView().getHeight())
                        .toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        preferenceManager = new PreferenceManager(this); // get pref

        initView();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction
                .replace(R.id.container, CompareFragment.newInstance())
                .commit();
    }

    private void initView() {
        mToolbar  = findViewById(R.id.toolbar);
        mBuyShoppingCartView = findViewById(R.id.shopping_cart_compare);
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
        mBuyShoppingCartView.setListener(this);
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
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            finish();
        }
    }

    @Override
    public void onShoppingCartClick(View view) {
        showProgressDialog();
        if (RealmController.with(this).getCacheCartItems().size() > 0) {
            ShoppingCartActivity.Companion.startActivity(this, view, preferenceManager.getCartId());
        } else {
            showAlertDialog("", getResources().getString(R.string.not_have_products_in_cart));
        }
    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok_alert), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mProgressDialog.dismiss();
                    }
                });

        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
