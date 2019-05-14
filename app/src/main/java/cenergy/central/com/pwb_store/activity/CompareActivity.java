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
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.interfaces.CompareItemListener;
import cenergy.central.com.pwb_store.fragment.CompareFragment;
import cenergy.central.com.pwb_store.manager.ApiResponseCallback;
import cenergy.central.com.pwb_store.manager.HttpManagerMagento;
import cenergy.central.com.pwb_store.manager.bus.event.CompareDetailBus;
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager;
import cenergy.central.com.pwb_store.model.APIError;
import cenergy.central.com.pwb_store.model.CacheCartItem;
import cenergy.central.com.pwb_store.model.CartItem;
import cenergy.central.com.pwb_store.model.CompareProduct;
import cenergy.central.com.pwb_store.model.body.CartBody;
import cenergy.central.com.pwb_store.model.body.CartItemBody;
import cenergy.central.com.pwb_store.realm.DatabaseListener;
import cenergy.central.com.pwb_store.realm.RealmController;
import cenergy.central.com.pwb_store.utils.DialogUtils;
import cenergy.central.com.pwb_store.view.PowerBuyShoppingCartView;

/**
 * Created by napabhat on 7/26/2017 AD.
 */

public class CompareActivity extends AppCompatActivity implements CompareItemListener, PowerBuyShoppingCartView.OnClickListener {

    Toolbar mToolbar;
    PowerBuyShoppingCartView mBuyShoppingCartView;
    private PreferenceManager preferenceManager;
    private ProgressDialog mProgressDialog;
    private RealmController database = RealmController.getInstance();

    @Subscribe
    public void onEvent(CompareDetailBus compareDetailBus) {
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
        mToolbar = findViewById(R.id.toolbar);
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
        updateShoppingCartBadge();
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
        if (database.getCacheCartItems().size() > 0) {
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
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
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
            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    // region {@link {Implement CompareItemListener}
    @Override
    public void onClickShoppingCart(CompareProduct compareProduct) {
        String cartId = preferenceManager.getCartId();
        if (preferenceManager.getCartId() != null) {
            Log.d("ProductDetail", "has cart id");
            addProductToCart(cartId, compareProduct);
        } else {
            Log.d("ProductDetail", "new cart id");
            retrieveCart(compareProduct);
        }
    }
    // endregion

    private void retrieveCart(final CompareProduct compareProduct) {
        showProgressDialog();
        HttpManagerMagento.Companion.getInstance(this).getCart(new ApiResponseCallback<String>() {
            @Override
            public void success(@Nullable String cartId) {
                if (cartId != null) {
                    preferenceManager.setCartId(cartId);
                    addProductToCart(cartId, compareProduct);
                }
            }

            @Override
            public void failure(@NotNull APIError error) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                showAlertDialog("", error.getErrorUserMessage());
            }
        });
    }

    private void addProductToCart(final String cartId, final CompareProduct compareProduct) {
        showProgressDialog();
        CartItemBody cartItemBody = new CartItemBody(new CartBody(cartId, compareProduct.getSku(), 1)); // default add qty 1
        HttpManagerMagento.Companion.getInstance(this).addProductToCart(cartId, cartItemBody, new ApiResponseCallback<CartItem>() {
            @Override
            public void success(@Nullable CartItem cartItem) {
                saveCartItem(cartItem, compareProduct);
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void failure(@NotNull APIError error) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

                if (error.getErrorCode().equals(String.valueOf(APIError.INTERNAL_SERVER_ERROR))) {
                    showClearCartDialog();
                } else {
                    showAlertDialog("", error.getErrorMessage());
                }
            }
        });
    }

    private void saveCartItem(CartItem cartItem, final CompareProduct compareProduct) {
        database.saveCartItem(CacheCartItem.asCartItem(cartItem, compareProduct), new DatabaseListener() {
            @Override
            public void onSuccessfully() {
                updateShoppingCartBadge();
                Toast.makeText(CompareActivity.this, getString(R.string.added_to_cart), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable error) {
                mProgressDialog.dismiss();
                if (error != null) {
                    showAlertDialog("", error.getMessage());
                }
            }
        });
    }

    private void updateShoppingCartBadge() {
        int count = 0;
        List<CacheCartItem> items = database.getCacheCartItems();
        for (CacheCartItem item : items) {
            if (item.getQty() != null) {
                count += item.getQty();
            }
        }
        mBuyShoppingCartView.setBadgeCart(count);
        Log.d("ProductDetail", "count shopping badge" + count);
    }


    private void showClearCartDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(getString(R.string.title_clear_cart))
                .setPositiveButton(getString(R.string.ok_alert), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearCart(); // clear item cart
                        updateShoppingCartBadge(); // update ui
                    }
                });
        builder.show();
    }

    private void clearCart() {
        database.deleteAllCacheCartItem();
        preferenceManager.clearCartId();
    }

}
