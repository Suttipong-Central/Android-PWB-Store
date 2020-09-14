package cenergy.central.com.pwb_store.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.PaymentProtocol
import cenergy.central.com.pwb_store.adapter.PaymentMethodAdapter
import cenergy.central.com.pwb_store.dialogs.interfaces.PaymentItemClickListener
import cenergy.central.com.pwb_store.model.PaymentMethod
import cenergy.central.com.pwb_store.model.PaymentMethodView
import cenergy.central.com.pwb_store.model.response.PaymentCreditCardPromotion
import cenergy.central.com.pwb_store.utils.Analytics
import cenergy.central.com.pwb_store.utils.Screen

class PaymentSelectMethodFragment : Fragment(), PaymentItemClickListener {
    private val analytics by lazy { context?.let { Analytics(it) } }

    private lateinit var paymentProtocol: PaymentProtocol
    private lateinit var recycler: RecyclerView
    private lateinit var selectMethodAdapter: PaymentMethodAdapter

    private var paymentMethods: List<PaymentMethod> = listOf()
    private var paymentPromotions: List<PaymentCreditCardPromotion> = listOf()
    private var deliveryCode: String = ""
    private var items = arrayListOf<PaymentMethodView>()
    private var selectedPaymentMethod: PaymentMethod? = null
    private var promotionId: Int? = null
    private var promotionCode: String? = null

    companion object {
        private const val ARG_DELIVERY_CODE = "ARG_DELIVERY_CODE"

        fun newInstance(methodCode: String): PaymentSelectMethodFragment {
            val fragment = PaymentSelectMethodFragment()
            val args = Bundle()
            args.putString(ARG_DELIVERY_CODE, methodCode)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        paymentProtocol = context as PaymentProtocol
        paymentMethods = paymentProtocol.getPaymentMethods()
        paymentPromotions = paymentProtocol.getPaymentPromotions()
        selectedPaymentMethod = paymentProtocol.getSelectedPaymentMethod()
        promotionId = paymentProtocol.getSelectedPromotionId()
        promotionCode = paymentProtocol.getSelectedPromotionCode()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            deliveryCode = it.getString(ARG_DELIVERY_CODE, "")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_payment_select_methods, container, false)
        setupView(rootView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPaymentMethodOptions()
    }

    override fun onResume() {
        super.onResume()
        analytics?.trackScreen(Screen.SELECT_PAYMENT)
    }

    // region {@link PaymentTypesClickListener.onClickedPaymentItem}
    override fun onClickedPayButton() {
        selectedPaymentMethod?.let { paymentProtocol.setPaymentInformation(it, promotionId, promotionCode) }
    }

    override fun onClickedPaymentItem(paymentMethod: PaymentMethod) {
        // clear promotion
        this.promotionId = null
        this.promotionCode = null
        val newItems = items.map {
            if (it is PaymentMethodView.PaymentItemView) {
                it.promotionId = null // clear! (only use for first time)
                if (it.paymentMethod.code == paymentMethod.code) {
                    this.selectedPaymentMethod = it.paymentMethod
                    it.selected = true
                } else {
                    it.selected = false
                }
            }

            // checked?
            if (it is PaymentMethodView.PayButtonItemView) {
                it.enable = true
            }
            it
        }
        this.items = ArrayList(newItems)
        selectMethodAdapter.submitList(this.items)

        // update
        selectedPaymentMethod?.let { paymentProtocol.updatePaymentInformation(it, promotionId, promotionCode) }
    }

    override fun onSelectedPromotion(paymentMethod: String, promotionId: Int, promotionCode: String) {
        // updated
        this.promotionId = promotionId
        selectedPaymentMethod?.let { paymentProtocol.updatePaymentInformation(it, promotionId, promotionCode) }
    }

    override fun onSelectedDefaultPromotion(paymentMethod: String) {
        this.promotionId = null
        selectedPaymentMethod?.let { paymentProtocol.updatePaymentInformation(it, null, null) }
    }
    // endregion

    private fun setupPaymentMethodOptions() {
        items.clear()
        // add header item
        items.add(PaymentMethodView.HeaderItemView(getString(R.string.select_payment_types)))

        // add payment method item
        if (paymentMethods.isNotEmpty()) {
            items.addAll(paymentMethods.mapTo(arrayListOf<PaymentMethodView>(), {
                when (it.code) {
                    PaymentMethod.PAY_AT_STORE -> {
                        PaymentMethodView.PaymentItemView(paymentMethod = it,
                                viewType = PaymentMethodAdapter.PAY_AT_STORE)
                    }
                    PaymentMethod.FULL_PAYMENT -> {
                        val creditCardPromotions = paymentPromotions.filter { p ->
                            p.paymentMethod == PaymentMethod.FULL_PAYMENT
                        }
                        val selected = isFullPaymentOption()
                        PaymentMethodView.PaymentItemView(paymentMethod = it,
                                promotions = creditCardPromotions,
                                viewType = PaymentMethodAdapter.FULL_PAYMENT,
                                promotionId = promotionId,
                                selected = selected)
                    }
                    PaymentMethod.INSTALLMENT -> {
                        val installmentsPromotion = paymentPromotions.filter { p ->
                            p.paymentMethod == PaymentMethod.INSTALLMENT
                        }
                        val selected = isInstallmentPaymentOption()
                        PaymentMethodView.PaymentItemView(paymentMethod = it,
                                promotions = installmentsPromotion,
                                viewType = PaymentMethodAdapter.INSTALLMENT,
                                promotionId = promotionId,
                                selected = selected)
                    }
                    PaymentMethod.E_ORDERING -> {
                        PaymentMethodView.PaymentItemView(paymentMethod = it,
                                viewType = PaymentMethodAdapter.E_ORDERING)
                    }
                    PaymentMethod.BANK_AND_COUNTER_SERVICE -> {
                        PaymentMethodView.PaymentItemView(paymentMethod = it,
                                viewType = PaymentMethodAdapter.BANK_AND_COUNTER_SERVICE)
                    }
                    PaymentMethod.CASH_ON_DELIVERY -> {
                        PaymentMethodView.PaymentItemView(paymentMethod = it,
                                viewType = PaymentMethodAdapter.CASH_ON_DELIVERY)
                    }
                    else -> {
                        PaymentMethodView.PaymentItemView(paymentMethod = it,
                                viewType = PaymentMethodAdapter.OTHER)
                    }
                }
            }))

            // add pay button
            items.add(PaymentMethodView.PayButtonItemView(isFullPaymentOption()))
        } else {
            items.add(PaymentMethodView.EmptyItemView())
        }

        // set to adapter
        selectMethodAdapter.submitList(items)
    }

    private fun isFullPaymentOption(): Boolean {
        return (this.selectedPaymentMethod != null
                && this.selectedPaymentMethod!!.code == PaymentMethod.FULL_PAYMENT)
    }

    private fun isInstallmentPaymentOption(): Boolean {
        return (this.selectedPaymentMethod != null
                && this.selectedPaymentMethod!!.code == PaymentMethod.INSTALLMENT)
    }

    private fun setupView(rootView: View) {
        recycler = rootView.findViewById(R.id.recycler_select_methods)
        selectMethodAdapter = PaymentMethodAdapter(this)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = selectMethodAdapter

    }
}