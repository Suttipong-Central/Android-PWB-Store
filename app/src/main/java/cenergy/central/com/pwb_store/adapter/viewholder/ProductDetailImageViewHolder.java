package cenergy.central.com.pwb_store.adapter.viewholder;

import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.pager.ProductDetailPagerAdapter;
import cenergy.central.com.pwb_store.manager.Contextor;
import cenergy.central.com.pwb_store.manager.bus.event.ProductViewImageBus;
import cenergy.central.com.pwb_store.model.ProductDetailImage;

/**
 * Created by napabhat on 7/18/2017 AD.
 */

public class ProductDetailImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ImageView mImageView;
    private ViewPager mViewPager;
    private ProductDetailPagerAdapter mAdapter;

    public ProductDetailImageViewHolder(View itemView) {
        super(itemView);
        mImageView = itemView.findViewById(R.id.image_view);
        mViewPager = itemView.findViewById(R.id.view_pager_image_detail);
    }

    public void setViewHolder(ProductDetailImage productDetailImage, FragmentManager fragmentManager) {

        if (productDetailImage.getProductDetailImageItems().size() > 0) {
            Glide.with(Contextor.getInstance().getContext())
                    .load(productDetailImage.getProductDetailImageItems().get(0).getImgUrl())
                    .placeholder(R.drawable.ic_pwb_logo_detail)
                    .crossFade()
                    .fitCenter()
                    .into(mImageView);
        } else {
            mImageView.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_pwb_logo_detail));
        }

//        for (ProductDetailImageItem productDetailImageItem : productDetailImage.getProductDetailImageItems()) {
//            if (productDetailImageItem.isSelected()) {
//                Glide.with(Contextor.getInstance().getContext())
//                        .load(productDetailImageItem.getImgUrl())
//                        .placeholder(R.drawable.ic_pwb_logo_detail)
//                        .crossFade()
//                        .fitCenter()
//                        .into(mImageView);
//
//            }
//        }

        if (mViewPager.getAdapter() == null) {
            mAdapter = new ProductDetailPagerAdapter(fragmentManager, productDetailImage);
            mViewPager.setAdapter(mAdapter);
        }

        itemView.setTag(productDetailImage);
        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        ProductDetailImage productDetailImage = (ProductDetailImage) itemView.getTag();
        EventBus.getDefault().post(new ProductViewImageBus(v, productDetailImage));
    }
}
