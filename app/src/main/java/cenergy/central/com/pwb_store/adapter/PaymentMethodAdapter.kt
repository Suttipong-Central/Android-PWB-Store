package cenergy.central.com.pwb_store.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.viewholder.*
import cenergy.central.com.pwb_store.dialogs.interfaces.PaymentTypeClickListener
import cenergy.central.com.pwb_store.model.response.PaymentMethod

class PaymentMethodAdapter(private var listener: PaymentTypeClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val E_ORDERING = 1
        const val PAY_AT_STORE = 2
        const val FULL_PAYMENT = 3
        const val INSTALLMENT = 4
        const val EMPTY = 5
    }

    var paymentMethods = listOf<PaymentMethod>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            E_ORDERING -> {
                PayHereViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_pay_at_store, parent, false))
            }
            PAY_AT_STORE -> {
                PayAtStoreViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_pay_at_store, parent, false))
            }
            FULL_PAYMENT -> {
                FullPaymentViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_pay_with_credite_card, parent, false))
            }
            INSTALLMENT -> {
                InstallmentViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_pay_with_credite_card, parent, false))
            }
            else -> {
                PaymentEmptyViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_empty, parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val paymentMethod = paymentMethods[position]
        when (holder){
            is PayHereViewHolder -> {
                holder.bindView(paymentMethod, listener)
            }
            is FullPaymentViewHolder -> {
                holder.bindView(paymentMethod, listener)
            }
            is InstallmentViewHolder -> {
                holder.bindView(paymentMethod, listener)
            }
            is PayAtStoreViewHolder -> {
                holder.bindView(paymentMethod, listener)
            }
            is CashOnDeliveryViewHolder -> {
                holder.bindView(paymentMethod, listener)
            }
        }
    }

    override fun getItemCount(): Int {
        return paymentMethods.size
    }

    override fun getItemViewType(position: Int): Int {
        return when(paymentMethods[position].code){
            PaymentMethod.PAY_AT_STORE -> {
                PAY_AT_STORE
            }
            PaymentMethod.FULL_PAYMENT -> {
                FULL_PAYMENT
            }
            PaymentMethod.INSTALLMENT -> {
                INSTALLMENT
            }
            PaymentMethod.E_ORDERING -> {
                E_ORDERING
            }
            else -> {
                EMPTY
            }
        }
    }
}