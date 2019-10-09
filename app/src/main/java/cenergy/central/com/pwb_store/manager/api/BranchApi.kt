package cenergy.central.com.pwb_store.manager.api

import android.content.Context
import android.util.Log
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.Branch
import cenergy.central.com.pwb_store.model.SourceItem
import cenergy.central.com.pwb_store.model.response.BranchResponse
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException
import java.util.*

class BranchApi {
    fun getBranchesISPU(context: Context, sku: String, callback: ApiResponseCallback<List<BranchResponse>>) {
        val apiManager = HttpManagerMagento.getInstance(context)
        val client = apiManager.defaultHttpClient
        val path = "rest/${apiManager.getLanguage()}/V1/storepickup/stores/ispu/$sku"
        val request = apiManager.createRequestHttps(path)

        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                try {
                    val items = parseBranchResponse(response)
                    callback.success(items)
                } catch (e: Exception) {
                    callback.failure(APIError(e))
                    Log.e("JSON Parser", "Error parsing data $e")
                }

                response.close()
            }

            override fun onFailure(call: Call, e: IOException) {
                callback.failure(APIError(e))
            }
        })
    }

    fun getBranchesSTS(context: Context, callback: ApiResponseCallback<List<BranchResponse>>) {
        val apiManager = HttpManagerMagento.getInstance(context)
        val client = apiManager.defaultHttpClient
        val path = "rest/${apiManager.getLanguage()}/V1/storepickup/stores/sts"
        val request = apiManager.createRequestHttps(path)

        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                try {
                    val items = parseBranchResponse(response)
                    callback.success(items)
                } catch (e: Exception) {
                    callback.failure(APIError(e))
                    Log.e("JSON Parser", "Error parsing data $e")
                }

                response.close()
            }

            override fun onFailure(call: Call, e: IOException) {
                callback.failure(APIError(e))
            }
        })
    }

    private fun parseBranchResponse(response: Response): List<BranchResponse> {
        val data = response.body()
        val arrayObj = JSONArray(data?.string())
        val branchResponses = arrayListOf<BranchResponse>()

        for (i in 0 until arrayObj.length()) {
            val itemObj = arrayObj.getJSONObject(i)

            // For store ispu
            var sourceItem: SourceItem? = SourceItem() // new source item
            if (itemObj.has("source_item")) {
                val sourceItemObj = itemObj.getJSONObject("source_item")
                sourceItem?.sku = sourceItemObj.getString("sku")
                if (sourceItemObj.has("quantity") && !sourceItemObj.isNull("quantity")) {
                    sourceItem?.quantity = sourceItemObj.getInt("quantity")
                }
                sourceItem?.status = sourceItemObj.getInt("status")
            } else {
                sourceItem = null
            }

            // For store sts&ispu
            val branch = Branch() // new Branch
            if (itemObj.has("store")) {
                val storeObj = itemObj.getJSONObject("store")
                branch.storeId = storeObj.getString("id")
                branch.storeName = storeObj.getString("name")
                branch.isActive = if (storeObj.getBoolean("is_active")) 1 else 0
                branch.sellerCode = storeObj.getString("seller_code")
                branch.createdAt = storeObj.getString("created_at")
                branch.updatedAt = storeObj.getString("updated_at")
                branch.attrSetName = storeObj.getString("attribute_set_name")

                // field extension_attributes
                if (storeObj.has("extension_attributes")) {
                    val extensionObj = storeObj.getJSONObject("extension_attributes")
                    if (extensionObj.has("address")) {
                        val addressObj = extensionObj.getJSONObject("address")
                        branch.city = addressObj.getString("city")
                        branch.postcode = addressObj.getString("postcode")
                        val street = addressObj.getJSONArray("street")
                        var txtStreet = ""
                        for (s in 0 until street.length()) {
                            txtStreet += street.getString(s)
                        }
                        branch.street = txtStreet
                        val coordinatesObj = addressObj.getJSONObject("coordinates")
                        branch.latitude = coordinatesObj.getString("latitude")
                        branch.longitude = coordinatesObj.getString("longitude")
                        if (addressObj.has("region")) {
                            branch.region = addressObj.getString("region")
                        }
                        if (addressObj.has("region_id")) {
                            branch.regionId = addressObj.getInt("region_id")
                        }
                        if (addressObj.has("region_code")) {
                            branch.regionCode = addressObj.getString("region_code")
                        }
                    }

                    if (extensionObj.has("opening_hours")) {
                        val openingArray = extensionObj.getJSONArray("opening_hours")
                        val calendar = Calendar.getInstance()
                        val day = calendar.get(Calendar.DAY_OF_WEEK) - 1 // index of opening_hours
                        if (openingArray.length() > 0 && day < openingArray.length()) {
                            val openItemArray = openingArray.getJSONArray(day)
                            val startTime = openItemArray.getJSONObject(0).getString("start_time")
                            val endTime = openItemArray.getJSONObject(0).getString("end_time")
                            branch.description = "$startTime - $endTime"
                        }
                    }

                    // ispu delivery description
                    if (extensionObj.has("ispu_promise_delivery")) {
                        branch.ispuDelivery = extensionObj.getString("ispu_promise_delivery") ?: ""
                    }
                }

                // field custom_attributes
                if (storeObj.has("custom_attributes")) {
                    val customAttrArray = storeObj.getJSONArray("custom_attributes")
                    for (m in 0 until customAttrArray.length()) {
                        val ctmAttr = customAttrArray.getJSONObject(m)
                        if (ctmAttr.has("name")) {
                            when (ctmAttr.getString("name")) {
                                "contact_phone" -> {
                                    branch.phone = ctmAttr.getString("value") ?: ""
                                }
                                "contact_fax" -> {
                                    branch.fax = ctmAttr.getString("value") ?: ""
                                }
                                "contact_mail" -> {
                                    branch.email = ctmAttr.getString("value") ?: ""
                                }
                            }
                        }
                    }
                }
            }
            branchResponses.add(BranchResponse(sourceItem, branch)) // add branch response
        }
        return branchResponses
    }
}