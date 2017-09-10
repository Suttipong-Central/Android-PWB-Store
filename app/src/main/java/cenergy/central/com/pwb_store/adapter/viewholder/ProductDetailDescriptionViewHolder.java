package cenergy.central.com.pwb_store.adapter.viewholder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.ProductDetailOptionItemAdapter;
import cenergy.central.com.pwb_store.manager.Contextor;
import cenergy.central.com.pwb_store.manager.bus.event.StoreAvaliableBus;
import cenergy.central.com.pwb_store.model.ProductDetail;
import cenergy.central.com.pwb_store.model.ProductDetailOptionItem;
import cenergy.central.com.pwb_store.model.ProductList;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;
import cenergy.central.com.pwb_store.view.PowerBuyWrapAbleGridView;

/**
 * Created by napabhat on 7/18/2017 AD.
 */

public class ProductDetailDescriptionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    @BindView(R.id.txt_view_product_name)
    PowerBuyTextView mProductName;

    @BindView(R.id.txt_view_product_code)
    PowerBuyTextView mProductCode;

    @BindView(R.id.txt_stock)
    PowerBuyTextView mStock;

    @BindView(R.id.img_pro_first)
    ImageView imgFirst;

    @BindView(R.id.img_pro_second)
    ImageView imgSecond;

    @BindView(R.id.img_pro_third)
    ImageView imgThird;

    @BindView(R.id.txt_sale_price)
    PowerBuyTextView mSalePrice;

    @BindView(R.id.txt_regular)
    PowerBuyTextView mRegular;

    @BindView(R.id.txt_save)
    PowerBuyTextView mSave;

    @BindView(R.id.txt_redeem)
    PowerBuyTextView mRedeem;

    @BindView(R.id.txt_color)
    PowerBuyTextView mTextColor;

    @BindView(R.id.grid_view)
    PowerBuyWrapAbleGridView mGridViewSize;

    @BindView(R.id.card_view_store)
    CardView mCardViewStore;

    @BindView(R.id.card_view_add_compare)
    CardView mCardViewCompare;

    private ProductDetailOptionItemAdapter mAdapter;

    public ProductDetailDescriptionViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setViewHolder(ProductDetail productDetail){
        mProductName.setText(productDetail.getProductName());
        mProductCode.setText(productDetail.getProductCode());
        if (productDetail.getStockAvailable() > 0){
            mStock.setText(Contextor.getInstance().getContext().getResources().getString(R.string.product_stock));
        }else {
            mStock.setText(Contextor.getInstance().getContext().getResources().getString(R.string.product_out_stock));
            mStock.setTextColor(Contextor.getInstance().getContext().getResources().getColor(R.color.salePriceColor));
        }

        Glide.with(Contextor.getInstance().getContext())
                .load(R.drawable.newproduct)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .into(imgFirst);

        Glide.with(Contextor.getInstance().getContext())
                .load(R.drawable.bestseller)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .into(imgSecond);

        Glide.with(Contextor.getInstance().getContext())
                .load(R.drawable.relax)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .into(imgThird);


        if (productDetail.getProductDetailOption() != null) {
            mAdapter = new ProductDetailOptionItemAdapter(productDetail.getProductDetailOption(), getAdapterPosition());
            if (productDetail.getProductDetailOption().getSlug().equalsIgnoreCase("color")) {
                mGridViewSize.setNumColumns(9);
                for (ProductDetailOptionItem productDetailOptionItem : productDetail.getProductDetailOption().getProductDetailOptionItemList()) {
//                    if (productDetailOptionItem.getProductDetailAvailableOption() != null) {
//                        mAdapterSection = new ProductDetailOptionSectionItemAdapter(productDetailOptionItem.getProductDetailAvailableOption(), 0, getAdapterPosition());
//                        if (productDetailOptionItem.isSelected()) {
//                            if (productDetailOptionItem.getProductDetailAvailableOption().getSlug().equalsIgnoreCase("size")) {
//                                mGridViewSize.setNumColumns(7);
//                            } else {
//                                mGridViewSize.setNumColumns(5);
//                            }
//                            mGridViewSize.setAdapter(mAdapterSection);
//                        }
//                    } else {
//                        mGridViewSize.setVisibility(View.INVISIBLE);
//                    }
                }

            } else {
                mGridViewSize.setNumColumns(5);
            }

            mGridViewSize.setAdapter(mAdapter);
        } else {
            mTextColor.setVisibility(View.INVISIBLE);
        }
        mCardViewStore.setTag(productDetail);
        mCardViewStore.setOnClickListener(this);
        mCardViewCompare.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mCardViewStore){
            ProductDetail productDetail = (ProductDetail) mCardViewStore.getTag();
            EventBus.getDefault().post(new StoreAvaliableBus(view, productDetail.getProductCode()));
        }
    }
}
