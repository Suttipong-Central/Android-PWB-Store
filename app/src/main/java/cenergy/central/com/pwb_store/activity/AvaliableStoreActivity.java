package cenergy.central.com.pwb_store.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.fragment.AvaliableFragment;
import cenergy.central.com.pwb_store.manager.HttpManagerMagento;
import cenergy.central.com.pwb_store.model.APIError;
import cenergy.central.com.pwb_store.model.AvaliableStoreDao;
import cenergy.central.com.pwb_store.model.AvaliableStoreItem;
import cenergy.central.com.pwb_store.utils.APIErrorUtils;
import cenergy.central.com.pwb_store.utils.DialogUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by napabhat on 7/26/2017 AD.
 */

public class AvaliableStoreActivity extends AppCompatActivity{
    private static final String TAG = AvaliableStoreActivity.class.getSimpleName();

    public static final String ARG_SKU = "ARG_SKU";
    public static final String ARG_AVALIABLEDAO = "ARG_AVALIABLEDAO";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private String sku;
    private AvaliableStoreDao mAvaliableStoreDao;
    private ProgressDialog mProgressDialog;

    final Callback<List<AvaliableStoreItem>> CALLBACK_AVALIABLE = new Callback<List<AvaliableStoreItem>>() {
        @Override
        public void onResponse(Call<List<AvaliableStoreItem>> call, Response<List<AvaliableStoreItem>> response) {
            if (response.isSuccessful()){
                mAvaliableStoreDao = new AvaliableStoreDao(response.body());
                mProgressDialog.dismiss();

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction
                        .replace(R.id.container, AvaliableFragment.newInstance(mAvaliableStoreDao))
                        .commit();
            }else {
                    APIError error = APIErrorUtils.parseError(response);
                    Log.e(TAG, "onResponse: " + error.getErrorMessage());
                    showAlertDialog(error.getErrorMessage(), false);
                    mProgressDialog.dismiss();
            }
        }

        @Override
        public void onFailure(Call<List<AvaliableStoreItem>> call, Throwable t) {
            Log.e(TAG, "onFailure: ", t);
            mProgressDialog.dismiss();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avaliable_store);

        Intent mIntent = getIntent();
        Bundle extras = mIntent.getExtras();
        if (extras != null) {
            sku = extras.getString(ARG_SKU);
            Log.d(TAG, "sku : " + sku);
        }

        initView();

        if (savedInstanceState == null){
            showProgressDialog();
            HttpManagerMagento.getInstance().getStoreService().getAvailableStock(sku).enqueue(CALLBACK_AVALIABLE);
        }else {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction
                    .replace(R.id.container, AvaliableFragment.newInstance(mAvaliableStoreDao))
                    .commit();
        }

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

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(ARG_SKU, sku);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        sku = savedInstanceState.getString(ARG_SKU);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            finish();
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

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtils.createProgressDialog(this);
            mProgressDialog.show();
        } else {
            mProgressDialog.show();
        }
    }
}
