package cenergy.central.com.pwb_store.dialogs

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import cenergy.central.com.pwb_store.utils.BarcodeUtils
import cenergy.central.com.pwb_store.view.PowerBuyIconButton
import android.widget.LinearLayout
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.extensions.dpToPx

class BarcodeDialogFragment : DialogFragment() {

    private var paymentRedirect: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            retrieveArguments(arguments)
        } else {
            retrieveInstanceState(savedInstanceState)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.qr_code_dialog_fragment, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
    }

    private fun setupView(rootView: View) {
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val barcodeImageView = rootView.findViewById<ImageView>(R.id.qr_code_dialog)
        val closeButton = rootView.findViewById<PowerBuyIconButton>(R.id.close_btn)
        val bitmapBarcode = BarcodeUtils.createQRCode(paymentRedirect)
        barcodeImageView.setImageBitmap(bitmapBarcode)
        barcodeImageView.layoutParams.height = 450.dpToPx(rootView.context)
        barcodeImageView.layoutParams.width = 450.dpToPx(rootView.context)
        closeButton.layoutParams.width = 250.dpToPx(rootView.context)
        closeButton.setOnClickListener { dialog?.dismiss() }
    }

    private fun retrieveInstanceState(bundle: Bundle) {
        paymentRedirect = bundle.getString(ARG_PAYMENT_REDIRECT) ?: ""
    }

    private fun retrieveArguments(bundle: Bundle?) {
        bundle?.let { paymentRedirect = bundle.getString(ARG_PAYMENT_REDIRECT) ?: "" }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ARG_PAYMENT_REDIRECT, paymentRedirect)
    }

    companion object {
        private const val ARG_PAYMENT_REDIRECT = "payment_redirect"

        fun newInstance(paymentRedirect: String): BarcodeDialogFragment {
            val fragment = BarcodeDialogFragment()
            val bundle = Bundle()
            bundle.putString(ARG_PAYMENT_REDIRECT, paymentRedirect)
            fragment.arguments = bundle
            return fragment
        }
    }

    class Builder {
        private var paymentRedirect: String = ""

        fun setMessage(paymentRedirect: String): Builder {
            this.paymentRedirect = paymentRedirect
            return this
        }

        fun build(): BarcodeDialogFragment {
            return newInstance(paymentRedirect)
        }
    }
}