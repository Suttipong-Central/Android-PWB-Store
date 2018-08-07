package cenergy.central.com.pwb_store.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.Contextor;
import cenergy.central.com.pwb_store.manager.bus.event.ProductFilterHeaderBus;
import cenergy.central.com.pwb_store.model.ProductFilterHeader;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by napabhat on 7/5/2017 AD.
 */

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ImageView mImageView = itemView.findViewById(R.id.image_view);
    private PowerBuyTextView mTextView = itemView.findViewById(R.id.txt_view);

    public CategoryViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setViewHolder(ProductFilterHeader categoryHeader) {
        Glide.with(Contextor.getInstance().getContext())
                //.load(category.getImageURL())
                .load(categoryHeader.getUrlName())
                .placeholder(R.drawable.ic_circle_grey_placeholder)
                .crossFade()
                //.fitCenter()
                .bitmapTransform(new CropCircleTransformation(Contextor.getInstance().getContext()))
                .into(mImageView);

        mTextView.setText(categoryHeader.getName());
        itemView.setTag(categoryHeader);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        ProductFilterHeader categoryHeader = (ProductFilterHeader) itemView.getTag();
        EventBus.getDefault().post(new ProductFilterHeaderBus(categoryHeader, getAdapterPosition()));
    }
}
