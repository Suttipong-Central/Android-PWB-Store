package cenergy.central.com.pwb_store.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.util.Log
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.fragment.PaymentHistoryFragment
import cenergy.central.com.pwb_store.fragment.PaymentSuccessFragment
import cenergy.central.com.pwb_store.manager.listeners.HistoryClickListener
import cenergy.central.com.pwb_store.view.LanguageButton
import cenergy.central.com.pwb_store.view.NetworkStateView

class HistoryActivity : BaseActivity(), HistoryClickListener {

    private lateinit var mToolbar: Toolbar
    private lateinit var networkStateView: NetworkStateView

    private var currentFragment: Fragment? = null

    companion object {
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
        startHistoryFragment()
    }

    private fun initView() {
        mToolbar = findViewById(R.id.toolbar)
        networkStateView = findViewById(R.id.networkStateView)
        mToolbar.setNavigationOnClickListener {
            backPressed()
        }
    }

    private fun setToolbar() {
        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        mToolbar.setNavigationOnClickListener { backPressed() }
    }

    override fun onClickHistory(orderResponseId: String) {
        startSuccessFragment(orderResponseId)
    }

    private fun startHistoryFragment() {
        val fragment = PaymentHistoryFragment.newInstance()
        startFragment(fragment)
    }

    private fun startSuccessFragment(orderID: String) {
        val fragment = PaymentSuccessFragment.newInstanceByHistory(orderID)
        startFragment(fragment)
    }

    private fun startFragment(fragment: Fragment) {
        currentFragment = fragment
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment).commit()
    }

    override fun onBackPressed() {
        backPressed()
    }

    private fun backPressed() {
        if (currentFragment is PaymentHistoryFragment) {
            finish()
            return
        }
        if (currentFragment is PaymentSuccessFragment) {
            startHistoryFragment()
            return
        }
    }

    override fun getSwitchButton(): LanguageButton? = null

    override fun getStateView(): NetworkStateView? = networkStateView
}