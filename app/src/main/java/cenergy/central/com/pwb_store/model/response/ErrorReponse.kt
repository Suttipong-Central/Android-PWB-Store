package cenergy.central.com.pwb_store.model.response

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
        val message: String? = "",
        @SerializedName("parameters")
        val errorParameter: ErrorParameter? = null
)

data class ErrorParameter(
        val fieldName: String? = null,
        val fieldValue: String? = null
)