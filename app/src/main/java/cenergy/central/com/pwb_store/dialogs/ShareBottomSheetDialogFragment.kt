package cenergy.central.com.pwb_store.dialogs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import kotlinx.android.synthetic.main.fragment_share_bottom_sheet.*
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast


class ShareBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private var shareText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            shareText = it.getString(BUNDLE_TEXT_SHARE, "")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_share_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Share to Line App
        shareToLineView.setOnClickListener {
            val uri = Uri.parse("https://line.me/R/msg/text/?$shareText")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        // Copy to clip board
        copyToClipboardView.setOnClickListener {
            val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("eOrdering_link", shareText)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val TAG = "ShareBottomSheet"
        private const val BUNDLE_TEXT_SHARE = "BUNDLE_TEXT_SHARE"

        fun newInstance(text: String): ShareBottomSheetDialogFragment {
            return ShareBottomSheetDialogFragment().apply {
                arguments = Bundle().apply { putString(BUNDLE_TEXT_SHARE, text) }
            }
        }
    }
}