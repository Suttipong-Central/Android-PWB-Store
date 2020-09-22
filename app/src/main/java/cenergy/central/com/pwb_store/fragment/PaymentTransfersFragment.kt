package cenergy.central.com.pwb_store.fragment

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.PaymentProtocol
import cenergy.central.com.pwb_store.adapter.PaymentTransferAdapter
import cenergy.central.com.pwb_store.adapter.PwbArrayAdapter
import cenergy.central.com.pwb_store.fragment.interfaces.PaymentTransferListener
import cenergy.central.com.pwb_store.utils.ValidationHelper
import cenergy.central.com.pwb_store.utils.showCommonDialog
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_payment_transfers.*
import java.util.*

class PaymentTransfersFragment : Fragment() {
    private lateinit var paymentProtocol: PaymentProtocol
    private lateinit var paymentTransferListener: PaymentTransferListener
    private var tabSelected = 0
    private var bankAgentItems = arrayListOf<PaymentTransferAdapter.AgentItem>()
    private var counterServiceItems = arrayListOf<PaymentTransferAdapter.AgentItem>()
    private var bankChannels = listOf<String>()
    private val bankChannelAdapter by lazy { PwbArrayAdapter(context!!, R.layout.layout_text_item, arrayListOf()) }
    private val paymentTransferAdapter by lazy {
        PaymentTransferAdapter {
            errorAgentTextView.visibility = View.GONE // hide error
            if (it.isBankAgent()) {
                // update selected
                this.bankAgentItems.forEach { item ->
                    item.isSelect = item.agent.agentId == it.agentId
                }
                updateAgentItems(this.bankAgentItems)

                // update bank channel
                this.bankChannels = it.getChannels()
                updateBankChannels(this.bankChannels)
            }
            if (it.isCounterService()){
                this.counterServiceItems.forEach { item ->
                    item.isSelect = item.agent.agentId == it.agentId
                }
                updateAgentItems(this.counterServiceItems)
            }
        }
    }

    private fun updateBankChannels(bankChannels: List<String>) {
        paymentChannelOptions.setText("")
        bankChannelAdapter.setItems(bankChannels)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        paymentProtocol = context as PaymentProtocol
        paymentTransferListener = context as PaymentTransferListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bankAgents = paymentProtocol.getPaymentAgents().filter {
            it.isBankAgent()
        }
        this.bankAgentItems.clear()
        bankAgents.mapTo(bankAgentItems, {
            PaymentTransferAdapter.AgentItem(it)
        })

        val counterService = paymentProtocol.getPaymentAgents().filter {
            it.isCounterService()
        }
        this.counterServiceItems.clear()
        counterService.mapTo(counterServiceItems, {
            PaymentTransferAdapter.AgentItem(it)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_payment_transfers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTab()
        setupInput()
        setupOptionRecycler()
        payNowButton.setOnClickListener {
            initPay()
        }
    }

    private fun setupInput() {
        paymentMobileInput.setEditTextInputType(InputType.TYPE_CLASS_NUMBER)
        paymentMobileInput.setTextLength(10)
        paymentEmailInput.setEditTextInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
    }

    private fun hasEmptyInput(): Boolean {
        val validator = ValidationHelper.getInstance(context!!)
        // validate input
        val agentSelected = paymentTransferAdapter.getItemSelected()
        if (agentSelected == null) {
            errorAgentTextView.visibility = View.VISIBLE
        } else {
            errorAgentTextView.visibility = View.GONE
        }

        if (paymentPayerInput.getText().isNotEmpty()) {
            paymentPayerInput.setError(validator.validText(paymentPayerInput.getText()))
        } else {
            paymentPayerInput.setError(getString(R.string.error_form_input_required))
        }

        if (tabSelected != TAB_COUNTER_SERVICE) {
            if (paymentChannelOptions.getText().isNotEmpty()) {
                paymentChannelOptions.setError(validator.validText(paymentChannelOptions.getText()))
            } else {
                paymentChannelOptions.setError(getString(R.string.error_form_input_required))
            }
        }

        if (paymentEmailInput.getText().isNotEmpty()) {
            paymentEmailInput.setError(validator.validEmail(paymentEmailInput.getText()))
        } else {
            paymentEmailInput.setError(getString(R.string.error_form_input_required))
        }

        if (paymentMobileInput.getText().isNotEmpty()) {
            paymentMobileInput.setError(validator.validThaiMobileNumber(paymentMobileInput.getText()))
        } else {
            paymentMobileInput.setError(getString(R.string.error_form_input_required))
        }

        return (errorAgentTextView.visibility == View.VISIBLE
                || paymentPayerInput.getError() != null
                || paymentChannelOptions.getError() != null || paymentEmailInput.getError() != null
                || paymentMobileInput.getError() != null)
    }

    private fun initPay() {
        if (!hasEmptyInput()) {
            val item = paymentTransferAdapter.getItemSelected()
            val payerName = paymentPayerInput.getText()
            val payerEmail = paymentEmailInput.getText()
            val agentCode = item!!.agent.code
            val agentChannelCode = if (tabSelected != TAB_COUNTER_SERVICE) {
                paymentChannelOptions.getText()
            } else {
                item.agent.channel
            }
            val mobileNumber = paymentMobileInput.getText()
            paymentTransferListener.startPayNow(payerName, payerEmail, agentCode,
                    agentChannelCode, mobileNumber)
        } else {
            activity?.showCommonDialog(resources.getString(R.string.error_form_input_required))
        }
    }

    private fun updateAgentItems(bankAgentItems: ArrayList<PaymentTransferAdapter.AgentItem>) {
        paymentTransferAdapter.source = bankAgentItems
    }

    private fun setupOptionRecycler() {
        agentRecycler?.apply {
            layoutManager = GridLayoutManager(context, 5)
            setHasFixedSize(true)
            adapter = paymentTransferAdapter
        }
        when (tabSelected) {
            TAB_COUNTER_SERVICE -> {
                updateAgentItems(counterServiceItems)
                paymentChannelOptions.visibility = View.GONE
            }
            else -> {
                updateAgentItems(bankAgentItems)
                paymentChannelOptions.visibility = View.VISIBLE
                // setup bank channel
                bankChannelAdapter.setCallback(object : PwbArrayAdapter.PwbArrayAdapterListener {
                    override fun onItemClickListener(item: String) {
                        paymentChannelOptions.setError(null) // clear error
                        paymentChannelOptions.setText(item)
                        hideKeyboard()
                    }
                })
                paymentChannelOptions.setAdapter(bankChannelAdapter)
            }
        }
    }

    private fun setupTab() {
        paymentTransferTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                this@PaymentTransfersFragment.tabSelected = tab.position
                paymentTransferTabLayout.getTabAt(tab.position)?.select()
                setupOptionRecycler()
            }
        })
        // set default tab selected
        paymentTransferTabLayout.getTabAt(tabSelected)?.select()
    }

    private fun hideKeyboard() {
        val inputManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocus = activity?.currentFocus
        if (currentFocus != null) {
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
            inputManager.hideSoftInputFromInputMethod(currentFocus.windowToken, 0)
        }
    }

    companion object {
        const val TAB_COUNTER_SERVICE = 0
    }
}