package cenergy.central.com.pwb_store.model.body

import cenergy.central.com.pwb_store.model.CustomerAddress

/**
 * Created by Anuphap Suwannamas on 25/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

data class BillingBody(var address: CustomerAddress? = null, var useForShipping: Boolean = true)
