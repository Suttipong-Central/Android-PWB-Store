package cenergy.central.com.pwb_store.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.Contextor;
import cenergy.central.com.pwb_store.manager.bus.event.LoginSuccessBus;
import cenergy.central.com.pwb_store.utils.DialogUtils;
import cenergy.central.com.pwb_store.view.PowerBuyEditText;

@SuppressWarnings("unused")
public class LoginFragment extends Fragment {
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
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here

    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        ButterKnife.bind(this, rootView);
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

    @OnClick(R.id.card_view_login)
    public void onCardViewLoginClick(CardView cardView) {
        hideSoftKeyboard(cardView);
        showProgressDialog();
        try {
            String username = mUserNameWrapper.getEditText().getText().toString();
            String password = mPasswordWrapper.getEditText().getText().toString();

            if (username.equals(Contextor.getInstance().getContext().getString(R.string.user)) &&
                    password.equals(Contextor.getInstance().getContext().getString(R.string.passwordDetail))) {
                EventBus.getDefault().post(new LoginSuccessBus(true));
                mProgressDialog.dismiss();
            } else {
                mProgressDialog.dismiss();
                showAlertDialog(Contextor.getInstance().getContext().getString(R.string.error), Contextor.getInstance().getContext().getString(R.string.error_login));
            }

        } catch (NullPointerException ex) {
            mProgressDialog.dismiss();
            Log.e(TAG, "onCardViewLoginClick: ", ex);
        }
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
}
