package cenergy.central.com.pwb_store.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.viewholder.*
import cenergy.central.com.pwb_store.dialogs.interfaces.PaymentItemClickListener
import cenergy.central.com.pwb_store.model.PaymentMethodView

abstract class PaymentMethodViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bindView(item: T)
}

class PaymentMethodAdapter(private var listener: PaymentItemClickListener) :
        ListAdapter<PaymentMethodView, RecyclerView.ViewHolder>(PaymentMethodViewDiffUtil()) {

    class PaymentMethodViewDiffUtil : DiffUtil.ItemCallback<PaymentMethodView>() {
        override fun areItemsTheSame(oldItem: PaymentMethodView, newItem: PaymentMethodView): Boolean {
            return oldItem.viewType == newItem.viewType
        }

        override fun areContentsTheSame(oldItem: PaymentMethodView, newItem: PaymentMethodView): Boolean {
            return if (oldItem is PaymentMethodView.HeaderItemView && newItem is PaymentMethodView.HeaderItemView) {
                oldItem.title == newItem.title
            } else if (oldItem is PaymentMethodView.PaymentItemView && newItem is PaymentMethodView.PaymentItemView) {
                oldItem.paymentMethod.code == newItem.paymentMethod.code
                        && oldItem.paymentMethod.title == newItem.paymentMethod.title
                        && oldItem.selected == newItem.selected
            } else if (oldItem is PaymentMethodView.EmptyItemView && newItem is PaymentMethodView.EmptyItemView) {
                oldItem.viewType == newItem.viewType
            } else {
                false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TITLE_VIEW -> {
                PaymentHeaderViewHolder(inflater.inflate(R.layout.list_item_payment_header,
                        parent, false))
            }
            E_ORDERING -> {
                PayHereViewHolder(inflater.inflate(R.layout.list_item_pay_common,
                        parent, false), listener)
            }
            PAY_AT_STORE -> {
                PayAtStoreViewHolder(inflater.inflate(R.layout.list_item_pay_common,
                        parent, false), listener)
            }
            FULL_PAYMENT -> {
                FullPaymentViewHolder(inflater.inflate(R.layout.list_item_pay_by_credite_card,
                        parent, false), listener)
            }
            INSTALLMENT -> {
                InstallmentViewHolder(inflater.inflate(R.layout.list_item_pay_by_credite_card,
                        parent, false), listener)
            }
            BANK_AND_COUNTER_SERVICE -> {
                BankAndCounterServiceViewHolder(inflater.inflate(R.layout.list_item_pay_common,
                        parent, false), listener)
            }
            CASH_ON_DELIVERY -> {
                CashOnDeliveryViewHolder(inflater.inflate(R.layout.list_item_pay_common,
                        parent, false), listener)
            }
            OTHER -> {
                PaymentEmptyViewHolder(inflater.inflate(R.layout.list_item_pay_by_other,
                        parent, false), listener)
            }
            else -> {
                PaymentListEmpty(inflater.inflate(R.layout.item_empty, parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is PaymentHeaderViewHolder -> holder.bindView(item as PaymentMethodView.HeaderItemView)
            is PayHereViewHolder -> holder.bindView(item as PaymentMethodView.PaymentItemView)
            is PayAtStoreViewHolder -> holder.bindView(item as PaymentMethodView.PaymentItemView)
            is FullPaymentViewHolder -> holder.bindView(item as PaymentMethodView.PaymentItemView)
            is InstallmentViewHolder -> holder.bindView(item as PaymentMethodView.PaymentItemView)
            is BankAndCounterServiceViewHolder -> holder.bindView(item as PaymentMethodView.PaymentItemView)
            is CashOnDeliveryViewHolder -> holder.bindView(item as PaymentMethodView.PaymentItemView)
            is PaymentEmptyViewHolder -> holder.bindView(item as PaymentMethodView.PaymentItemView)
        }
    }

    override fun getItemViewType(position: Int): Int = getItem(position).viewType

    companion object {
        const val TITLE_VIEW = 0
        const val E_ORDERING = 1
        const val PAY_AT_STORE = 2
        const val FULL_PAYMENT = 3
        const val INSTALLMENT = 4
        const val BANK_AND_COUNTER_SERVICE = 5
        const val CASH_ON_DELIVERY = 6
        const val OTHER = 7
        const val EMPTY_VIEW = 8
    }

    inner class PaymentListEmpty(itemView: View) : RecyclerView.ViewHolder(itemView)
}