package cenergy.central.com.pwb_store.activity

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import cenergy.central.com.pwb_store.extensions.asLiveData
import cenergy.central.com.pwb_store.manager.network.NetworkReceiver
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager
import cenergy.central.com.pwb_store.model.CacheCartItem
import cenergy.central.com.pwb_store.model.CompareProduct
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.view.LanguageButton
import cenergy.central.com.pwb_store.view.NetworkStateView
import cenergy.central.com.pwb_store.view.PowerBuyShoppingCartView
import cenergy.central.com.pwb_store.view.ProductCompareView
import java.util.*


abstract class BaseActivity : AppCompatActivity(), LanguageButton.LanguageListener,
        NetworkReceiver.NetworkStateLister {

    val preferenceManager by lazy { PreferenceManager(this) }
    private var onNetworkReceived: NetworkReceiver? = null
    private var currentLanguage = ""
    var currentState: NetworkInfo.State? = null
    var forceRefresh: Boolean = false // default

    // widget view
    private var languageButton: LanguageButton? = null
    private var shoppingCartView: PowerBuyShoppingCartView? = null

    // data
    private var compareLiveData: LiveData<List<CompareProduct>>? = null
    private var cartItemLiveData: LiveData<List<CacheCartItem>>? = null

    // observer
    private var compareObserver = Observer<List<CompareProduct>> {
        updateCompareCountView(it.size)
    }

    private var shoppingCartObserver = Observer<List<CacheCartItem>> {
        updateShoppingCartView(it)
    }

    override fun onStart() {
        onNetworkReceived = NetworkReceiver(this)
        registerReceiver(onNetworkReceived, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)) // register broadcast
        super.onStart()
    }

    override fun onResume() {
        if (onNetworkReceived == null) {
            onNetworkReceived = NetworkReceiver(this)
            registerReceiver(onNetworkReceived, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)) // register broadcast
        }
        super.onResume()
    }

    override fun onPause() {
        onNetworkReceived?.let {
            unregisterReceiver(it) // unregister broadcast
            this@BaseActivity.onNetworkReceived = null
        }
        super.onPause()
    }

    protected fun observeCartItems() {
        val db = RealmController.getInstance()
        cartItemLiveData = Transformations.map(db.cacheCartItemsAsync.asLiveData()) {
            it
        }
        cartItemLiveData?.observeForever(shoppingCartObserver)
    }

    protected fun observeCompareProducts() {
        val db = RealmController.getInstance()
        compareLiveData = Transformations.map(db.compareProductsAsync.asLiveData()) {
            it
        }
        compareLiveData?.observeForever(compareObserver)
    }

    private fun updateCompareCountView(count: Int) {
        getProductCompareView()?.setCompareCount(count)
    }

    private fun updateShoppingCartView(items: List<CacheCartItem>) {
        var count = 0
        for (item in items) {
            count += item.qty ?: 0
        }
        shoppingCartView?.setBadgeCart(count)
    }

    fun handleChangeLanguage() {
        val language = preferenceManager.getDefaultLanguage()
        if (currentLanguage == language) {
            Log.d("BaseActivity", "same language")
            return
        }

        if (languageButton == null && getSwitchButton() != null) {
            languageButton = getSwitchButton()
            languageButton!!.setDefaultLanguage(language)
            languageButton!!.setOnLanguageChangeListener(this)
        }

        val res = resources
        // Change locale settings in the app.
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.setLocale(Locale(language))
        res.updateConfiguration(conf, dm)

        currentLanguage = language
    }

    abstract fun getProductCompareView(): ProductCompareView?

    abstract fun getSwitchButton(): LanguageButton?

    abstract fun getStateView(): NetworkStateView?

    protected fun setShoppingCartView(view: PowerBuyShoppingCartView) {
        this.shoppingCartView = view
    }

    // region {@link LanguageButton.LanguageListener}
    override fun onChangedLanguage(lang: AppLanguage) {
        preferenceManager.setDefaultLanguage(lang) // save language
        handleChangeLanguage()

        // update product compare view
        getProductCompareView()?.refreshView()

        // update network state view
        if (currentState != null && currentState != NetworkInfo.State.CONNECTED) {
            updateNetworkStateView(currentState!!)
        }
    }
    // endregion

    override fun onNetworkStateChange(state: NetworkInfo.State) {
        currentState = state // update current state

        if (currentState != null) {
            updateNetworkStateView(currentState!!)
        }
    }

    private fun updateNetworkStateView(state: NetworkInfo.State) {
        getStateView()?.let { stateView ->
            when (state) {
                NetworkInfo.State.CONNECTED -> {
                    stateView.onConnected()
                }
                NetworkInfo.State.UNKNOWN, NetworkInfo.State.CONNECTING -> {
                    stateView.onConnecting()
                }
                NetworkInfo.State.DISCONNECTED -> {
                    forceRefresh = true
                    stateView.onDisconnected()
                }
                else -> {
                    stateView.onDisconnected()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compareLiveData?.removeObserver(compareObserver)
        cartItemLiveData?.removeObserver(shoppingCartObserver)
    }

    companion object {
        // request update
        const val REQUEST_UPDATE_LANGUAGE = 4000
        const val RESULT_UPDATE_LANGUAGE = 4001
    }
}