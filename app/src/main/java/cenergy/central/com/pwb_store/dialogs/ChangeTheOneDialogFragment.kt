package cenergy.central.com.pwb_store.dialogs

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.dialogs.interfaces.PaymentT1Listener
import cenergy.central.com.pwb_store.utils.ValidationHelper
import cenergy.central.com.pwb_store.view.PowerBuyEditTextBorder
import cenergy.central.com.pwb_store.view.PowerBuyIconButton
import kotlinx.android.synthetic.main.single_cell_layout.*

class ChangeTheOneDialogFragment : DialogFragment() {
    private var listener: PaymentT1Listener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as PaymentT1Listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.change_the_one_dialog_fragment, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
    }

    private fun setupView(rootView: View) {
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.window?.attributes?.gravity = Gravity.CENTER

        val editText = rootView.findViewById<PowerBuyEditTextBorder>(R.id.the_one_edit_txt)
        editText.setEditTextInputType(InputType.TYPE_CLASS_NUMBER)
        editText.setTextLength(10)

        val okBtn = rootView.findViewById<PowerBuyIconButton>(R.id.dialog_ok_btn)
        okBtn.setOnClickListener {
           sentMobileNumber(editText)
        }
    }

    private fun sentMobileNumber(editText: PowerBuyEditTextBorder) {
        val mobile = editText.getText()

        // validate mobile number
        val error = context?.let { ValidationHelper.getInstance(it).validThaiPhoneNumber(mobile) }
        if (error != null && error.isNotBlank()) {
            editText.setError(error)
            return
        }

        if (mobile.isNotBlank()) {
            listener?.onChangingT1Member(mobile)
            dialog?.dismiss()
        }
    }

    companion object {
        fun newInstance(): ChangeTheOneDialogFragment {
            val fragment = ChangeTheOneDialogFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    class Builder {
        fun build(): ChangeTheOneDialogFragment {
            return newInstance()
        }
    }
}
