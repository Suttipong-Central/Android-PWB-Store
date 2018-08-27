package cenergy.central.com.pwb_store.model.response

import cenergy.central.com.pwb_store.model.MemberCard
import cenergy.central.com.pwb_store.model.NamePattern
import com.google.gson.annotations.SerializedName

/**
 * Created by Anuphap Suwannamas on 27/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

data class MemberResponse(
        var customerId: String = "",
        var memberTier: String = "",
        @SerializedName("firstName")
        var firstname: NamePattern? = null,
        @SerializedName("lastName")
        var lastname: NamePattern? = null,
        var cards: ArrayList<MemberCard> = arrayListOf()
) {
    fun getDisplayName(): String {
        return if (firstname != null && lastname != null) {
            if (firstname!!.thai.isNotEmpty()) {
                "${firstname!!.thai} ${lastname!!.thai}"
            } else {
                "${firstname!!.eng} ${lastname!!.eng}"
            }
        } else {
            ""
        }
    }
}