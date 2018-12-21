package cenergy.central.com.pwb_store

import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import java.util.*

open class BaseActivity : AppCompatActivity() {

    fun handleChangeLanguage(lang: String) {

        Toast.makeText(this, "Change language to $lang", Toast.LENGTH_SHORT).show()

        val res = resources
        // Change locale settings in the app.
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.setLocale(Locale(lang))
        res.updateConfiguration(conf, dm)
    }
}