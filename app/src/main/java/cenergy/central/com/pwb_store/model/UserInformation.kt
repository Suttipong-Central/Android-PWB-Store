package cenergy.central.com.pwb_store.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class UserInformation(@PrimaryKey
                           var userId: Long = 0,
                           var user: User? = null,
                           var store: Store? = null) : RealmObject()