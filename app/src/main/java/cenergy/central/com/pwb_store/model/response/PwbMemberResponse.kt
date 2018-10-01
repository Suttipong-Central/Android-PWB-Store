package cenergy.central.com.pwb_store.model.response

import cenergy.central.com.pwb_store.model.PwbMember
import com.google.gson.annotations.SerializedName

/**
 * Created by Anuphap Suwannamas on 1/10/2018 AD.
 * Email: Anupharpae@gmail.com
 */

class PwbMemberResponse(
        @SerializedName("items")
        var pwbMembers: List<PwbMember>? = null,
        @SerializedName("total_count")
        var totalCount: Int? = 0
)