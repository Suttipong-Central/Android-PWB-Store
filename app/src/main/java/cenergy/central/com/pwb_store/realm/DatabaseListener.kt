package cenergy.central.com.pwb_store.realm

/**
 * Created by Anuphap Suwannamas on 10/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

interface DatabaseListener{
    fun onSuccessfully()
    fun onFailure(error: Throwable)
}