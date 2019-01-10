package cenergy.central.com.pwb_store.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.fragment.PaymentHistoryFragment
import cenergy.central.com.pwb_store.fragment.PaymentSuccessFragment
import cenergy.central.com.pwb_store.manager.listeners.HistoryClickListener
import cenergy.central.com.pwb_store.view.LanguageButton
import cenergy.central.com.pwb_store.view.NetworkStateView

class HistoryActivity : BaseActivity(), HistoryClickListener {

    private lateinit var mToolbar: Toolbar
    private lateinit var networkStateView: NetworkStateView

    companion object {
        private const val TAG_FRAGMENT_CATEGORY_DEFAULT = "category_default"

        @JvmStatic
        fun startActivity(context: Context) {
            val intent = Intent(context, HistoryActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        initView()
        setToolbar()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
                .replace(R.id.container, PaymentHistoryFragment.newInstance(), TAG_FRAGMENT_CATEGORY_DEFAULT)
                .commit()
    }

    private fun initView() {
        mToolbar = findViewById(R.id.toolbar)
        networkStateView = findViewById(R.id.networkStateView)
    }

    private fun setToolbar() {
        setSupportActionBar(mToolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }
        mToolbar.setNavigationOnClickListener { finish() }
    }

    override fun onClickHistory(orderResponseId: String) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
                .replace(R.id.container, PaymentSuccessFragment.newInstanceByHistory(orderResponseId), TAG_FRAGMENT_CATEGORY_DEFAULT)
                .commit()
    }

    override fun getSwitchButton(): LanguageButton? = null

    override fun getStateView(): NetworkStateView? = networkStateView
}