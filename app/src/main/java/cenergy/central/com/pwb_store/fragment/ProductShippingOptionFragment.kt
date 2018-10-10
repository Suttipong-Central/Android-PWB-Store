package cenergy.central.com.pwb_store.fragment

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.ProductDetailListener
import cenergy.central.com.pwb_store.manager.HttpManagerHDLOld
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.model.ShippingDao
import cenergy.central.com.pwb_store.model.ShippingItem
import cenergy.central.com.pwb_store.model.request.CustomDetailRequest
import cenergy.central.com.pwb_store.model.request.PeriodRequest
import cenergy.central.com.pwb_store.model.request.ShippingRequest
import cenergy.central.com.pwb_store.model.request.SkuDataRequest
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.APIErrorHDLUtils
import cenergy.central.com.pwb_store.utils.DialogUtils
import cenergy.central.com.pwb_store.view.CalendarViewCustom
import cenergy.central.com.pwb_store.view.PowerBuyTextView
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
    private val shippingItems = arrayListOf<ShippingItem>()
    private val shippingItemsA = arrayListOf<ShippingItem>()
    private val shippingItemsB = arrayListOf<ShippingItem>()
    private val shippingItemsC = arrayListOf<ShippingItem>()
    private val shippingItemsD = arrayListOf<ShippingItem>()
    private val shippingItemsE = arrayListOf<ShippingItem>()
    private val shippingItemsF = arrayListOf<ShippingItem>()

    private var NextPreWeekday: Array<String>? = null
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
        product?.let { it ->
            if (it.deliveryMethod.contains(HOME_DELIVERY)) {
                getDataSlot(getMonth())
            }
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
    private fun getMonth(): String {

        val now = Calendar.getInstance()
        val format = SimpleDateFormat("yyyy-MM")
        val month: String
        month = format.format(now.time)

        return month

    }

    private fun getDataSlot(mount: String) {
        showProgressDialog()
        product?.let {
            val userInformation = database.userInformation
            val store = userInformation.store
            mSkuDataRequests.add(SkuDataRequest(it.extension?.barcode, 1, it.sku, 1, store!!.storeId.toString(), ""))
            val separated = mount.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val year = separated[0]
            val month = separated[1]
            mPeriodRequest = PeriodRequest(Integer.parseInt(year), Integer.parseInt(month))
            mCustomDetailRequest = CustomDetailRequest("1", "", "")
            mShippingRequest = ShippingRequest(store.postalCode, mSkuDataRequests, mPeriodRequest, mCustomDetailRequest)

            HttpManagerHDLOld.getInstance().hdlService.checkShippingTime(
                    mShippingRequest).enqueue(object : Callback<ShippingDao> {
                override fun onResponse(call: Call<ShippingDao>, response: Response<ShippingDao>) {
                    if (response.isSuccessful) {
                        mShippingDao = response.body()
                        createShippingData(mShippingDao)
                        progressDialog?.dismiss()
                    } else {
                        val error = APIErrorHDLUtils.parseError(response)
                        Log.e("ShippingOption", "onResponse: " + error.errorUserMessage)
                        progressDialog?.dismiss()

                    }
                }

                override fun onFailure(call: Call<ShippingDao>, t: Throwable) {
                    progressDialog?.dismiss()
                }
            })
        }
    }

    private fun createShippingData(shippingDao: ShippingDao?) {
        if (NextPreWeekday == null) {
            NextPreWeekday = mCalendarView.weekDay
            monthPreday = mCalendarView.monthDay
        }
        sunday = NextPreWeekday?.get(0)
        monday = NextPreWeekday?.get(1)
        tuesday = NextPreWeekday?.get(2)
        wednesday = NextPreWeekday?.get(3)
        thursday = NextPreWeekday?.get(4)
        friday = NextPreWeekday?.get(5)
        saturday = NextPreWeekday?.get(6)
        for (shipping in shippingDao!!.shippings) {
            val shippingDate = shipping.shippingDate
            if (sunday.equals(shippingDate, ignoreCase = true)) {
                for (shippingItem in shipping.shippingItems) {
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
                for (shippingItem in shipping.shippingItems) {
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
            if (tuesday.equals(shippingDate, ignoreCase = true)) {
                for (shippingItem in shipping.shippingItems) {
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
            } else if (wednesday.equals(shippingDate, ignoreCase = true)) {
                for (shippingItem in shipping.shippingItems) {
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
            } else if (thursday.equals(shippingDate, ignoreCase = true)) {
                for (shippingItem in shipping.shippingItems) {
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
            } else if (friday.equals(shippingDate, ignoreCase = true)) {
                for (shippingItem in shipping.shippingItems) {
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
            } else if (saturday.equals(shippingDate, ignoreCase = true)) {
                for (shippingItem in shipping.shippingItems) {
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
            shippingItems.add(ShippingItem(shippingItemA.getId(), shippingItemA.getDescription()))
        }

        for (shippingItemB in shippingItemsB) {
            shippingItems.add(ShippingItem(shippingItemB.getId(), shippingItemB.getDescription()))
        }

        for (shippingItemC in shippingItemsC) {
            shippingItems.add(ShippingItem(shippingItemC.getId(), shippingItemC.getDescription()))
        }

        for (shippingItemD in shippingItemsD) {
            shippingItems.add(ShippingItem(shippingItemD.getId(), shippingItemD.getDescription()))
        }

        for (shippingItemE in shippingItemsE) {
            shippingItems.add(ShippingItem(shippingItemE.getId(), shippingItemE.getDescription()))
        }

        for (shippingItemF in shippingItemsF) {
            shippingItems.add(ShippingItem(shippingItemF.getId(), shippingItemF.getDescription()))
        }

        mCalendarView.setTimeSlotItem(shippingItems)
    }

    // endregion


    private fun showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = DialogUtils.createProgressDialog(context)
            progressDialog?.show()
        } else {
            progressDialog?.show()
        }
    }

    companion object {
        private const val HOME_DELIVERY = "pwb_homedelivery"
    }
}