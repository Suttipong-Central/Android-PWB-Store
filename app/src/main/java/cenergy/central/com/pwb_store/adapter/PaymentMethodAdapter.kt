package cenergy.central.com.pwb_store.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.viewholder.*
import cenergy.central.com.pwb_store.dialogs.interfaces.PaymentTypeClickListener
import cenergy.central.com.pwb_store.model.response.PaymentMethod

interface PaymentMethodItem

class EmptyList : PaymentMethodItem

class PaymentMethodAdapter(private var listener: PaymentTypeClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val E_ORDERING = 1
        const val PAY_AT_STORE = 2
        const val FULL_PAYMENT = 3
        const val INSTALLMENT = 4
        const val OTHER = 5
        const val EMPTY_VIEW = 6
    }

    var paymentMethodItems = listOf<PaymentMethodItem>()
        set(value) {
            field = if (value.isEmpty()) {
                val newList = arrayListOf<PaymentMethodItem>(EmptyList())
                newList
            } else {
                value
            }
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
            OTHER -> {
                PaymentEmptyViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_other, parent, false))
            }
            else -> {
                PaymentListEmpty(LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_empty, parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = paymentMethodItems[position]
        if (item is PaymentMethod) {
            when (holder) {
                is PayHereViewHolder -> {
                    holder.bindView(item, listener)
                }
                is FullPaymentViewHolder -> {
                    holder.bindView(item, listener)
                }
                is InstallmentViewHolder -> {
                    holder.bindView(item, listener)
                }
                is PayAtStoreViewHolder -> {
                    holder.bindView(item, listener)
                }
                is CashOnDeliveryViewHolder -> {
                    holder.bindView(item, listener)
                }
                is PaymentEmptyViewHolder -> {
                    holder.bindView(item, listener)
                }
            }
        }

        if (item is EmptyList)  {

        }
    }

    override fun getItemCount(): Int {
        return paymentMethodItems.size
    }

    override fun getItemViewType(position: Int): Int {
        val item = paymentMethodItems[position]
        return if(item is PaymentMethod) {
             when (item.code) {
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
                    OTHER
                }
            }
        } else {
            EMPTY_VIEW
        }
    }

    inner class PaymentListEmpty(itemView: View) : RecyclerView.ViewHolder(itemView)
}