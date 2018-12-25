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
import cenergy.central.com.pwb_store.model.UserInformation
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
    private val database = RealmController.getInstance()

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
    private val userInformation: UserInformation = database.userInformation
    private var store = userInformation.store

    // date
    private val dateTime = DateTime.now()
    private var month = dateTime.monthOfYear
    private var year = dateTime.year

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
            product?.let { product ->
                if (product.deliveryMethod.contains(HOME_DELIVERY) && store != null) {
                    header.visibility = View.VISIBLE
                    mCalendarView.visibility = View.VISIBLE
                    tvNoHaveHomeDelivery.visibility = View.GONE
                    getShippingHomeDelivery()
                } else if (product.deliveryMethod.contains(HOME_DELIVERY) && store == null) {
                    hideCalendarView()
                    showAlertDialog("", resources.getString(R.string.cannot_get_shipping_slot))
                } else {
                    hideCalendarView()
                }
            }
        }
    }

    private fun hideCalendarView() {
        header.visibility = View.GONE
        mCalendarView.visibility = View.GONE
        tvNoHaveHomeDelivery.visibility = View.VISIBLE
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
            val period = PeriodBody.createPeriod(year, month)
            val shippingSlotBody = ShippingSlotBody.createShippingSlotBody(productHDLs = productHDLList,
                    district = store?.district ?: "", subDistrict = store?.subDistrict ?: "",
                    province = store?.province ?: "", postalId = store?.postalCode ?: "",
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
                                    response.shippingSlot.forEach { shippingSlot ->
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
        val period: PeriodBody
        if(month == 12){
            month = 1
            period = PeriodBody.createPeriod(year + 1, month)
        } else {
            period = PeriodBody.createPeriod(year, month + 1)
        }
        val shippingSlotBody = ShippingSlotBody.createShippingSlotBody(productHDLs = productHDLList,
                district = store?.district ?: "", subDistrict = store?.subDistrict ?: "",
                province = store?.province ?: "", postalId = store?.postalCode ?: "",
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
        val shippingItemsG = arrayListOf<ShippingItem>()
        val shippingItemsH = arrayListOf<ShippingItem>()
        val shippingItemsI = arrayListOf<ShippingItem>()
        val shippingItemsJ = arrayListOf<ShippingItem>()
        val shippingItemsK = arrayListOf<ShippingItem>()
        val shippingItemsL = arrayListOf<ShippingItem>()
        val shippingItemsM = arrayListOf<ShippingItem>()
        val shippingItemsN = arrayListOf<ShippingItem>()
        val shippingItemsO = arrayListOf<ShippingItem>()
        val shippingItemsP = arrayListOf<ShippingItem>()
        val shippingItemsQ = arrayListOf<ShippingItem>()
        val shippingItemsR = arrayListOf<ShippingItem>()
        val shippingItemsS = arrayListOf<ShippingItem>()
        val availableTime = arrayListOf<String>()
        if (nextPreWeekday.isEmpty()) {
            nextPreWeekday = mCalendarView.weekDay
        }
        for (item in enableShippingSlot) {
            if (nextPreWeekday.contains(item.shippingDate)) {
                currentWeekday.add(item)
            }
        }
        currentWeekday.forEach { shippingSlot ->
            shippingSlot.slot.forEach { slot ->
                if (availableTime.none { it == slot.description }) {
                    availableTime.add(slot.description)
                }
            }
        }
        for (day in nextPreWeekday) {
            val currentDay = currentWeekday.firstOrNull { it.shippingDate == day }
            if (currentDay != null) {
                var itemA = ShippingItem(0, "FULL")
                var itemB = ShippingItem(0, "FULL")
                var itemC = ShippingItem(0, "FULL")
                var itemD = ShippingItem(0, "FULL")
                var itemE = ShippingItem(0, "FULL")
                var itemF = ShippingItem(0, "FULL")
                var itemG = ShippingItem(0, "FULL")
                var itemH = ShippingItem(0, "FULL")
                var itemI = ShippingItem(0, "FULL")
                var itemJ = ShippingItem(0, "FULL")
                var itemK = ShippingItem(0, "FULL")
                var itemL = ShippingItem(0, "FULL")
                var itemM = ShippingItem(0, "FULL")
                var itemN = ShippingItem(0, "FULL")
                var itemO = ShippingItem(0, "FULL")
                var itemP = ShippingItem(0, "FULL")
                var itemQ = ShippingItem(0, "FULL")
                var itemR = ShippingItem(0, "FULL")
                var itemS = ShippingItem(0, "FULL")

                for (shippingItem in currentDay.slot) {
                    val slotTime = shippingItem.description
                    when (slotTime) {
                        "09:00-09:30" -> {
                            itemA = ShippingItem(shippingItem.id, shippingItem.description)
                        }
                        "09:30-10:00" -> {
                            itemB = ShippingItem(shippingItem.id, shippingItem.description)
                        }
                        "10:00-10:30" -> {
                            itemC = ShippingItem(shippingItem.id, shippingItem.description)
                        }
                        "10:30-11:00" -> {
                            itemD = ShippingItem(shippingItem.id, shippingItem.description)
                        }
                        "11:00-11:30" -> {
                            itemE = ShippingItem(shippingItem.id, shippingItem.description)
                        }
                        "11:30-12:00" -> {
                            itemF = ShippingItem(shippingItem.id, shippingItem.description)
                        }
                        "13:00-13:30" -> {
                            itemG = ShippingItem(shippingItem.id, shippingItem.description)
                        }
                        "13:30-14:00" -> {
                            itemH = ShippingItem(shippingItem.id, shippingItem.description)
                        }
                        "14:00-14:30" -> {
                            itemI = ShippingItem(shippingItem.id, shippingItem.description)
                        }
                        "14:30-15:00" -> {
                            itemJ = ShippingItem(shippingItem.id, shippingItem.description)
                        }
                        "15:00-15:30" -> {
                            itemK = ShippingItem(shippingItem.id, shippingItem.description)
                        }
                        "15:30-16:00" -> {
                            itemL = ShippingItem(shippingItem.id, shippingItem.description)
                        }
                        "16:00-16:30" -> {
                            itemM = ShippingItem(shippingItem.id, shippingItem.description)
                        }
                        "16:30-17:00" -> {
                            itemN = ShippingItem(shippingItem.id, shippingItem.description)
                        }
                        "17:00-17:30" -> {
                            itemO = ShippingItem(shippingItem.id, shippingItem.description)
                        }
                        "17:30-18:00" -> {
                            itemP = ShippingItem(shippingItem.id, shippingItem.description)
                        }
                        "18:00-18:30" -> {
                            itemQ = ShippingItem(shippingItem.id, shippingItem.description)
                        }
                        "18:30-19:00" -> {
                            itemR = ShippingItem(shippingItem.id, shippingItem.description)
                        }
                        "19:00-19:30" -> {
                            itemS = ShippingItem(shippingItem.id, shippingItem.description)
                        }
                    }
                }
                shippingItemsA.add(itemA)
                shippingItemsB.add(itemB)
                shippingItemsC.add(itemC)
                shippingItemsD.add(itemD)
                shippingItemsE.add(itemE)
                shippingItemsF.add(itemF)
                shippingItemsG.add(itemG)
                shippingItemsH.add(itemH)
                shippingItemsI.add(itemI)
                shippingItemsJ.add(itemJ)
                shippingItemsK.add(itemK)
                shippingItemsL.add(itemL)
                shippingItemsM.add(itemM)
                shippingItemsN.add(itemN)
                shippingItemsO.add(itemO)
                shippingItemsP.add(itemP)
                shippingItemsQ.add(itemQ)
                shippingItemsR.add(itemR)
                shippingItemsS.add(itemS)
            } else {
                shippingItemsA.add(ShippingItem(0, "-"))
                shippingItemsB.add(ShippingItem(0, "-"))
                shippingItemsC.add(ShippingItem(0, "-"))
                shippingItemsD.add(ShippingItem(0, "-"))
                shippingItemsE.add(ShippingItem(0, "-"))
                shippingItemsF.add(ShippingItem(0, "-"))
                shippingItemsG.add(ShippingItem(0, "-"))
                shippingItemsH.add(ShippingItem(0, "-"))
                shippingItemsI.add(ShippingItem(0, "-"))
                shippingItemsJ.add(ShippingItem(0, "-"))
                shippingItemsK.add(ShippingItem(0, "-"))
                shippingItemsL.add(ShippingItem(0, "-"))
                shippingItemsM.add(ShippingItem(0, "-"))
                shippingItemsN.add(ShippingItem(0, "-"))
                shippingItemsO.add(ShippingItem(0, "-"))
                shippingItemsP.add(ShippingItem(0, "-"))
                shippingItemsQ.add(ShippingItem(0, "-"))
                shippingItemsR.add(ShippingItem(0, "-"))
                shippingItemsS.add(ShippingItem(0, "-"))
            }
        }
        availableTime.sortedBy { it }.forEach { time ->
            when (time) {
                "09:00-09:30" -> {
                    shippingItems.addAll(shippingItemsA)
                }
                "09:30-10:00" -> {
                    shippingItems.addAll(shippingItemsB)
                }
                "10:00-10:30" -> {
                    shippingItems.addAll(shippingItemsC)
                }
                "10:30-11:00" -> {
                    shippingItems.addAll(shippingItemsD)
                }
                "11:00-11:30" -> {
                    shippingItems.addAll(shippingItemsE)
                }
                "11:30-12:00" -> {
                    shippingItems.addAll(shippingItemsF)
                }
                "13:00-13:30" -> {
                    shippingItems.addAll(shippingItemsG)
                }
                "13:30-14:00" -> {
                    shippingItems.addAll(shippingItemsH)
                }
                "14:00-14:30" -> {
                    shippingItems.addAll(shippingItemsI)
                }
                "14:30-15:00" -> {
                    shippingItems.addAll(shippingItemsJ)
                }
                "15:00-15:30" -> {
                    shippingItems.addAll(shippingItemsK)
                }
                "15:30-16:00" -> {
                    shippingItems.addAll(shippingItemsL)
                }
                "16:00-16:30" -> {
                    shippingItems.addAll(shippingItemsM)
                }
                "16:30-17:00" -> {
                    shippingItems.addAll(shippingItemsN)
                }
                "17:00-17:30" -> {
                    shippingItems.addAll(shippingItemsO)
                }
                "17:30-18:00" -> {
                    shippingItems.addAll(shippingItemsP)
                }
                "18:00-18:30" -> {
                    shippingItems.addAll(shippingItemsQ)
                }
                "18:30-19:00" -> {
                    shippingItems.addAll(shippingItemsR)
                }
                "19:00-19:30" -> {
                    shippingItems.addAll(shippingItemsS)
                }
            }
        }
        mCalendarView.hideTimeColumn(availableTime)
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
                    .setPositiveButton(getString(R.string.ok_alert)) { dialog, _ -> dialog.dismiss() }
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