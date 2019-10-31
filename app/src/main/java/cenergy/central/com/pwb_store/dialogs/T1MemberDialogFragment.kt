package cenergy.central.com.pwb_store.dialogs

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.PaymentProtocol
import cenergy.central.com.pwb_store.adapter.MembersAdapter
import cenergy.central.com.pwb_store.dialogs.interfaces.PaymentT1Listener
import cenergy.central.com.pwb_store.manager.listeners.MemberClickListener
import cenergy.central.com.pwb_store.model.response.MemberResponse
import kotlinx.android.synthetic.main.t1_member_dialog.view.*

class T1MemberDialogFragment : DialogFragment(), MemberClickListener {

    private var listener: PaymentT1Listener? = null
    private var paymentProtocol: PaymentProtocol? = null

    var membersList: List<MemberResponse> = listOf()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as PaymentT1Listener
        paymentProtocol = context as PaymentProtocol
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.t1_member_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
    }

    private fun setupView(rootView: View) {
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.window?.attributes?.gravity = Gravity.CENTER

        membersList = paymentProtocol?.getMembers() ?: arrayListOf()
        val recycler = rootView.recycler_view_members
        val membersAdapter = MembersAdapter(showDetail = true)
        membersAdapter.setOnMemberClickListener(this)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = membersAdapter
        membersAdapter.memberList = membersList
    }

    override fun onClickedHDLMember(position: Int) {
        // do nothing
    }
    override fun onClickedPwbMember(pwbMemberIndex: Int) {
        // nothing
    }

    override fun onClickedT1CMember(member: MemberResponse) {
        listener?.onSelectedT1Member(member)
        dialog.dismiss()
    }
    // endregion

    companion object {
        const val TAG_FRAGMENT = "t1_member_dialog"
        fun newInstance(): T1MemberDialogFragment {
            val fragment = T1MemberDialogFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    class Builder {
        fun build(): T1MemberDialogFragment {
            return newInstance()
        }
    }
}
