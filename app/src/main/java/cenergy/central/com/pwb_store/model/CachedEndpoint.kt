package cenergy.central.com.pwb_store.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by Anuphap Suwannamas on 8/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

open class CachedEndpoint : RealmObject() {
    @PrimaryKey
    var endpoint = ""
    var lastUpdated: Date? = null
}