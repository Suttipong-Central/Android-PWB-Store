package cenergy.central.com.pwb_store.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.ShoppingCartAdapter
import cenergy.central.com.pwb_store.manager.Contextor
import cenergy.central.com.pwb_store.manager.listeners.PaymentClickLintener
import cenergy.central.com.pwb_store.manager.listeners.PaymentDescriptionListener
import cenergy.central.com.pwb_store.model.CartItem
import cenergy.central.com.pwb_store.view.PowerBuyEditTextBorder
import cenergy.central.com.pwb_store.view.PowerBuyTextView
import java.text.NumberFormat
import java.util.*

class PaymentDescriptionFragment : Fragment() {

    private lateinit var recycler: RecyclerView
    private lateinit var firstNameEdt: PowerBuyEditTextBorder
    private lateinit var lastNameEdt: PowerBuyEditTextBorder
    private lateinit var contactNumberEdt: PowerBuyEditTextBorder
    private lateinit var emailEdt: PowerBuyEditTextBorder
    private lateinit var houseNoEdt: PowerBuyEditTextBorder
    private lateinit var placeOrBuildingEdit: PowerBuyEditTextBorder
    private lateinit var soiEdt: PowerBuyEditTextBorder
    private lateinit var streetEdt: PowerBuyEditTextBorder
    private lateinit var provinceEdt: PowerBuyEditTextBorder
    private lateinit var districtEdt: PowerBuyEditTextBorder
    private lateinit var subDistrictEdt: PowerBuyEditTextBorder
    private lateinit var postCodeEdt: PowerBuyEditTextBorder
    private lateinit var tellEdt: PowerBuyEditTextBorder
    private lateinit var totalPrice: PowerBuyTextView
    private lateinit var paymentBtn: CardView
    private var cartItemList: List<CartItem> = listOf()
    private var contactNo: String? = null
    private var paymentClickListener: PaymentClickLintener? = null
    private var paymentDescriptionListener: PaymentDescriptionListener? = null

    companion object {
        private const val contactNumber = "CONTACT_NUMBER"

        fun newInstance(contactNo: String): PaymentDescriptionFragment {
            val fragment = PaymentDescriptionFragment()
            val args = Bundle()
            args.putString(contactNumber, contactNo)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        paymentClickListener = context as PaymentClickLintener
        paymentDescriptionListener = context as PaymentDescriptionListener
        cartItemList = paymentDescriptionListener!!.getItemList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactNo = arguments?.getString(contactNumber)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_payment_description, container, false)
        setupView(rootView)
        return rootView
    }

    private fun setupView(rootView: View) {
        recycler = rootView.findViewById(R.id.recycler_product_list_payment)
        firstNameEdt = rootView.findViewById(R.id.first_name_payment)
        lastNameEdt = rootView.findViewById(R.id.last_name_payment)
        contactNumberEdt = rootView.findViewById(R.id.contact_number_payment)
        emailEdt = rootView.findViewById(R.id.email_payment)
        houseNoEdt = rootView.findViewById(R.id.house_no_payment)
        placeOrBuildingEdit = rootView.findViewById(R.id.place_or_building_payment)
        soiEdt = rootView.findViewById(R.id.soi_payment)
        streetEdt = rootView.findViewById(R.id.street_payment)
        provinceEdt = rootView.findViewById(R.id.province_payment)
        districtEdt = rootView.findViewById(R.id.district_payment)
        subDistrictEdt = rootView.findViewById(R.id.sub_district_payment)
        postCodeEdt = rootView.findViewById(R.id.post_code_payment)
        tellEdt = rootView.findViewById(R.id.tell_payment)
        paymentBtn = rootView.findViewById(R.id.payment_button_payment)
        totalPrice = rootView.findViewById(R.id.txt_total_price_payment_description)
        val shoppingCartAdapter = ShoppingCartAdapter(null)
        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recycler.isNestedScrollingEnabled = false
        recycler.adapter = shoppingCartAdapter
        shoppingCartAdapter.cartItemList = this.cartItemList

        val unit = Contextor.getInstance().context.getString(R.string.baht)
        var total = 0.0
        cartItemList.forEach {
            total += it.qty!! * it.price!!
        }
        totalPrice.text = getDisplayPrice(unit, total.toString())
        contactNo?.let { contactNumberEdt.setText(it) }
        paymentBtn.setOnClickListener { paymentClickListener?.onPaymentClickListener() }
    }

    private fun getDisplayPrice(unit: String, price: String): String {
        return String.format(Locale.getDefault(), "%s %s", unit, NumberFormat.getInstance(
                Locale.getDefault()).format(java.lang.Double.parseDouble(price)))
    }
}