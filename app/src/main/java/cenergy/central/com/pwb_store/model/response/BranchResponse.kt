package cenergy.central.com.pwb_store.model.response

import cenergy.central.com.pwb_store.model.Branch
import com.google.gson.annotations.SerializedName

class BranchResponse(var items: List<Branch>, @SerializedName("total_count")var totalBranch: Int = 0)