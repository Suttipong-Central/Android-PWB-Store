package cenergy.central.com.pwb_store.fragment.interfaces

import cenergy.central.com.pwb_store.model.Branch
import cenergy.central.com.pwb_store.model.response.BranchResponse

/**
 * Created by Anuphap Suwannamas on 13/9/2018 AD.
 * Email: Anupharpae@gmail.com
 */

interface StorePickUpListener {
    fun onUpdateStoreDetail(branch: BranchResponse)
    fun onSelectedStore(branch: Branch)
    fun addProduct2hToCart(branchResponse: BranchResponse)
}