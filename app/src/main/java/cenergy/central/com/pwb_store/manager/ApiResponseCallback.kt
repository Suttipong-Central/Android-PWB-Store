package cenergy.central.com.pwb_store.manager

import cenergy.central.com.pwb_store.model.APIError

/**
 * Created by Anuphap Suwannamas on 6/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

interface ApiResponseCallback<T> {
    fun success(response: T?)
    fun failure(error: APIError)
}