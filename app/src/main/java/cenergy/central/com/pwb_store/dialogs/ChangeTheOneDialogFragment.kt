package cenergy.central.com.pwb_store.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.view.PowerBuyEditTextBorder
import cenergy.central.com.pwb_store.view.PowerBuyIconButton

class ChangeTheOneDialogFragment : DialogFragment() {

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
        val okBtn = rootView.findViewById<PowerBuyIconButton>(R.id.dialog_ok_btn)
        okBtn.setOnClickListener {
            dialog.dismiss()
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
