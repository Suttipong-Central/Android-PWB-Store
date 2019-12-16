package cenergy.central.com.pwb_store.activity

import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.appcompat.app.AppCompatActivity
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.DeepLink

class SplashScreenActivity : AppCompatActivity() {
    val preferenceManager by lazy { PreferenceManager(this) }
    private lateinit var database: RealmController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = RealmController.getInstance()

        if (database.userToken != null) {
            checkStart(intent)
        } else {
            forceLogin(intent)
        }
    }

    private fun checkStart(intent: Intent) {
        val action = intent.action
        val data = intent.dataString

        if (Intent.ACTION_VIEW != action || data == null) {
            start()
            return
        }

        if (intent.data != null && intent.data!!.pathSegments.isNotEmpty()){
            DeepLink(this).checkIntent(
                    intent.data!!.pathSegments.toTypedArray(),
                    intent.data!!,
                    data)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { checkStart(intent) }
    }

    private fun forceLogin(intent: Intent) {
        // start login page
        val intentLogin = Intent(this, LoginActivity::class.java)
        if (intent.data != null && intent.data!!.pathSegments.isNotEmpty()){
            intentLogin.putExtra(DeepLink.DEEP_LINK_EXTRA_PATH_SEGMENTS, intent.data!!.pathSegments.toTypedArray())
            intentLogin.putExtra(DeepLink.DEEP_LINK_EXTRA_URI, intent.data!!)
            intentLogin.putExtra(DeepLink.DEEP_LINK_EXTRA_LINK, intent.dataString)
        }
        startActivity(intentLogin)
        finish()

    }

    private fun start() {
        // start main page
        val intent = Intent(this, MainActivity::class.java)
        ActivityCompat.startActivity(this, intent,
                ActivityOptionsCompat.makeBasic().toBundle())
        finish()
    }
}
