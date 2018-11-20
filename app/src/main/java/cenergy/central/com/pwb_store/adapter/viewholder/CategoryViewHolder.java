package cenergy.central.com.pwb_store.adapter.viewholder;

import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.Contextor;
import cenergy.central.com.pwb_store.manager.bus.event.ProductFilterHeaderBus;
import cenergy.central.com.pwb_store.manager.bus.event.ProductFilterSubHeaderBus;
import cenergy.central.com.pwb_store.model.ProductFilterHeader;
import cenergy.central.com.pwb_store.model.ProductFilterSubHeader;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 7/5/2017 AD.
 */

public class CategoryViewHolder extends RecyclerView.ViewHolder {

    private ImageView mImageView;
    private PowerBuyTextView mTextView;

    public CategoryViewHolder(View itemView) {
        super(itemView);
        mImageView = itemView.findViewById(R.id.image_view);
        mTextView = itemView.findViewById(R.id.txt_view);
    }

    public void setViewHolder(ProductFilterHeader categoryHeader) {
        if (!categoryHeader.getImageURL().isEmpty()) {
            Glide.with(Contextor.getInstance().getContext())
                    .load(categoryHeader.getImageURL())
                    .error(R.drawable.ic_category_placeholder)
                    .crossFade()
                    .fitCenter()
                    .into(mImageView);
        } else {
            mImageView.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_category_placeholder));
        }

        mTextView.setText(categoryHeader.getName());
        itemView.setTag(categoryHeader);
    }

    public void setViewHolder(ProductFilterSubHeader categorySubHeader) {
        if (!categorySubHeader.getUrlName().isEmpty()) {
            Glide.with(Contextor.getInstance().getContext())
                    .load(categorySubHeader.getUrlName())
                    .error(R.drawable.ic_category_placeholder)
                    .crossFade()
                    .fitCenter()
                    .into(mImageView);
        } else {
            mImageView.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_category_placeholder));
        }

        mTextView.setText(categorySubHeader.getName());
        itemView.setTag(categorySubHeader);
    }
}
