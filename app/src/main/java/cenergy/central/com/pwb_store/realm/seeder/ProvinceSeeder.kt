package cenergy.central.com.pwb_store.realm.seeder

import android.content.Context
import android.support.annotation.RawRes
import android.util.Log
import cenergy.central.com.pwb_store.helpers.ReadFileHelper
import cenergy.central.com.pwb_store.model.Province
import cenergy.central.com.pwb_store.realm.RealmController
import com.google.gson.reflect.TypeToken

class ProvinceSeeder(private val context: Context, val database: RealmController, @RawRes private val fileResource: Int) {
    fun seed() {
        Log.i("ProvinceSeeder", "start seed!")

        // Read data in JSON file and save into Local database
        val results = ReadFileHelper<List<Province>>().parseRawJson(context, fileResource,
                object : TypeToken<List<Province>>() {}.type, null)

        if (results != null) {
            for (result in results) {
                database.storeProvince(result)
            }
        }
        Log.i("ProvinceSeeder", "end seed!")
    }
}