package cenergy.central.com.pwb_store.extensions

import android.content.Context
import cenergy.central.com.pwb_store.model.Store
import cenergy.central.com.pwb_store.realm.RealmController

fun Context.getUserStore(): Store? {
    val database = RealmController.getInstance()
    val userInfo = database.userInformation
    return userInfo.store
}