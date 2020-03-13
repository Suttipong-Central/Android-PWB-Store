package cenergy.central.com.pwb_store.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 7/7/2017 AD.
 */

public class ProductHeaderViewHolder extends RecyclerView.ViewHolder {
//    @BindView(R.id.txt_title_product)
    PowerBuyTextView titleProduct;

//    @BindView(R.id.layout_title)
//    LinearLayout mLayoutTitle;
//
//    @BindView(R.id.layout_product)
//    LinearLayout mLinearLayout;
//
//    @BindView(R.id.img_icon)
//    ImageView mImageView;

    public ProductHeaderViewHolder(View itemView) {
        super(itemView);
        titleProduct = itemView.findViewById(R.id.txt_title_product);
    }

    public void setViewHolder(String title){
        titleProduct.setText(title);
    }

//    @Override
//    public void onClick(View v) {
//        EventBus.getDefault().post(new ProductBackBus(true));
//    }
}
