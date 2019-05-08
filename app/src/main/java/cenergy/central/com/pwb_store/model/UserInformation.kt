package cenergy.central.com.pwb_store.model

import cenergy.central.com.pwb_store.model.response.LoginUserResponse
import cenergy.central.com.pwb_store.model.response.UserBranch
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Anuphap Suwannamas on 31/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

open class UserInformation(@PrimaryKey
                           var userId: Long = 0,
                           var user: User? = null,
                           var store: Store? = null,
                           var userResponse: LoginUserResponse? = null,
                           var userBranch: UserBranch? = null) : RealmObject()