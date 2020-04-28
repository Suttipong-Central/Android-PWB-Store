package cenergy.central.com.pwb_store.dialogs

import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.DialogFragment
import android.view.*
import android.widget.ImageView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.extensions.dpToPx
import cenergy.central.com.pwb_store.view.PowerBuyTextView

class PrivacyDialogFragment : DialogFragment() {
    private var privacy: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            retrieveArguments(arguments)
        } else {
            retrieveInstanceState(savedInstanceState)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.privacy_dialog_fragment, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
    }

    private fun setupView(rootView: View) {
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val tvPrivacy = rootView.findViewById<PowerBuyTextView>(R.id.privacyTextView)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvPrivacy.text = Html.fromHtml(privacy, Html.FROM_HTML_MODE_COMPACT)
        } else {
            tvPrivacy.text = Html.fromHtml(privacy)
        }
    }

    private fun retrieveInstanceState(bundle: Bundle) {
        privacy = bundle.getString(PRIVACY_POLICY) ?: ""
    }

    private fun retrieveArguments(bundle: Bundle?) {
        bundle?.let { privacy = bundle.getString(PRIVACY_POLICY) ?: "" }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(PRIVACY_POLICY, privacy)
    }

    companion object {
        private const val PRIVACY_POLICY = "PRIVACY_POLICY"

        fun newInstance(privacy: String): PrivacyDialogFragment {
            val fragment = PrivacyDialogFragment()
            val bundle = Bundle()
            bundle.putString(PRIVACY_POLICY, privacy)
            fragment.arguments = bundle
            return fragment
        }
    }

    class Builder {
        private var privacy: String = ""

        fun setMessage(privacy: String): Builder {
            this.privacy = privacy
            return this
        }

        fun build(): PrivacyDialogFragment {
            return newInstance(privacy)
        }
    }
}