package cenergy.central.com.pwb_store.model.response

import cenergy.central.com.pwb_store.model.Store
import cenergy.central.com.pwb_store.model.User

class UserResponse(
        var status: String,
        var user: User,
        var store: Store?
)