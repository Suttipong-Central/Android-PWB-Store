package cenergy.central.com.pwb_store.realm.seeder

import android.content.Context
import android.support.annotation.RawRes
import android.util.Log
import cenergy.central.com.pwb_store.helpers.ReadFileHelper
import cenergy.central.com.pwb_store.model.Postcode
import cenergy.central.com.pwb_store.realm.RealmController
import com.google.gson.reflect.TypeToken

class PostcodeSeeder(private val context: Context, val database: RealmController, @RawRes private val fileResource: Int) {
    fun seed() {
        Log.i("PostcodeSeeder", "start seed!")
        val results = ReadFileHelper<List<Postcode>>().parseRawJson(context, fileResource,
                object : TypeToken<List<Postcode>>() {}.type, null)
//
//        if (results != null) {
//            for (result in results) {
//                database.storePostcode(result)
//            }
//        }
        Log.i("PostcodeSeeder", "end seed!")
    }
}