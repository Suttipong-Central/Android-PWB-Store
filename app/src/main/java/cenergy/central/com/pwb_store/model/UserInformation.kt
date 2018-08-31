package cenergy.central.com.pwb_store.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Anuphap Suwannamas on 31/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

open class UserInformation(@PrimaryKey
                           var userId: Long = 0,
                           var user: User? = null,
                           var stores: RealmList<Store>? = null):RealmObject() {
    companion object {
        const val FIELD_USER_ID = "userId"
    }
}