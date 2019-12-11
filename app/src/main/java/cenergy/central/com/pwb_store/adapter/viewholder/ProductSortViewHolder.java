package cenergy.central.com.pwb_store.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.model.ProductSortList;

/**
 * Created by napabhat on 7/7/2017 AD.
 */

public class ProductSortViewHolder extends RecyclerView.ViewHolder {

    public ProductSortViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setViewHolder(ProductSortList productSortList){

    }
}
