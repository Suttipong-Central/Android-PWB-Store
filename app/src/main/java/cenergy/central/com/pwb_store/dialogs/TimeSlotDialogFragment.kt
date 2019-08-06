package cenergy.central.com.pwb_store.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.dialogs.adapter.TimeSlotAdapter
import cenergy.central.com.pwb_store.dialogs.interfaces.TimeSlotClickListener
import cenergy.central.com.pwb_store.model.ShippingSlot

class TimeSlotDialogFragment : DialogFragment() {
    private var shippingSlot: ArrayList<ShippingSlot> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            retrieveArguments(arguments)
        } else {
            retrieveInstanceState(savedInstanceState)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.time_slot_dialog_fragment, container)

    }

    private fun retrieveInstanceState(bundle: Bundle) {
        shippingSlot = bundle.getParcelableArrayList(ARG_SLOT)
    }

    private fun retrieveArguments(bundle: Bundle?) {
        bundle?.let { shippingSlot = bundle.getParcelableArrayList(ARG_SLOT) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
    }

    private fun setupView(rootView: View) {
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val recycler: RecyclerView = rootView.findViewById(R.id.time_slot_recycler)
        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recycler.adapter = TimeSlotAdapter(shippingSlot.sortedBy { it.getTimeDescription() }, timeSlotClickListener)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(ARG_SLOT, shippingSlot)
    }

    companion object {
        private var timeSlotClickListener: TimeSlotClickListener? = null
        private const val ARG_SLOT = "arg_shipping_slot"

        fun newInstance(slot: ArrayList<ShippingSlot>): TimeSlotDialogFragment {
            val fragment = TimeSlotDialogFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList(ARG_SLOT, slot)
            fragment.arguments = bundle
            return fragment
        }

        fun setOnPickDateListener(timeSlotClickListener: TimeSlotClickListener) {
            Companion.timeSlotClickListener = timeSlotClickListener
        }
    }

    class Builder {
        private var slot: ArrayList<ShippingSlot> = arrayListOf()

        fun setMessage(slot: ArrayList<ShippingSlot>): Builder {
            this.slot = slot
            return this
        }

        fun build(): TimeSlotDialogFragment {
            return newInstance(slot)
        }
    }
}