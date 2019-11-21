package cenergy.central.com.pwb_store.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.interfaces.ShadeClickListener
import cenergy.central.com.pwb_store.adapter.viewholder.ShadeSelectViewHolder
import cenergy.central.com.pwb_store.model.ProductValue

class ShadeSelectAdapter(var shadeList: List<ProductValue>) : RecyclerView.Adapter<ShadeSelectViewHolder>() {

    private var selectedIndex = 0
    private var listener: ShadeClickListener? = null

    fun setCallBack(listener: ShadeClickListener){
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShadeSelectViewHolder {
        return ShadeSelectViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_shade_select, parent, false))
    }

    override fun getItemCount(): Int {
        return shadeList.size
    }

    override fun onBindViewHolder(holder: ShadeSelectViewHolder, position: Int) {
        val shade = shadeList[position]
        holder.bind(shade, selectedIndex)

        // onclick
        holder.itemView.setOnClickListener {
            selectedIndex = holder.adapterPosition
            notifyDataSetChanged()
            listener?.onShadeClickListener(shade)
        }
    }
}