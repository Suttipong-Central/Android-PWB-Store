package cenergy.central.com.pwb_store.fragment.interfaces

import cenergy.central.com.pwb_store.model.Branch

/**
 * Created by Anuphap Suwannamas on 13/9/2018 AD.
 * Email: Anupharpae@gmail.com
 */

interface StorePickUpListener {
    fun onUpdateStoreDetail(branch: Branch)
    fun onSelectedStore(branch: Branch)
}