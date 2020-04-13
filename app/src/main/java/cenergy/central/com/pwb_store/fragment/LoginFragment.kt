package cenergy.central.com.pwb_store.fragment

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.Constants
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.fragment.interfaces.UserLoginListener
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.manager.bus.event.LoginSuccessBus
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager
import cenergy.central.com.pwb_store.model.*
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.DialogUtils
import cenergy.central.com.pwb_store.utils.showCommonAPIErrorDialog
import cenergy.central.com.pwb_store.utils.showCommonDialog
import cenergy.central.com.pwb_store.view.PowerBuyEditText
import cenergy.central.com.pwb_store.view.PowerBuyIconButton
import com.google.firebase.firestore.FirebaseFirestore
import org.greenrobot.eventbus.EventBus

class LoginFragment : Fragment(), TextWatcher, View.OnClickListener {

    private val firestoreDB = FirebaseFirestore.getInstance()
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var listener: UserLoginListener

    //View Members
    private lateinit var mEditTextUserName: PowerBuyEditText
    private lateinit var mEditTextPassword: PowerBuyEditText
    private lateinit var mLoginButton: PowerBuyIconButton
    private var mProgressDialog: ProgressDialog? = null
    private var username: String = ""
    private var password: String = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        preferenceManager = PreferenceManager(context)
        listener = context as UserLoginListener
    }

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
        context?.let {
            if (BuildConfig.FLAVOR == "cds"){
                mockUpUser()
            } else {
                HttpManagerMagento.getInstance(it).userLogin(username, password,
                        object : ApiResponseCallback<UserInformation> {
                            override fun success(response: UserInformation?) {
                                if (response != null) {
                                    dismissDialog()
                                    if (checkUserLogin(response)) {
                                        retrieveSecretKey(response)
                                    } else {
                                        activity?.showCommonDialog(getString(R.string.some_thing_wrong))
                                        listener.userLogOut()
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
    }

    private fun retrieveSecretKey(userInformation: UserInformation) {
        firestoreDB.collection(Constants.FIRE_STORE_COLLECTION_NAME).document(Constants.FIRE_STORE_DOCUMENT_KEY).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val secretKey = document.toObject(SecretKey::class.java)
                        if (checkSecretKey(secretKey)) {
                            preferenceManager.setSecretKey(secretKey!!)
                            dismissDialog()
                            EventBus.getDefault().post(LoginSuccessBus(true, userInformation))
                        } else {
                            Log.d("Firestore", "secretKey is null!")
                            dismissDialog()
                            listener.userLogOut()
                        }
                    } else {
                        Log.d("Firestore", "document is null!")
                        dismissDialog()
                        listener.userLogOut()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Firestore", "error -> ${exception.message}")
                    dismissDialog()
                    listener.userLogOut()
                }
    }

    private fun mockUpUser() {
        val database = RealmController.getInstance()
        val user = User(12345678L, "", "12345678", 223L,
                "chuan@central.tech", username, "", 0, "", 1)
        val store = Store()

        database.saveUserToken(UserToken(Constants.CLIENT_MAGENTO))
        // save user information
        val userInformation = UserInformation(user.userId, user, store)
        database.saveUserInformation(userInformation)
        dismissDialog()
        EventBus.getDefault().post(LoginSuccessBus(true, userInformation))
    }

    private fun checkSecretKey(secretKey: SecretKey?): Boolean{
        return secretKey?.accessKey != null && secretKey.secretKey != null && secretKey.region != null &&
                secretKey.xApiKey != null && secretKey.serviceName != null
    }

    private fun checkUserLogin(userInformation: UserInformation): Boolean {
        return userInformation.store != null && userInformation.user != null &&
                userInformation.user!!.staffId != null && userInformation.user!!.staffId != "" &&
                userInformation.user!!.staffId != "0" && userInformation.store!!.retailerId != ""
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
