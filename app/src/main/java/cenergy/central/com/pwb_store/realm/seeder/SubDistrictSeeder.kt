package cenergy.central.com.pwb_store.realm.seeder

import android.content.Context
import android.support.annotation.RawRes
import android.util.Log
import cenergy.central.com.pwb_store.helpers.ReadFileHelper
import cenergy.central.com.pwb_store.model.SubDistrict
import cenergy.central.com.pwb_store.realm.RealmController
import com.google.gson.reflect.TypeToken

class SubDistrictSeeder(private val context: Context, val database: RealmController, @RawRes private val fileResource: Int) {
    fun seed() {
        Log.i("SubDistrictSeeder", "start seed!")
        val results = ReadFileHelper<List<SubDistrict>>().parseRawJson(context, fileResource,
                object : TypeToken<List<SubDistrict>>() {}.type, null)

        if (results != null) {
            for (result in results) {
                database.storeSubDistrict(result)
            }
        }
        Log.i("SubDistrictSeeder", "end seed!")
    }
}