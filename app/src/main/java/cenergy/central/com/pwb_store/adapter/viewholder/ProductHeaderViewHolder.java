package cenergy.central.com.pwb_store.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.bus.event.ProductBackBus;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 7/7/2017 AD.
 */

public class ProductHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    @BindView(R.id.txt_title_product)
    PowerBuyTextView titleProduct;

    @BindView(R.id.layout_title)
    LinearLayout mLayoutTitle;

    @BindView(R.id.layout_product)
    LinearLayout mLinearLayout;

    @BindView(R.id.img_icon)
    ImageView mImageView;

    public ProductHeaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        mLayoutTitle.setOnClickListener(this);
    }

    public void setViewHolder(String title){
        titleProduct.setText(title);
    }

    @Override
    public void onClick(View v) {
        EventBus.getDefault().post(new ProductBackBus(true));
    }
}
