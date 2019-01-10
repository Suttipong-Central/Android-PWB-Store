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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.fragment.AvailableFragment;
import cenergy.central.com.pwb_store.manager.HttpManagerHDLOld;
import cenergy.central.com.pwb_store.manager.HttpManagerMagentoOld;
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage;
import cenergy.central.com.pwb_store.model.APIError;
import cenergy.central.com.pwb_store.model.AvailableStoreDao;
import cenergy.central.com.pwb_store.model.StoreDao;
import cenergy.central.com.pwb_store.model.StoreList;
import cenergy.central.com.pwb_store.utils.APIErrorUtils;
import cenergy.central.com.pwb_store.utils.DialogUtils;
import cenergy.central.com.pwb_store.view.LanguageButton;
import cenergy.central.com.pwb_store.view.NetworkStateView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by napabhat on 7/26/2017 AD.
 */

public class AvaliableStoreActivity extends BaseActivity{
    private static final String TAG = AvaliableStoreActivity.class.getSimpleName();

    public static final String ARG_SKU = "ARG_SKU";
    public static final String ARG_AVALIABLEDAO = "ARG_AVALIABLEDAO";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

//    private LanguageButton languageButton;
    private NetworkStateView networkStateView;
    private String sku;
    private AvailableStoreDao mAvailableStoreDao;
    private StoreDao mStoreDao;
    private ProgressDialog mProgressDialog;

//    final Callback<List<AvaliableStoreItem>> CALLBACK_AVALIABLE = new Callback<List<AvaliableStoreItem>>() {
//        @Override
//        public void onResponse(Call<List<AvaliableStoreItem>> call, Response<List<AvaliableStoreItem>> response) {
//            if (response.isSuccessful()){
//                mAvaliableStoreDao = new AvaliableStoreDao(response.body());
//                dismissProgressDialog();
//
//                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                fragmentTransaction
//                        .replace(R.id.container, AvailableFragment.newInstance(mAvaliableStoreDao))
//                        .commit();
//            }else {
//                    APIError error = APIErrorUtils.parseError(response);
//                    Log.e(TAG, "onResponse: " + error.getErrorMessage());
//                    showAlertDialog(error.getErrorMessage(), false);
//                    dismissProgressDialog();
//            }
//        }
//
//        @Override
//        public void onFailure(Call<List<AvaliableStoreItem>> call, Throwable t) {
//            Log.e(TAG, "onFailure: ", t);
//            dismissProgressDialog();
//        }
//    };

    final Callback<AvailableStoreDao> CALLBACK_AVALIABLE = new Callback<AvailableStoreDao>() {
        @Override
        public void onResponse(Call<AvailableStoreDao> call, Response<AvailableStoreDao> response) {
            if (response.isSuccessful()){
                mAvailableStoreDao = response.body();
                if(!isFinishing() && getSupportFragmentManager() != null){
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction
                            .replace(R.id.container, AvailableFragment.newInstance(mAvailableStoreDao, mStoreDao))
                            .commit();
                }
                dismissProgressDialog();
            }else {
                APIError error = APIErrorUtils.parseError(response);
                Log.e(TAG, "onResponse: " + error.getErrorMessage());
                showAlertDialog(error.getErrorMessage(), false);
                dismissProgressDialog();
            }

        }

        @Override
        public void onFailure(Call<AvailableStoreDao> call, Throwable t) {
            Log.e(TAG, "onFailure: ", t);
            dismissProgressDialog();
        }
    };

    final Callback<List<StoreList>> CALLBACK_STORE_LIST = new Callback<List<StoreList>>() {
        @Override
        public void onResponse(Call<List<StoreList>> call, Response<List<StoreList>> response) {
            if (response.isSuccessful()){
                mStoreDao = new StoreDao(response.body());
                HttpManagerHDLOld.getInstance().getHDLService().getStore(sku).enqueue(CALLBACK_AVALIABLE);
            }else {
                APIError error = APIErrorUtils.parseError(response);
                Log.e(TAG, "onResponse: " + error.getErrorMessage());
                dismissProgressDialog();
            }
        }

        @Override
        public void onFailure(Call<List<StoreList>> call, Throwable t) {
            Log.e(TAG, "onFailure: ", t);
            dismissProgressDialog();
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
            //HttpManagerMagento.getInstance().getStoreService().getAvailableStock(sku).enqueue(CALLBACK_AVALIABLE);
            retrieveChangeLanguage();
        }else {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction
                    .replace(R.id.container, AvailableFragment.newInstance(mAvailableStoreDao, mStoreDao))
                    .commit();
        }

    }

    private void retrieveChangeLanguage() {
        HttpManagerMagentoOld.getInstance().getStoreService().getStore().enqueue(CALLBACK_STORE_LIST);
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
//        languageButton = findViewById(R.id.switch_language_button);
        networkStateView = findViewById(R.id.networkStateView);
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
        if (!isFinishing()) {
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

    private void showProgressDialog() {
        if (!isFinishing()) {
            if (mProgressDialog == null) {
                mProgressDialog = DialogUtils.createProgressDialog(this);
                mProgressDialog.show();
            } else {
                mProgressDialog.show();
            }
        }
    }

    private void dismissProgressDialog() {
        if (!isFinishing() && mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Nullable
    @Override
    public NetworkStateView getStateView() { return networkStateView; }

    @Nullable
    @Override
    public LanguageButton getSwitchButton() {
        return null;
    }
}
