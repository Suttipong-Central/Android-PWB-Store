package cenergy.central.com.pwb_store.model.body

import cenergy.central.com.pwb_store.model.AddressInformation
import com.google.gson.annotations.SerializedName

data class DeliveryOptionsBody(@SerializedName("address")
                               var addressBody: AddressInformation)