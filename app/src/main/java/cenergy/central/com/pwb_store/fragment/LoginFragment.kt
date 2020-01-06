package cenergy.central.com.pwb_store.fragment

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager

import org.greenrobot.eventbus.EventBus

import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.manager.bus.event.LoginSuccessBus
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.UserInformation
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.DialogUtils
import cenergy.central.com.pwb_store.utils.showCommonAPIErrorDialog
import cenergy.central.com.pwb_store.utils.showCommonDialog
import cenergy.central.com.pwb_store.view.PowerBuyEditText
import cenergy.central.com.pwb_store.view.PowerBuyIconButton

class LoginFragment : Fragment(), TextWatcher, View.OnClickListener {

    //View Members
    private lateinit var mEditTextUserName: PowerBuyEditText
    private lateinit var mEditTextPassword: PowerBuyEditText
    private lateinit var mLoginButton: PowerBuyIconButton
    private var mProgressDialog: ProgressDialog? = null
    private var username: String = ""
    private var password: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_login, container, false)
        setupView(rootView)
        return rootView
    }

    private fun setupView(rootView: View) {
        mEditTextUserName = rootView.findViewById(R.id.edit_text_username)
        mEditTextPassword = rootView.findViewById(R.id.edit_text_password)
        mLoginButton = rootView.findViewById(R.id.loginButton)
        mEditTextUserName.addTextChangedListener(this)
        mEditTextPassword.addTextChangedListener(this)
        checkLogin()
    }

    private fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtils.createProgressDialog(context)
            mProgressDialog?.show()
        } else {
            mProgressDialog?.show()
        }
    }

    private fun hideSoftKeyboard(view: View?) {
        // Check if no view has focus:
        if (view != null && context != null) {
            val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        checkLogin()
    }

    override fun afterTextChanged(s: Editable) {}

    private fun checkLogin() {
        if (context != null) {
            if (!mEditTextUserName.text.isNullOrEmpty() && !mEditTextPassword.text.isNullOrEmpty()) {
                username = mEditTextUserName.text.toString()
                password = mEditTextPassword.text.toString()
                mLoginButton.setButtonDisable(false)
                mLoginButton.setOnClickListener(this)
            } else {
                mLoginButton.setButtonDisable(true)
                mLoginButton.setOnClickListener(null)
            }
        }
    }

    override fun onClick(v: View) {
        if (v.id == R.id.loginButton) {
            hideSoftKeyboard(v)
            showProgressDialog()
            login()
        }
    }

    private fun login() {
        if (context != null) {
            HttpManagerMagento.getInstance(context!!).userLogin(username, password,
                    object : ApiResponseCallback<UserInformation> {
                        override fun success(response: UserInformation?) {
                            if (response != null) {
                                if (checkUserLogin(response)) {
                                    dismissDialog()
                                    EventBus.getDefault().post(LoginSuccessBus(true, response))
                                } else {
                                    dismissDialog()
                                    activity?.showCommonDialog(getString(R.string.some_thing_wrong))
                                    userLogout()
                                }
                            }
                        }

                        override fun failure(error: APIError) {
                            dismissDialog()
                            activity?.showCommonAPIErrorDialog(error)
                        }
                    })
        }
    }

    private fun checkUserLogin(userInformation: UserInformation): Boolean {
        return userInformation.store != null && userInformation.user != null &&
                userInformation.user!!.staffId != null && userInformation.user!!.staffId != "" &&
                userInformation.user!!.staffId != "0" && userInformation.store!!.retailerId != ""
    }

    private fun clearData() {
        if (context != null) {
            val preferenceManager = PreferenceManager(context!!)
            preferenceManager.userLogout()
            RealmController.getInstance().userLogout()
        }
    }

    private fun userLogout() {
        clearData()
    }

    private fun dismissDialog() {
        if (mProgressDialog != null) {
            mProgressDialog!!.dismiss()
        }
    }

    companion object {
        val TAG: String = LoginFragment::class.java.simpleName

        fun newInstance(): LoginFragment {
            val fragment = LoginFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
