package cenergy.central.com.pwb_store.adapter

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.extensions.setImage
import cenergy.central.com.pwb_store.model.Installment
import cenergy.central.com.pwb_store.view.PowerBuyTextView

class InstallmentPlanAdapter : RecyclerView.Adapter<InstallmentPlanViewHolder>() {
    var items = arrayListOf<Installment>()
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
        holder.bind(items[position])
    }
}

class InstallmentPlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    private var badgeIcon: ImageView = itemView.findViewById(R.id.badgeIcon)
    private var planTv: PowerBuyTextView = itemView.findViewById(R.id.planTv)

    @SuppressLint("Range")
    fun bind(installment: Installment){
        badgeIcon.setImage(installment.installments[0].getBankImageUrl())
        planTv.text = getDisplayMonths(installment.installments.map { it.period })
    }

    private fun getDisplayMonths(months: List<Int>): String{
        var result = ""
        if (months.size > 1){
            for (i in months.indices){
                result += when (i) {
                    months.size -1 -> {
                        " ${months[i]} months"
                    }
                    0 -> {
                        "${months[i]} months,"
                    }
                    else -> {
                        " ${months[i]} months,"
                    }
                }
            }
        } else {
            result += "${months[0]} months"
        }
        return result
    }
}