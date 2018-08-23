package cenergy.central.com.pwb_store.manager.preferences

import android.content.Context
import android.content.SharedPreferences
import cenergy.central.com.pwb_store.R

/**
 * Created by Anuphap Suwannamas on 23/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */
class PreferenceManager(private var context: Context) {
    private var pref: SharedPreferences = context.getSharedPreferences(this.context.getString(R.string.preference_file_name), Context.MODE_PRIVATE)

    val cartId: String?
        get() = pref.getString(PREF_CART_ID, null)

    fun setCartId(cartId: String?) {
        val editor = pref.edit()
        editor.putString(PREF_CART_ID, cartId)
        editor.apply()
    }

    fun clearCartId() {
        pref.edit().clear().apply()
    }

    companion object {
        const val PREF_CART_ID = "cart_id"
    }
}