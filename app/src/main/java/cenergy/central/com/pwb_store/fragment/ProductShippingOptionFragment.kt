package cenergy.central.com.pwb_store.fragment

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
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.model.ShippingItem
import cenergy.central.com.pwb_store.model.body.CustomDetail
import cenergy.central.com.pwb_store.model.body.PeriodBody
import cenergy.central.com.pwb_store.model.body.ProductHDLBody
import cenergy.central.com.pwb_store.model.body.ShippingSlotBody
import cenergy.central.com.pwb_store.model.response.ShippingSlot
import cenergy.central.com.pwb_store.model.response.ShippingSlotResponse
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.DialogUtils
import cenergy.central.com.pwb_store.view.CalendarViewCustom
import cenergy.central.com.pwb_store.view.PowerBuyTextView
import com.google.common.reflect.TypeToken
import org.joda.time.DateTime

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

    private val productHDLList = arrayListOf<ProductHDLBody>()
    private var customDetail = CustomDetail.createCustomDetail("1", "", "00139")

    private val shippingItems = arrayListOf<ShippingItem>()
    private val shippingItemsA = arrayListOf<ShippingItem>()
    private val shippingItemsB = arrayListOf<ShippingItem>()
    private val shippingItemsC = arrayListOf<ShippingItem>()
    private val shippingItemsD = arrayListOf<ShippingItem>()
    private val shippingItemsE = arrayListOf<ShippingItem>()
    private val shippingItemsF = arrayListOf<ShippingItem>()

    private var nextPreWeekday: Array<String> = arrayOf()
    private var monthPreday: Array<String>? = null
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
        if (isAdded) {
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
                    district = store.district ?: "", subDistrict = store.subDistrict ?: "",
                    province = store.province ?: "", postalId = store.postalCode ?: "",
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
                district = store.district ?: "", subDistrict = store.subDistrict ?: "",
                province = store.province ?: "", postalId = store.postalCode ?: "",
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

    private fun createShippingData() {
        if (nextPreWeekday.isEmpty()) {
            nextPreWeekday = mCalendarView.weekDay
            monthPreday = mCalendarView.monthDay
        }

        mCalendarView.setFirstDayAndLastDay(enableShippingSlot[0], enableShippingSlot[enableShippingSlot.size - 1])
        Log.d("WeekD", " WeekD ${nextPreWeekday.size} - ")
        Log.d("WeekD", " enableShippingSlot ${enableShippingSlot.size} - ")

        val currentWeekday: ArrayList<ShippingSlot> = arrayListOf()

        for (item in enableShippingSlot) {
            if (nextPreWeekday.contains(item.shippingDate)) {
                currentWeekday.add(item)
            }
        }

        for (day in nextPreWeekday) {

            val currentDay = currentWeekday.firstOrNull { it.shippingDate == day }

            if (currentDay != null) {
                Log.d("WeekD", "${currentDay.shippingDate} ")
                var itemA = ShippingItem(1, "FULL")
                var itemB = ShippingItem(2, "FULL")
                var itemC = ShippingItem(3, "FULL")
                var itemD = ShippingItem(6, "FULL")
                var itemE = ShippingItem(7, "FULL")
                var itemF = ShippingItem(8, "FULL")

                for (shippingItem in currentDay.slot) {
                    val slotTime = shippingItem.description

                    if (slotTime == "09:00-09:30") {
                        itemA = ShippingItem(shippingItem.id, shippingItem.description)
                    }

                    if (slotTime == "11:00-11:30") {
                        itemB = ShippingItem(shippingItem.id, shippingItem.description)
                    }


                    if (slotTime == "13:00-13:30") {
                        itemC = ShippingItem(shippingItem.id, shippingItem.description)
                    }

                    if (slotTime == "15:00-15:30") {
                        itemD = ShippingItem(shippingItem.id, shippingItem.description)
                    }

                    if (slotTime == "17:00-17:30") {
                        itemE = ShippingItem(shippingItem.id, shippingItem.description)
                    }

                    if (slotTime == "19:00-19:30") {
                        itemF = ShippingItem(shippingItem.id, shippingItem.description)
                    }

                }

                itemA?.let { shippingItemsA.add(it) }
                itemB?.let { shippingItemsB.add(it) }
                itemC?.let { shippingItemsC.add(it) }
                itemD?.let { shippingItemsD.add(it) }
                itemE?.let { shippingItemsE.add(it) }
                itemF?.let { shippingItemsF.add(it) }

            } else {
                shippingItemsA.add(ShippingItem(1, "-"))
                shippingItemsB.add(ShippingItem(2, "-"))
                shippingItemsC.add(ShippingItem(3, "-"))
                shippingItemsD.add(ShippingItem(6, "-"))
                shippingItemsE.add(ShippingItem(7, "-"))
                shippingItemsF.add(ShippingItem(8, "-"))
            }
        }

        shippingItems.addAll(shippingItemsA)
        shippingItems.addAll(shippingItemsB)
        shippingItems.addAll(shippingItemsC)
        shippingItems.addAll(shippingItemsD)
        shippingItems.addAll(shippingItemsE)
        shippingItems.addAll(shippingItemsF)
        Log.d("WeekD", "shippingItems: ${shippingItems.size}")

        mCalendarView.setTimeSlotItem(shippingItems)
    }
    // endregion

    private fun getSpecialSKUList(): List<Long>? {
        return this.specialSKUList ?: context?.let {
            ReadFileHelper<List<Long>>().parseRawJson(it, R.raw.special_sku,
                    object : TypeToken<List<Long>>() {}.type, null)
        }
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
        val builder = context?.let {
            AlertDialog.Builder(it, R.style.AlertDialogTheme)
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