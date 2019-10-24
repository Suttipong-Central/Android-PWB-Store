package cenergy.central.com.pwb_store.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.MembersAdapter
import cenergy.central.com.pwb_store.activity.interfaces.PaymentProtocol
import cenergy.central.com.pwb_store.manager.listeners.MemberClickListener
import cenergy.central.com.pwb_store.model.EOrderingMember
import cenergy.central.com.pwb_store.model.response.MemberResponse

class PaymentMembersFragment : Fragment() {

    var listener: PaymentProtocol? = null
    var membersList: List<MemberResponse> = listOf()
    var EOrderingMembersList: List<EOrderingMember> = listOf()
    private lateinit var recycler: RecyclerView

    companion object {
        fun newInstance(): PaymentMembersFragment {
            val fragment = PaymentMembersFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as PaymentProtocol
        listener?.let {
            EOrderingMembersList = it.getPWBMembers()
            membersList = it.getMembers()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_payment_members, container, false)
        setupView(rootView)
        return rootView
    }

    private fun setupView(rootView: View) {
        recycler = rootView.findViewById(R.id.recycler_view_members)
        val membersAdapter = MembersAdapter()
        membersAdapter.setOnMemberClickListener(context as MemberClickListener)
        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recycler.adapter = membersAdapter
        if (EOrderingMembersList.isNotEmpty()) {
            membersAdapter.memberList = EOrderingMembersList
        } else {
            membersAdapter.memberList = membersList
        }
    }
}