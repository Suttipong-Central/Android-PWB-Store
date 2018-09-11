package cenergy.central.com.pwb_store.realm.seeder

import android.content.Context
import android.support.annotation.RawRes
import cenergy.central.com.pwb_store.helpers.ReadFileHelper
import cenergy.central.com.pwb_store.realm.RealmController
import com.google.gson.reflect.TypeToken
import me.a3cha.android.thaiaddress.models.District

/**
 * Created by Anuphap Suwannamas on 7/9/2018 AD.
 * Email: Anupharpae@gmail.com
 */

class DistrictSeeder(private val context: Context, val database: RealmController, @RawRes private val fileResource: Int) {
    fun seed() {
        // Read data in JSON file and save into Local database
        val results = ReadFileHelper<List<District>>().parseRawJson(context, fileResource,
                object : TypeToken<List<District>>() {}.type, null)

        if (results != null) {
            for (result in results) {
                database.storeDistrict(result)
            }
        }
    }
}