package cenergy.central.com.pwb_store.realm.seeder

import android.content.Context
import android.support.annotation.RawRes
import android.util.Log
import cenergy.central.com.pwb_store.helpers.ReadFileHelper
import cenergy.central.com.pwb_store.model.District
import cenergy.central.com.pwb_store.realm.RealmController
import com.google.gson.reflect.TypeToken

class DistrictSeeder(private val context: Context, val database: RealmController, @RawRes private val fileResource: Int) {
    fun seed() {
        Log.i("DistrictSeeder", "start seed!")
        val results = ReadFileHelper<List<District>>().parseRawJson(context, fileResource,
                object : TypeToken<List<District>>() {}.type, null)

        if (results != null) {
            for (result in results) {
                database.storeDistrict(result)
            }
        }
        Log.i("DistrictSeeder", "end seed!")
    }
}