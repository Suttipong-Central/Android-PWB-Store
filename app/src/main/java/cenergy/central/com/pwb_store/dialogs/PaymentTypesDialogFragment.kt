package cenergy.central.com.pwb_store.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.dialogs.adapter.PaymentTypesAdapter
import cenergy.central.com.pwb_store.dialogs.interfaces.PaymentTypesClickListener
import cenergy.central.com.pwb_store.model.response.PaymentMethod

class PaymentTypesDialogFragment : DialogFragment() {

    private var paymentMethods: ArrayList<PaymentMethod> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            retrieveArguments(arguments)
        } else {
            retrieveInstanceState(savedInstanceState)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.payment_types_dialog_fragment, container)

    }

    private fun retrieveInstanceState(bundle: Bundle) {
        paymentMethods = bundle.getParcelableArrayList(ARG_OPTIONS)
    }

    private fun retrieveArguments(bundle: Bundle?) {
        bundle?.let { paymentMethods = bundle.getParcelableArrayList(ARG_OPTIONS) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
    }

    private fun setupView(rootView: View) {
        dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
        isCancelable = false
        val recycler: RecyclerView = rootView.findViewById(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recycler.adapter = PaymentTypesAdapter(paymentMethods, paymentTypesClickListener)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(ARG_OPTIONS, paymentMethods)
    }

    companion object {
        var paymentTypesClickListener: PaymentTypesClickListener? = null
        private const val ARG_OPTIONS = "arg_options"

        fun newInstance(paymentMethods: ArrayList<PaymentMethod>): PaymentTypesDialogFragment {
            val fragment = PaymentTypesDialogFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList(ARG_OPTIONS, paymentMethods)
            fragment.arguments = bundle
            return fragment
        }

        fun setPaymentTypesListener(paymentTypesClickListener: PaymentTypesClickListener) {
            Companion.paymentTypesClickListener = paymentTypesClickListener
        }
    }

    class Builder {
        private var options: ArrayList<PaymentMethod> = arrayListOf()

        fun setMessage(options: ArrayList<PaymentMethod>): Builder {
            this.options = options
            return this
        }

        fun build(): PaymentTypesDialogFragment {
            return newInstance(options)
        }
    }
}