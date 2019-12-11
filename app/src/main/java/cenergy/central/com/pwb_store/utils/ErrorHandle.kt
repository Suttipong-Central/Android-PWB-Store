package cenergy.central.com.pwb_store.utils

import android.util.Log
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.response.ErrorParameter
import cenergy.central.com.pwb_store.model.response.ErrorResponse
import retrofit2.HttpException
import java.io.IOException

class ApiException(val code: Int? = -1, message: String,val errorParameter: ErrorParameter? = null) : Exception(message)
class UnauthenticatedException : Exception()
class NetworkNotConnection : Exception()

fun HttpException?.getErrorFormApi(): Exception {

    if (this?.code() == 401) {
        return UnauthenticatedException()
    } else {
        if (this?.response()?.errorBody() == null) {
            return ApiException(this?.code(), "error and missing error body")
        }

        val url = this.response().raw().request().url().toString()
        val errString = this.response().errorBody()?.string()
        Log.e("ApiError", url)

        val error: ErrorResponse = GsonProvider.getInstance().gson.fromJson(
                errString, ErrorResponse::class.java)
        return ApiException(this.code(), error.message ?: "", error.errorParameter)

    }
}

fun Throwable.getResultError(): APIError {
    this.printStackTrace()
    return when (this) {

        is IOException -> {
            APIError(NetworkNotConnection())
        }

        is HttpException -> {
            APIError(this.getErrorFormApi())
        }

        else -> {
            APIError(this)
        }
    }
}