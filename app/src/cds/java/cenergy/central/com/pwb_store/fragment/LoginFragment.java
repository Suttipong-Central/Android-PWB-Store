package cenergy.central.com.pwb_store.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import org.greenrobot.eventbus.EventBus;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.bus.event.LoginSuccessBus;
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager;
import cenergy.central.com.pwb_store.model.Store;
import cenergy.central.com.pwb_store.model.User;
import cenergy.central.com.pwb_store.model.UserInformation;
import cenergy.central.com.pwb_store.model.UserToken;
import cenergy.central.com.pwb_store.realm.RealmController;
import cenergy.central.com.pwb_store.utils.DialogUtils;
import cenergy.central.com.pwb_store.view.PowerBuyEditText;

@SuppressWarnings("unused")
public class LoginFragment extends Fragment implements TextWatcher, View.OnClickListener {
    public static final String TAG = LoginFragment.class.getSimpleName();

    //View Members
    private PowerBuyEditText mEditTextUserName;
    private PowerBuyEditText mEditTextPassword;
    private CardView mLoginButton;
    private ProgressDialog mProgressDialog;
    private String username;
    private String password;

    @SuppressWarnings("unused")
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        mEditTextUserName = rootView.findViewById(R.id.edit_text_username);
        mEditTextPassword = rootView.findViewById(R.id.edit_text_password);
        mLoginButton = rootView.findViewById(R.id.card_view_login);
        mEditTextUserName.addTextChangedListener(this);
        mEditTextPassword.addTextChangedListener(this);
    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        builder.show();
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtils.createProgressDialog(getContext());
            mProgressDialog.show();
        } else {
            mProgressDialog.show();
        }
    }

    private void hideSoftKeyboard(View view) {
        // Check if no view has focus:
        if (view != null && getContext() != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        checkLogin();
    }

    @Override
    public void afterTextChanged(Editable s) { }

    private void checkLogin() {
        if(getContext() != null){
            if (!mEditTextUserName.getText().toString().isEmpty() && !mEditTextPassword.getText().toString().isEmpty()) {
                username = mEditTextUserName.getText().toString();
                password = mEditTextPassword.getText().toString();
                mLoginButton.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.primaryButtonColor));
                mLoginButton.setOnClickListener(this);
            } else {
                mLoginButton.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.hintColor));
                mLoginButton.setOnClickListener(null);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.card_view_login) {
            hideSoftKeyboard(v);
            showProgressDialog();
            login();
        }
    }

    private void login() {
        if (getContext() != null) {

            // TODO Call Login API and remove realm go to use it in magento manager
            RealmController database = RealmController.getInstance();

            //TODO: Mock up data will delete soon
            User user = new User(0, "", "0", 223L,
                    "chuan@central.tech", "", "", 1, "");
            Store store = new Store();
            store.setStoreId(223L);
            store.setStoreCode("");
            store.setStoreName("");
            store.setRetailerId("");

            // save user token
            database.saveUserToken( new UserToken(""));
            // save user information
            UserInformation userInformation = new UserInformation( 0,  user, store);
            database.saveUserInformation(userInformation);

            dismissDialog();
            EventBus.getDefault().post(new LoginSuccessBus(true));
        }
    }

    private void userLogout() {
        clearData();
    }

    private void clearData() {
        if (getContext() != null) {
            PreferenceManager preferenceManager = new PreferenceManager(getContext());
            preferenceManager.userLogout();
            RealmController.getInstance().userLogout();
        }
    }

    private void dismissDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
}
