package cenergy.central.com.pwb_store.model.response

import cenergy.central.com.pwb_store.model.EOrderingMember
import com.google.gson.annotations.SerializedName

/**
 * Created by Anuphap Suwannamas on 1/10/2018 AD.
 * Email: Anupharpae@gmail.com
 */

class PwbMemberResponse(
        @SerializedName("items")
        var EOrderingMembers: List<EOrderingMember>? = null,
        @SerializedName("total_count")
        var totalCount: Int? = 0
)