package cenergy.central.com.pwb_store.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.viewholder.*
import cenergy.central.com.pwb_store.dialogs.interfaces.PaymentItemClickListener
import cenergy.central.com.pwb_store.model.PaymentMethod

interface PaymentMethodItem

class PaymentMethodAdapter(private var listener: PaymentItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    abstract class PaymentMethodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bindView(paymentMethod: PaymentMethod, listener: PaymentItemClickListener)
    }

    inner class EmptyList : PaymentMethodItem
    inner class PaymentListEmpty(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        const val E_ORDERING = 1
        const val PAY_AT_STORE = 2
        const val FULL_PAYMENT = 3
        const val INSTALLMENT = 4
        const val BANK_AND_COUNTER_SERVICE = 5
        const val CASH_ON_DELIVERY = 6
        const val OTHER = 7
        const val EMPTY_VIEW = 8
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
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            E_ORDERING -> {
                PayHereViewHolder(layoutInflater.inflate(R.layout.list_item_pay_common,
                        parent, false))
            }
            PAY_AT_STORE -> {
                PayAtStoreViewHolder(layoutInflater.inflate(R.layout.list_item_pay_common,
                        parent, false))
            }
            FULL_PAYMENT -> {
                FullPaymentViewHolder(layoutInflater.inflate(R.layout.list_item_pay_by_credite_card,
                        parent, false))
            }
            INSTALLMENT -> {
                InstallmentViewHolder(layoutInflater.inflate(R.layout.list_item_pay_by_credite_card,
                        parent, false))
            }
            BANK_AND_COUNTER_SERVICE -> {
                BankAndCounterServiceViewHolder(layoutInflater.inflate(R.layout.list_item_pay_common,
                        parent, false))
            }
            CASH_ON_DELIVERY -> {
                CashOnDeliveryViewHolder(layoutInflater.inflate(R.layout.list_item_pay_common,
                        parent, false))
            }
            OTHER -> {
                PaymentEmptyViewHolder(layoutInflater.inflate(R.layout.list_item_pay_by_other,
                        parent, false))
            }
            else -> {
                PaymentListEmpty(layoutInflater.inflate(R.layout.item_empty, parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = paymentMethodItems[position]
        if (item is PaymentMethod && holder is PaymentMethodViewHolder) {
            holder.bindView(item, listener)
        }
    }

    override fun getItemCount(): Int {
        return paymentMethodItems.size
    }

    override fun getItemViewType(position: Int): Int {
        val item = paymentMethodItems[position]
        return if (item is PaymentMethod) {
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
                PaymentMethod.BANK_AND_COUNTER_SERVICE -> {
                    BANK_AND_COUNTER_SERVICE
                }
                PaymentMethod.CASH_ON_DELIVERY -> {
                    CASH_ON_DELIVERY
                }
                else -> {
                    OTHER
                }
            }
        } else {
            EMPTY_VIEW
        }
    }
}