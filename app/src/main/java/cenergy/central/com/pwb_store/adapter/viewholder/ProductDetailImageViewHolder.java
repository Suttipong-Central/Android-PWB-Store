package cenergy.central.com.pwb_store.adapter.viewholder;

import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.pager.ProductDetailPagerAdapter;
import cenergy.central.com.pwb_store.manager.Contextor;
import cenergy.central.com.pwb_store.manager.bus.event.ProductViewImageBus;
import cenergy.central.com.pwb_store.model.ProductDetailImage;
import cenergy.central.com.pwb_store.model.ProductDetailImageItem;

/**
 * Created by napabhat on 7/18/2017 AD.
 */

public class ProductDetailImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.image_view)
    ImageView mImageView;

    @BindView(R.id.view_pager_image_detail)
    ViewPager mViewPager;

    private ProductDetailPagerAdapter mAdapter;

    public ProductDetailImageViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setViewHolder(ProductDetailImage productDetailImage, FragmentManager fragmentManager){

        Glide.with(Contextor.getInstance().getContext())
                .load("http://api.powerbuy.world/media/catalog/product"+productDetailImage.getProductDetailImageItems().get(0).getImgUrl())
                //.error(R.drawable.ic_error_placeholder)
                .placeholder(R.drawable.ic_pwb_logo_detail)
                .crossFade()
                .fitCenter()
                .into(mImageView);

        for (ProductDetailImageItem productDetailImageItem : productDetailImage.getProductDetailImageItems()) {
            if (productDetailImageItem.isSelected()) {
                Glide.with(Contextor.getInstance().getContext())
                        //.load("http://api.powerbuy.world/media/catalog/product"+productDetailImageItem.getImgUrl())
                        .load("http://api.powerbuy.world/media/catalog/product"+productDetailImageItem.getImgUrl())
                        //.error(R.drawable.ic_error_placeholder)
                        .placeholder(R.drawable.ic_pwb_logo_detail)
                        .crossFade()
                        .fitCenter()
                        .into(mImageView);

            }
        }
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
