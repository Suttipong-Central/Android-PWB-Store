package cenergy.central.com.pwb_store.adapter.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import org.greenrobot.eventbus.EventBus;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.Contextor;
import cenergy.central.com.pwb_store.manager.bus.event.ProductDetailOptionItemBus;
import cenergy.central.com.pwb_store.model.ProductDetailOptionItem;

/**
 * Created by napabhat on 02/02/2017 AD.
 */

public class ProductDetailOptionItemViewHolder implements View.OnClickListener {
    private ViewGroup mLayoutOption;
    private ImageView mImageView;

    private int position;

    public ProductDetailOptionItemViewHolder(View view) {
        mLayoutOption = view.findViewById(R.id.layout_option);
        mImageView = view.findViewById(R.id.image_view);
    }

    public void setViewHolder(ProductDetailOptionItem productDetailOptionItem, int adapterPosition) {
        Glide.with(Contextor.getInstance().getContext())
                .load(productDetailOptionItem.getImgUrl())
                .placeholder(R.drawable.ic_circle_grey_placeholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(mImageView);
        mImageView.setTag(productDetailOptionItem);
        position = adapterPosition;
        mImageView.setOnClickListener(this);

        //mLayoutOption.setBackgroundColor(productDetailOptionItem.isSelected() ? Color.BLACK : Color.TRANSPARENT);
        mLayoutOption.setBackgroundDrawable(productDetailOptionItem.isSelected() ?
                Contextor.getInstance().getContext().getResources().getDrawable(R.drawable.ic_shape) :
                Contextor.getInstance().getContext().getResources().getDrawable(R.drawable.ic_circle_grey_placeholder));
    }

    @Override
    public void onClick(View view) {
        if (mImageView.getTag() instanceof ProductDetailOptionItem) {
            ProductDetailOptionItem productDetailOptionItem = (ProductDetailOptionItem) mImageView.getTag();
            EventBus.getDefault().post(new ProductDetailOptionItemBus(productDetailOptionItem, position));
        }
    }
}
