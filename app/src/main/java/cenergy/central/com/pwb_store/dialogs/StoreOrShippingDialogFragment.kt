package cenergy.central.com.pwb_store.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.dialogs.adapter.StoreOrShippingAdapter
import cenergy.central.com.pwb_store.dialogs.interfaces.StoreOrShippingClickListener
import cenergy.central.com.pwb_store.model.DialogOption

class StoreOrShippingDialogFragment : DialogFragment() {
    private var options: ArrayList<DialogOption> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            retrieveArguments(arguments)
        } else {
            retrieveInstanceState(savedInstanceState)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.store_or_shipping_dialog_fragment, container)

    }

    private fun retrieveInstanceState(bundle: Bundle) {
        options = bundle.getParcelableArrayList(ARG_OPTIONS)
    }

    private fun retrieveArguments(bundle: Bundle?) {
        bundle?.let { options = bundle.getParcelableArrayList(ARG_OPTIONS) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
    }

    private fun setupView(rootView: View) {
        dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
        val recycler: RecyclerView = rootView.findViewById(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recycler.adapter = StoreOrShippingAdapter(options, storeOrShippingClickListener)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(ARG_OPTIONS, options)
    }

    companion object {
        private var storeOrShippingClickListener: StoreOrShippingClickListener? = null
        private const val ARG_OPTIONS = "arg_options"

        fun newInstance(options: ArrayList<DialogOption>): StoreOrShippingDialogFragment {
            val fragment = StoreOrShippingDialogFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList(ARG_OPTIONS, options)
            fragment.arguments = bundle
            return fragment
        }

        fun setOnStoreOrShippingListener(storeOrShippingClickListener: StoreOrShippingClickListener) {
            Companion.storeOrShippingClickListener = storeOrShippingClickListener
        }
    }

    class Builder {
        private var options: ArrayList<DialogOption> = arrayListOf()

        fun setMessage(options: ArrayList<DialogOption>): Builder {
            this.options = options
            return this
        }

        fun build(): StoreOrShippingDialogFragment {
            return newInstance(options)
        }
    }
}