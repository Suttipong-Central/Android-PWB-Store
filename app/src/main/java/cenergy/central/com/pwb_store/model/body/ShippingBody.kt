package cenergy.central.com.pwb_store.model.body

import cenergy.central.com.pwb_store.model.AddressInformation
import com.google.gson.annotations.SerializedName

/**
 * Created by Anuphap Suwannamas on 25/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

data class ShippingBody(@SerializedName("addressInformation")
                        var addressBody: AddressInformationBody)

data class AddressInformationBody(@SerializedName("shipping_address")
                                  var shippingAddress: AddressInformation,
                                  @SerializedName("billing_address")
                                  var billingAddress: AddressInformation,
                                  @SerializedName("shipping_method_code")
                                  var methodCode: String,
                                  @SerializedName("shipping_carrier_code")
                                  var carrierCode: String)
