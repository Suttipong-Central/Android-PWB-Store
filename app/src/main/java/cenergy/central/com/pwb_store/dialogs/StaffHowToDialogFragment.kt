package cenergy.central.com.pwb_store.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import android.widget.ImageView
import cenergy.central.com.pwb_store.R

class StaffHowToDialogFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.staff_how_to_dialog_fragment, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
    }

    private fun setupView(rootView: View) {
        dialog.window.setBackgroundDrawableResource(android.R.color.transparent)

        val cancelBtn = rootView.findViewById<ImageView>(R.id.img_cancel)
        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
    }

    companion object {
        fun newInstance(): StaffHowToDialogFragment {
            val fragment = StaffHowToDialogFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    class Builder {
        fun build(): StaffHowToDialogFragment {
            return newInstance()
        }
    }
}