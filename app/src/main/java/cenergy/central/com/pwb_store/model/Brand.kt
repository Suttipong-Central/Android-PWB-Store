package cenergy.central.com.pwb_store.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Anuphap Suwannamas on 27/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

data class Brand(@SerializedName("id") var brandId: Long? = 0, var name: String = "")