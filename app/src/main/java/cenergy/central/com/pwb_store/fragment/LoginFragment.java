package cenergy.central.com.pwb_store.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.ApiResponseCallback;
import cenergy.central.com.pwb_store.manager.HttpManagerMagento;
import cenergy.central.com.pwb_store.manager.bus.event.LoginSuccessBus;
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager;
import cenergy.central.com.pwb_store.model.APIError;
import cenergy.central.com.pwb_store.model.response.UserResponse;
import cenergy.central.com.pwb_store.realm.RealmController;
import cenergy.central.com.pwb_store.utils.DialogUtils;
import cenergy.central.com.pwb_store.view.PowerBuyEditText;

@SuppressWarnings("unused")
public class LoginFragment extends Fragment implements TextWatcher, View.OnClickListener {
    public static final String TAG = LoginFragment.class.getSimpleName();

    //View Members
    @BindView(R.id.layout_content)
    ViewGroup mLayoutContent;

    @BindView(R.id.username_wrapper)
    TextInputLayout mUserNameWrapper;

    @BindView(R.id.edit_text_username)
    AppCompatAutoCompleteTextView mEditTextUserName;

    @BindView(R.id.password_wrapper)
    TextInputLayout mPasswordWrapper;

    @BindView(R.id.edit_text_password)
    PowerBuyEditText mEditTextPassword;

    @BindView(R.id.card_view_login)
    CardView mLoginButton;

    private ProgressDialog mProgressDialog;
    private String username;
    private String password;

    public LoginFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        initInstances(rootView, savedInstanceState);

//        String username = rootView.<EditText>findViewById(R.id.edit_text_username).getText().toString();
//        String password = rootView.<EditText>findViewById(R.id.edit_text_password).getText().toString();

        // setup onClick login
//        rootView.findViewById(R.id.card_view_login).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                hideSoftKeyboard(v);
//                showProgressDialog();
//                try {
//                    String username = rootView.<EditText>findViewById(R.id.edit_text_username).getText().toString();
//                    String password = rootView.<EditText>findViewById(R.id.edit_text_password).getText().toString();
//
//                    if (username.equals(Contextor.getInstance().getContext().getString(R.string.user)) && password.equals(Contextor.getInstance().getContext().getString(R.string.passwordDetail))) {
//                        EventBus.getDefault().post(new LoginSuccessBus(true));
//                        mProgressDialog.dismiss();
//                    } else {
//                        mProgressDialog.dismiss();
//                        showAlertDialog(Contextor.getInstance().getContext().getString(R.string.error), Contextor.getInstance().getContext().getString(R.string.error_login));
//                    }
//
//                } catch (NullPointerException ex) {
//                    mProgressDialog.dismiss();
//                    Log.e(TAG, "onCardViewLoginClick: ", ex);
///               }
//            }
//        });

        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
//        preferenceManager = new PreferenceManager(getContext());
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        mEditTextUserName = rootView.findViewById(R.id.edit_text_username);
        mEditTextPassword = rootView.findViewById(R.id.edit_text_password);
        mLoginButton = rootView.findViewById(R.id.card_view_login);
        mEditTextUserName.addTextChangedListener(this);
        mEditTextPassword.addTextChangedListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
    }

    /*
     * Restore Instance State Here
     */
    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
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
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        checkLogin();
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    private void checkLogin() {
        if(getContext() != null){
            if (!mEditTextUserName.getText().toString().isEmpty() && !mEditTextPassword.getText().toString().isEmpty()) {
                username = mEditTextUserName.getText().toString();
                password = mEditTextPassword.getText().toString();
                mLoginButton.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.powerBuyOrange));
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
            HttpManagerMagento.Companion.getInstance(getContext()).userLogin(username, password,
                    new ApiResponseCallback<UserResponse>() {
                        @Override
                        public void success(@org.jetbrains.annotations.Nullable UserResponse response) {
                            if (response != null) {
                                if (response.getStore() != null && response.getStore().getPostalCode() != null) {
                                    dismissDialog();
                                    EventBus.getDefault().post(new LoginSuccessBus(true));
                                } else {
                                    dismissDialog();
                                    showAlertDialog("", getString(R.string.some_thing_wrong));
                                    userLogout();
                                }
                            } else {
                                dismissDialog();
                                showAlertDialog("", getString(R.string.some_thing_wrong));
                            }
                        }

                        @Override
                        public void failure(@NotNull APIError error) {
                            dismissDialog();
                            showAlertDialog("", error.getErrorMessage() == null ? getString(R.string.some_thing_wrong) : error.getErrorMessage());
                        }
                    });
        }
    }

    private void clearData() {
        if (getContext() != null) {
            PreferenceManager preferenceManager = new PreferenceManager(getContext());
            preferenceManager.userLogout();
            RealmController.getInstance().userLogout();
        }
    }

    private void userLogout() {
        clearData();
    }

    private void dismissDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
}
