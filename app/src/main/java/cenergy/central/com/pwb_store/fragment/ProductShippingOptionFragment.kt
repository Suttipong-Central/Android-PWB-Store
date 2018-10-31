package cenergy.central.com.pwb_store.fragment

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.text.TextUtils
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

class ProductShippingOptionFragment : Fragment(), CalendarViewCustom.OnItemClickListener {

    private var productDetailListener: ProductDetailListener? = null
    private var product: Product? = null
    private val database by lazy { RealmController.with(context) }

    // older code
    private lateinit var header: PowerBuyTextView
    private lateinit var tvNoHaveHomeDelivery: PowerBuyTextView
    private lateinit var mCalendarView: CalendarViewCustom
    private val productHDLList = arrayListOf<ProductHDLBody>()
    private var customDetail = CustomDetail.createCustomDetail("1", "", "00139")
    private var nextPreWeekday: Array<String> = arrayOf()
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

    //region calendar click
    override fun onPreviousClick(weekDays: Array<String>) {
        this.nextPreWeekday = weekDays
        createShippingData(false)
    }

    override fun onNextClick(weekDays: Array<String>) {
        this.nextPreWeekday = weekDays
        createShippingData(false)
    }
    // end region calendar

    private fun setupView(rootView: View) {
        header = rootView.findViewById(R.id.txt_header)
        mCalendarView = rootView.findViewById(R.id.custom_calendar)
        mCalendarView.setListener(this)
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
            context?.let {
                HttpManagerHDL.getInstance(it).getShippingSlot(shippingSlotBody, object : ApiResponseCallback<ShippingSlotResponse> {
                    override fun success(response: ShippingSlotResponse?) {
                        if (response != null) {
                            if (response.shippingSlot.isNotEmpty()) {
                                if (response.shippingSlot.size > 14) {
                                    for (i in response.shippingSlot.indices) {
                                        if (enableShippingSlot.size == 14) {
                                            break
                                        }
                                        enableShippingSlot.add(response.shippingSlot[i])
                                    }
                                    progressDialog?.dismiss()
                                    createShippingData(true)
                                } else {
                                    response.shippingSlot.forEach {shippingSlot ->
                                        enableShippingSlot.add(shippingSlot)
                                    }
                                    if (enableShippingSlot.size == 14) {
                                        progressDialog?.dismiss()
                                        createShippingData(true)
                                    } else {
                                        getNextMonthShippingSlot()
                                    }
                                }
                            } else {
                                getNextMonthShippingSlot()
                            }
                        } else {
                            progressDialog?.dismiss()
                            showAlertDialog("", getString(R.string.not_have_day_to_delivery))
                        }
                    }

                    override fun failure(error: APIError) {
                        progressDialog?.dismiss()
                        showAlertDialog("", error.errorMessage)
                    }
                })
            }
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
        context?.let {
            HttpManagerHDL.getInstance(it).getShippingSlot(shippingSlotBody, object : ApiResponseCallback<ShippingSlotResponse> {
                override fun success(response: ShippingSlotResponse?) {
                    if (response != null && response.shippingSlot.isNotEmpty()) {
                        for (i in response.shippingSlot.indices) {
                            if (enableShippingSlot.size == 14) {
                                break
                            }
                            enableShippingSlot.add(response.shippingSlot[i])
                        }
                        progressDialog?.dismiss()
                        createShippingData(true)
                    } else {
                        getNextMonthShippingSlot()
                    }
                }

                override fun failure(error: APIError) {
                    progressDialog?.dismiss()
                    showAlertDialog("", error.errorMessage)
                }
            })
        }
    }

    private fun createShippingData(setFirstDayAndLastDay: Boolean) {
        if (setFirstDayAndLastDay) {
            mCalendarView.setFirstDayAndLastDay(enableShippingSlot[0], enableShippingSlot[enableShippingSlot.size - 1])
        }
        val currentWeekday: ArrayList<ShippingSlot> = arrayListOf()
        val shippingItems = arrayListOf<ShippingItem>()
        val shippingItemsA = arrayListOf<ShippingItem>()
        val shippingItemsB = arrayListOf<ShippingItem>()
        val shippingItemsC = arrayListOf<ShippingItem>()
        val shippingItemsD = arrayListOf<ShippingItem>()
        val shippingItemsE = arrayListOf<ShippingItem>()
        val shippingItemsF = arrayListOf<ShippingItem>()
        if (nextPreWeekday.isEmpty()) {
            nextPreWeekday = mCalendarView.weekDay
        }
        for (item in enableShippingSlot) {
            if (nextPreWeekday.contains(item.shippingDate)) {
                currentWeekday.add(item)
            }
        }
        for (day in nextPreWeekday) {
            val currentDay = currentWeekday.firstOrNull { it.shippingDate == day }
            if (currentDay != null) {
                var itemA = ShippingItem(1, "FULL")
                var itemB = ShippingItem(2, "FULL")
                var itemC = ShippingItem(3, "FULL")
                var itemD = ShippingItem(6, "FULL")
                var itemE = ShippingItem(7, "FULL")
                var itemF = ShippingItem(8, "FULL")

                for (shippingItem in currentDay.slot) {
                    val slotTime = shippingItem.description
                    when (slotTime) {
                        "09:00-09:30" -> {
                            itemA = ShippingItem(shippingItem.id, shippingItem.description)
                        }
                        "11:00-11:30" -> {
                            itemB = ShippingItem(shippingItem.id, shippingItem.description)
                        }
                        "13:00-13:30" -> {
                            itemC = ShippingItem(shippingItem.id, shippingItem.description)
                        }
                        "15:00-15:30" -> {
                            itemD = ShippingItem(shippingItem.id, shippingItem.description)
                        }
                        "17:00-17:30" -> {
                            itemE = ShippingItem(shippingItem.id, shippingItem.description)
                        }
                        "19:00-19:30" -> {
                            itemF = ShippingItem(shippingItem.id, shippingItem.description)
                        }
                    }
                }
                shippingItemsA.add(itemA)
                shippingItemsB.add(itemB)
                shippingItemsC.add(itemC)
                shippingItemsD.add(itemD)
                shippingItemsE.add(itemE)
                shippingItemsF.add(itemF)
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