package cenergy.central.com.pwb_store.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.ProductDetailListener
import cenergy.central.com.pwb_store.helpers.ReadFileHelper
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerHDL
import cenergy.central.com.pwb_store.manager.HttpManagerHDLOld
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.model.ShippingDao
import cenergy.central.com.pwb_store.model.ShippingItem
import cenergy.central.com.pwb_store.model.body.CustomDetail
import cenergy.central.com.pwb_store.model.body.PeriodBody
import cenergy.central.com.pwb_store.model.body.ProductHDLBody
import cenergy.central.com.pwb_store.model.body.ShippingSlotBody
import cenergy.central.com.pwb_store.model.request.CustomDetailRequest
import cenergy.central.com.pwb_store.model.request.PeriodRequest
import cenergy.central.com.pwb_store.model.request.ShippingRequest
import cenergy.central.com.pwb_store.model.request.SkuDataRequest
import cenergy.central.com.pwb_store.model.response.ShippingSlot
import cenergy.central.com.pwb_store.model.response.ShippingSlotResponse
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.APIErrorHDLUtils
import cenergy.central.com.pwb_store.utils.DialogUtils
import cenergy.central.com.pwb_store.view.CalendarViewCustom
import cenergy.central.com.pwb_store.view.PowerBuyTextView
import com.google.common.reflect.TypeToken
import org.joda.time.DateTime
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Anuphap Suwannamas on 24/9/2018 AD.
 * Email: Anupharpae@gmail.com
 */

class ProductShippingOptionFragment : Fragment() {

    private var productDetailListener: ProductDetailListener? = null
    private var product: Product? = null
    private val database by lazy { RealmController.with(context) }

    // older code
    private lateinit var header: PowerBuyTextView
    private lateinit var tvNoHaveHomeDelivery: PowerBuyTextView
    private lateinit var mCalendarView: CalendarViewCustom
    private var mShippingRequest: ShippingRequest? = null
    private var mShippingDao: ShippingDao? = null
    private val mSkuDataRequests = arrayListOf<SkuDataRequest>()
    private var mPeriodRequest: PeriodRequest? = null
    private var mCustomDetailRequest: CustomDetailRequest? = null

    private val productHDLList = arrayListOf<ProductHDLBody>()
    private var customDetail = CustomDetail.createCustomDetail("1", "", "00139")

    private val shippingItems = arrayListOf<ShippingItem>()
    private val shippingItemsA = arrayListOf<ShippingItem>()
    private val shippingItemsB = arrayListOf<ShippingItem>()
    private val shippingItemsC = arrayListOf<ShippingItem>()
    private val shippingItemsD = arrayListOf<ShippingItem>()
    private val shippingItemsE = arrayListOf<ShippingItem>()
    private val shippingItemsF = arrayListOf<ShippingItem>()

    private var nextPreWeekday: Array<String>? = null
    private var monthPreday: Array<String>? = null
    private var sunday: String? = null
    private var monday: String? = null
    private var tuesday: String? = null
    private var wednesday: String? = null
    private var thursday: String? = null
    private var friday: String? = null
    private var saturday: String? = null
    // end
    private var progressDialog: ProgressDialog? = null
    private val enableShippingSlot: ArrayList<ShippingSlot> = arrayListOf()
    private var specialSKUList: List<Long>? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        productDetailListener = context as ProductDetailListener
        product = productDetailListener?.getProduct()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_product_shipping_option, container, false)
        setupView(rootView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isAdded){
            getShippingHomeDelivery()
        }
    }

    private fun setupView(rootView: View) {
        header = rootView.findViewById(R.id.txt_header)
        mCalendarView = rootView.findViewById(R.id.custom_calendar)
        tvNoHaveHomeDelivery = rootView.findViewById(R.id.tvNotHaveHomeDelivery)
        product?.let { product ->
            if (product.deliveryMethod.contains(HOME_DELIVERY)) {
                header.visibility = View.VISIBLE
                mCalendarView.visibility = View.VISIBLE
                tvNoHaveHomeDelivery.visibility = View.GONE
            } else {
                header.visibility = View.GONE
                mCalendarView.visibility = View.GONE
                tvNoHaveHomeDelivery.visibility = View.VISIBLE
            }
        }
    }

    // older code
//    @SuppressLint("SimpleDateFormat")
//    private fun getMonth(): String {
//
//        val now = Calendar.getInstance()
//        val format = SimpleDateFormat("yyyy-MM")
//        val month: String
//        month = format.format(now.time)
//
//        return month
//
//    }

    private fun getShippingHomeDelivery() {
        product?.let { product ->
            showProgressDialog()
            val productHDL = ProductHDLBody.createProductHDL("", 1, product.sku,
                    1, "00139")
            productHDLList.add(productHDL)
            if ((getSpecialSKUList() ?: arrayListOf()).contains(product.sku.toLong())) {
                customDetail = CustomDetail(deliveryType = "2", deliveryByStore = "00139", deliveryToStore = "")
            }
            val userInformation = database.userInformation
            val store = userInformation.store!!
            val dateTime = DateTime.now()
            val period = PeriodBody.createPeriod(dateTime.year, dateTime.monthOfYear)
            val shippingSlotBody = ShippingSlotBody.createShippingSlotBody(productHDLs = productHDLList,
                    district = store.district?:"", subDistrict = store.subDistrict?:"",
                    province = store.province?:"", postalId = store.postalCode?:"",
                    period = period, customDetail = customDetail)
            HttpManagerHDL.getInstance().getShippingSlot(shippingSlotBody, object : ApiResponseCallback<ShippingSlotResponse> {
                override fun success(response: ShippingSlotResponse?) {
                    progressDialog?.dismiss()
                    if (response != null) {
                        if (response.shippingSlot.isNotEmpty()) {
                            if (response.shippingSlot.size > 14) {
                                for (i in response.shippingSlot.indices) {
                                    if (enableShippingSlot.size == 14) {
                                        break
                                    }
                                    enableShippingSlot.add(response.shippingSlot[i])
                                }
                                createShippingData()
                            } else {
                                response.shippingSlot.forEach {
                                    enableShippingSlot.add(it)
                                }
                                if (enableShippingSlot.size == 14) {
                                    createShippingData()
                                } else {
                                    getNextMonthShippingSlot()
                                }
                            }
                        } else {
                            showAlertDialog("", getString(R.string.not_have_day_to_delivery))
                        }
                    } else {
                        showAlertDialog("", resources.getString(R.string.some_thing_wrong))
                    }
                }

                override fun failure(error: APIError) {
                    progressDialog?.dismiss()
                    showAlertDialog("", error.errorMessage)
                }
            })
        }
    }

    fun getNextMonthShippingSlot() {
        val userInformation = database.userInformation
        val store = userInformation.store!!
        val dateTime = DateTime.now()
        val period = PeriodBody.createPeriod(dateTime.year, dateTime.monthOfYear + 1)
        val shippingSlotBody = ShippingSlotBody.createShippingSlotBody(productHDLs = productHDLList,
                district = store.district?:"", subDistrict = store.subDistrict?:"",
                province = store.province?:"", postalId = store.postalCode?:"",
                period = period, customDetail = customDetail)
        HttpManagerHDL.getInstance().getShippingSlot(shippingSlotBody, object : ApiResponseCallback<ShippingSlotResponse> {
            override fun success(response: ShippingSlotResponse?) {
                if (response != null && response.shippingSlot.isNotEmpty()) {
                    for (i in response.shippingSlot.indices) {
                        if (enableShippingSlot.size == 14) {
                            break
                        }
                        enableShippingSlot.add(response.shippingSlot[i])
                    }
                    createShippingData()
                }
            }

            override fun failure(error: APIError) {
            }
        })
    }

//    private fun getDataSlot(mount: String) {
//        showProgressDialog()
//        product?.let { product ->
//            val userInformation = database.userInformation
//            val store = userInformation.store
//            mSkuDataRequests.add(SkuDataRequest(product.extension?.barcode, 1, product.sku, 1, store!!.storeId.toString(), ""))
//            val separated = mount.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
//            val year = separated[0]
//            val month = separated[1]
//            mPeriodRequest = PeriodRequest(Integer.parseInt(year), Integer.parseInt(month))
//            mCustomDetailRequest = if ((specialSKUList ?: arrayListOf()).contains(product.sku.toLong())) {
//                CustomDetailRequest("2", "", "00139")
//            } else {
//                CustomDetailRequest("1", "", "00139")
//            }
//            mShippingRequest = ShippingRequest(store.postalCode, mSkuDataRequests, mPeriodRequest, mCustomDetailRequest)
//
//            HttpManagerHDLOld.getInstance().hdlService.checkShippingTime(mShippingRequest).enqueue(object : Callback<ShippingDao> {
//                override fun onResponse(call: Call<ShippingDao>, response: Response<ShippingDao>) {
//                    if (response.isSuccessful) {
//                        mShippingDao = response.body()
//                        createShippingData(mShippingDao)
//                        progressDialog?.dismiss()
//                    } else {
//                        val error = APIErrorHDLUtils.parseError(response)
//                        Log.e("ShippingOption", "onResponse: " + error.errorUserMessage)
//                        progressDialog?.dismiss()
//
//                    }
//                }
//
//                override fun onFailure(call: Call<ShippingDao>, t: Throwable) {
//                    progressDialog?.dismiss()
//                }
//            })
//        }
//    }

    private fun createShippingData() {
        if (nextPreWeekday == null) {
            nextPreWeekday = mCalendarView.weekDay
            monthPreday = mCalendarView.monthDay
        }
        sunday = nextPreWeekday?.get(0)
        monday = nextPreWeekday?.get(1)
        tuesday = nextPreWeekday?.get(2)
        wednesday = nextPreWeekday?.get(3)
        thursday = nextPreWeekday?.get(4)
        friday = nextPreWeekday?.get(5)
        saturday = nextPreWeekday?.get(6)

        mCalendarView.setFirstDayAndLastDay(enableShippingSlot[0], enableShippingSlot[enableShippingSlot.size - 1])

        for (shipping in enableShippingSlot) {
            val shippingDate = shipping.shippingDate
            if (sunday.equals(shippingDate, ignoreCase = true)) {
                for (shippingItem in shipping.slot) {
                    val slotTime = shippingItem.description
                    when (slotTime) {
                        "09:00-09:30" -> shippingItemsA.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "11:00-11:30" -> shippingItemsB.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "13:00-13:30" -> shippingItemsC.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "15:00-15:30" -> shippingItemsD.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "17:00-17:30" -> shippingItemsE.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "19:00-19:30" -> shippingItemsF.add(ShippingItem(shippingItem.id, shippingItem.description))
                        else -> {
                            shippingItemsA.add(ShippingItem(1, "FULL"))
                            shippingItemsB.add(ShippingItem(2, "FULL"))
                            shippingItemsC.add(ShippingItem(3, "FULL"))
                            shippingItemsD.add(ShippingItem(6, "FULL"))
                            shippingItemsE.add(ShippingItem(7, "FULL"))
                            shippingItemsF.add(ShippingItem(8, "FULL"))
                        }
                    }
                }

            } else if (monday.equals(shippingDate, ignoreCase = true)) {
                for (shippingItem in shipping.slot) {
                    val slotTime = shippingItem.description
                    when (slotTime) {
                        "09:00-09:30" -> shippingItemsA.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "11:00-11:30" -> shippingItemsB.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "13:00-13:30" -> shippingItemsC.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "15:00-15:30" -> shippingItemsD.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "17:00-17:30" -> shippingItemsE.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "19:00-19:30" -> shippingItemsF.add(ShippingItem(shippingItem.id, shippingItem.description))
                        else -> {
                            shippingItemsA.add(ShippingItem(1, "FULL"))
                            shippingItemsB.add(ShippingItem(2, "FULL"))
                            shippingItemsC.add(ShippingItem(3, "FULL"))
                            shippingItemsD.add(ShippingItem(6, "FULL"))
                            shippingItemsE.add(ShippingItem(7, "FULL"))
                            shippingItemsF.add(ShippingItem(8, "FULL"))
                        }
                    }
                }

            }
            when {
                tuesday.equals(shippingDate, ignoreCase = true) -> for (shippingItem in shipping.slot) {
                    val slotTime = shippingItem.description
                    when (slotTime) {
                        "09:00-09:30" -> shippingItemsA.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "11:00-11:30" -> shippingItemsB.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "13:00-13:30" -> shippingItemsC.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "15:00-15:30" -> shippingItemsD.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "17:00-17:30" -> shippingItemsE.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "19:00-19:30" -> shippingItemsF.add(ShippingItem(shippingItem.id, shippingItem.description))
                        else -> {
                            shippingItemsA.add(ShippingItem(1, "FULL"))
                            shippingItemsB.add(ShippingItem(2, "FULL"))
                            shippingItemsC.add(ShippingItem(3, "FULL"))
                            shippingItemsD.add(ShippingItem(6, "FULL"))
                            shippingItemsE.add(ShippingItem(7, "FULL"))
                            shippingItemsF.add(ShippingItem(8, "FULL"))
                        }
                    }
                }
                wednesday.equals(shippingDate, ignoreCase = true) -> for (shippingItem in shipping.slot) {
                    val slotTime = shippingItem.description
                    when (slotTime) {
                        "09:00-09:30" -> shippingItemsA.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "11:00-11:30" -> shippingItemsB.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "13:00-13:30" -> shippingItemsC.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "15:00-15:30" -> shippingItemsD.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "17:00-17:30" -> shippingItemsE.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "19:00-19:30" -> shippingItemsF.add(ShippingItem(shippingItem.id, shippingItem.description))
                        else -> {
                            shippingItemsA.add(ShippingItem(1, "FULL"))
                            shippingItemsB.add(ShippingItem(2, "FULL"))
                            shippingItemsC.add(ShippingItem(3, "FULL"))
                            shippingItemsD.add(ShippingItem(6, "FULL"))
                            shippingItemsE.add(ShippingItem(7, "FULL"))
                            shippingItemsF.add(ShippingItem(8, "FULL"))
                        }
                    }
                }
                thursday.equals(shippingDate, ignoreCase = true) -> for (shippingItem in shipping.slot) {
                    val slotTime = shippingItem.description
                    when (slotTime) {
                        "09:00-09:30" -> shippingItemsA.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "11:00-11:30" -> shippingItemsB.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "13:00-13:30" -> shippingItemsC.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "15:00-15:30" -> shippingItemsD.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "17:00-17:30" -> shippingItemsE.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "19:00-19:30" -> shippingItemsF.add(ShippingItem(shippingItem.id, shippingItem.description))
                        else -> {
                            shippingItemsA.add(ShippingItem(1, "FULL"))
                            shippingItemsB.add(ShippingItem(2, "FULL"))
                            shippingItemsC.add(ShippingItem(3, "FULL"))
                            shippingItemsD.add(ShippingItem(6, "FULL"))
                            shippingItemsE.add(ShippingItem(7, "FULL"))
                            shippingItemsF.add(ShippingItem(8, "FULL"))
                        }
                    }
                }
                friday.equals(shippingDate, ignoreCase = true) -> for (shippingItem in shipping.slot) {
                    val slotTime = shippingItem.description
                    when (slotTime) {
                        "09:00-09:30" -> shippingItemsA.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "11:00-11:30" -> shippingItemsB.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "13:00-13:30" -> shippingItemsC.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "15:00-15:30" -> shippingItemsD.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "17:00-17:30" -> shippingItemsE.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "19:00-19:30" -> shippingItemsF.add(ShippingItem(shippingItem.id, shippingItem.description))
                        else -> {
                            shippingItemsA.add(ShippingItem(1, "FULL"))
                            shippingItemsB.add(ShippingItem(2, "FULL"))
                            shippingItemsC.add(ShippingItem(3, "FULL"))
                            shippingItemsD.add(ShippingItem(6, "FULL"))
                            shippingItemsE.add(ShippingItem(7, "FULL"))
                            shippingItemsF.add(ShippingItem(8, "FULL"))
                        }
                    }
                }
                saturday.equals(shippingDate, ignoreCase = true) -> for (shippingItem in shipping.slot) {
                    val slotTime = shippingItem.description
                    when (slotTime) {
                        "09:00-09:30" -> shippingItemsA.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "11:00-11:30" -> shippingItemsB.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "13:00-13:30" -> shippingItemsC.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "15:00-15:30" -> shippingItemsD.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "17:00-17:30" -> shippingItemsE.add(ShippingItem(shippingItem.id, shippingItem.description))
                        "19:00-19:30" -> shippingItemsF.add(ShippingItem(shippingItem.id, shippingItem.description))
                        else -> {
                            shippingItemsA.add(ShippingItem(1, "FULL"))
                            shippingItemsB.add(ShippingItem(2, "FULL"))
                            shippingItemsC.add(ShippingItem(3, "FULL"))
                            shippingItemsD.add(ShippingItem(6, "FULL"))
                            shippingItemsE.add(ShippingItem(7, "FULL"))
                            shippingItemsF.add(ShippingItem(8, "FULL"))
                        }
                    }
                }
            }
        }

        for (shippingItemA in shippingItemsA) {
            shippingItems.add(ShippingItem(shippingItemA.id, shippingItemA.description))
        }

        for (shippingItemB in shippingItemsB) {
            shippingItems.add(ShippingItem(shippingItemB.id, shippingItemB.description))
        }

        for (shippingItemC in shippingItemsC) {
            shippingItems.add(ShippingItem(shippingItemC.id, shippingItemC.description))
        }

        for (shippingItemD in shippingItemsD) {
            shippingItems.add(ShippingItem(shippingItemD.id, shippingItemD.description))
        }

        for (shippingItemE in shippingItemsE) {
            shippingItems.add(ShippingItem(shippingItemE.id, shippingItemE.description))
        }

        for (shippingItemF in shippingItemsF) {
            shippingItems.add(ShippingItem(shippingItemF.id, shippingItemF.description))
        }

        mCalendarView.setTimeSlotItem(shippingItems)
    }
    // endregion

    private fun getSpecialSKUList(): List<Long>? {
        return this.specialSKUList ?: context?.let { ReadFileHelper<List<Long>>().parseRawJson(it, R.raw.special_sku,
                object : TypeToken<List<Long>>() {}.type, null) }
    }


    private fun showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = DialogUtils.createProgressDialog(context)
            progressDialog?.show()
        } else {
            progressDialog?.show()
        }
    }

    private fun showAlertDialog(title: String, message: String) {
        val builder = context?.let { AlertDialog.Builder(it, R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok_alert)) { dialog, which -> dialog.dismiss() }
        }
        if (!TextUtils.isEmpty(title)) {
            builder?.setTitle(title)
        }
        builder?.show()
    }

    companion object {
        private const val HOME_DELIVERY = "pwb_homedelivery"
    }
}