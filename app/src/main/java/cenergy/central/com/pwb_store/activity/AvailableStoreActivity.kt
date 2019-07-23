package cenergy.central.com.pwb_store.activity

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.TextView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.fragment.AvailableFragment
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.StoreAvailable
import cenergy.central.com.pwb_store.utils.APIErrorUtils
import cenergy.central.com.pwb_store.utils.DialogUtils
import cenergy.central.com.pwb_store.view.LanguageButton
import cenergy.central.com.pwb_store.view.NetworkStateView

class AvailableStoreActivity : BaseActivity(), AvailableProtocol {

    private lateinit var mToolbar: Toolbar
    private lateinit var languageButton: LanguageButton
    private lateinit var networkStateView: NetworkStateView
    private var sku: String? = null
    private var storeAvailableList: List<StoreAvailable> = arrayListOf()
    private var mProgressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_avaliable_store)

        val mIntent = intent
        val extras = mIntent.extras
        if (extras != null) {
            sku = extras.getString(ARG_SKU)
        }

        initView()
        handleChangeLanguage()

        if (savedInstanceState == null) {
            retrieveStoreStocks()
        } else {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction
                    .replace(R.id.container, AvailableFragment.newInstance())
                    .commit()
        }

    }

    private fun retrieveStoreStocks() {
        showProgressDialog()
        HttpManagerMagento.getInstance(this).getAvailableStore(sku!!, object : ApiResponseCallback<List<StoreAvailable>> {
            override fun success(response: List<StoreAvailable>?) {
                runOnUiThread {
                    dismissProgressDialog()
                    if (response != null) {
                        storeAvailableList = response
                        val fragmentTransaction = supportFragmentManager.beginTransaction()
                        fragmentTransaction.replace(R.id.container, AvailableFragment.newInstance())
                                .commit()
                    } else {
                        val error = APIErrorUtils.parseError(response)
                        Log.e(TAG, "onResponse: " + error.errorMessage)
                        showAlertDialog(error.errorMessage, false)
                    }
                }
            }

            override fun failure(error: APIError) {
                runOnUiThread {
                    dismissProgressDialog()
                    showAlertDialog(error.errorMessage, false)
                }
            }
        })
    }

    private fun initView() {
        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }
        mToolbar.setNavigationOnClickListener { finish() }
        languageButton = findViewById(R.id.switch_language_button)
        networkStateView = findViewById(R.id.networkStateView)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(ARG_SKU, sku)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        sku = savedInstanceState.getString(ARG_SKU)
    }

    private fun showAlertDialog(message: String, shouldCloseActivity: Boolean) {
        if (!isFinishing) {
            val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
                    .setMessage(message)
                    .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                        dialog.dismiss()
                        if (shouldCloseActivity) finish()
                    }
            builder.show()
        }
    }

    private fun showProgressDialog() {
        if (!isFinishing) {
            if (mProgressDialog == null) {
                mProgressDialog = DialogUtils.createProgressDialog(this)
                mProgressDialog!!.show()
            } else {
                mProgressDialog!!.show()
            }
        }
    }

    private fun dismissProgressDialog() {
        if (!isFinishing && mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }

    override fun onChangedLanguage(lang: AppLanguage) {
        super.onChangedLanguage(lang)
        updateView()
        retrieveStoreStocks()
    }

    override fun getStoreAvailable() = this.storeAvailableList

    override fun getStateView(): NetworkStateView? = networkStateView

    override fun getSwitchButton(): LanguageButton? = languageButton

    private fun updateView() {
        mToolbar.findViewById<TextView>(R.id.txt_header).text = getString(R.string.store_avaliable)
    }

    companion object {
        private val TAG = AvailableStoreActivity::class.java.simpleName

        const val ARG_SKU = "ARG_SKU"
    }
}

interface AvailableProtocol {
    fun getStoreAvailable(): List<StoreAvailable>
}
