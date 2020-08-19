package cenergy.central.com.pwb_store.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.extensions.setImage
import cenergy.central.com.pwb_store.extensions.setImageUrl
import cenergy.central.com.pwb_store.model.InstallmentPlan
import cenergy.central.com.pwb_store.model.InstallmentPlanView
import cenergy.central.com.pwb_store.model.response.CreditCardPromotion
import cenergy.central.com.pwb_store.view.PowerBuyTextView

class InstallmentPlanAdapter : RecyclerView.Adapter<InstallmentPlanViewHolder>() {
    var items = arrayListOf<InstallmentPlanView>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstallmentPlanViewHolder {
        return InstallmentPlanViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_installment_plan, parent, false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: InstallmentPlanViewHolder, position: Int) {
        holder.bind(items[position] as InstallmentPlanView.Installment)
    }
}

class InstallmentPlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    private var badgeIcon: ImageView = itemView.findViewById(R.id.badgeIcon)
    private var planTv: PowerBuyTextView = itemView.findViewById(R.id.planTv)

    @SuppressLint("Range")
    fun bind(installmentPlan: InstallmentPlanView.Installment){
        badgeIcon.setImage(installmentPlan.installments[0].getBankImageUrl())
        planTv.text = TextUtils.join(", ", installmentPlan.installments.map { it.name })
    }
}