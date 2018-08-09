package cenergy.central.com.pwb_store.adapter.viewholder;

import android.annotation.SuppressLint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.ProductDetailOptionItemAdapter;
import cenergy.central.com.pwb_store.manager.Contextor;
import cenergy.central.com.pwb_store.manager.bus.event.ProductBus;
import cenergy.central.com.pwb_store.manager.bus.event.StoreAvaliableBus;
import cenergy.central.com.pwb_store.model.ExtensionProductDetail;
import cenergy.central.com.pwb_store.model.ProductDetail;
import cenergy.central.com.pwb_store.model.Product;
import cenergy.central.com.pwb_store.model.ProductDetailOptionItem;
import cenergy.central.com.pwb_store.model.ProductDetailStore;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;
import cenergy.central.com.pwb_store.view.PowerBuyWrapAbleGridView;

/**
 * Created by napabhat on 7/18/2017 AD.
 */

@SuppressLint("SetTextI18n")
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

    @BindView(R.id.txt_name_price)
    PowerBuyTextView namePrice;

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
//        ButterKnife.bind(this, itemView);
        namePrice = itemView.findViewById(R.id.txt_name_price);
        mProductName = itemView.findViewById(R.id.txt_view_product_name);
        mProductCode = itemView.findViewById(R.id.txt_view_product_code);
        mSalePrice = itemView.findViewById(R.id.txt_sale_price);
        mStock = itemView.findViewById(R.id.txt_stock);
        mRegular = itemView.findViewById(R.id.txt_regular);
    }

    public void setViewHolder(ProductDetail productDetail){
        String unit = Contextor.getInstance().getContext().getString(R.string.baht);

        mProductName.setText(productDetail.getProductName());
        mProductCode.setText(Contextor.getInstance().getContext().getResources().getString(R.string.product_code)+ productDetail.getSku());
        ExtensionProductDetail extensionProductDetail = productDetail.getExtensionProductDetail();
        if (extensionProductDetail != null){
            for (ProductDetailStore productDetailStore : extensionProductDetail.getProductDetailStores()){
                float oldPrice = Float.parseFloat(productDetailStore.getPrice());
                float newPrice = Float.parseFloat(productDetailStore.getSpecialPrice());
                if (oldPrice > newPrice){
                    mSalePrice.setText(productDetailStore.getDisplayNewPrice(unit));
                }else {
                    mSalePrice.setTextColor(ContextCompat.getColor(Contextor.getInstance().getContext(),R.color.powerBuyPurple));
                    namePrice.setTextColor(ContextCompat.getColor(Contextor.getInstance().getContext(),R.color.headerTextColor));
                    mSalePrice.setText(productDetailStore.getDisplayNewPrice(unit));
                }

                mRegular.setText("Regular Price : " +productDetailStore.getDisplayOldPrice(unit));

                if (Integer.parseInt(productDetailStore.getStockAvailable()) > 0){
                    mStock.setText(Contextor.getInstance().getContext().getResources().getString(R.string.product_stock));
                }else {
                    mStock.setText(Contextor.getInstance().getContext().getResources().getString(R.string.product_out_stock));
                    mStock.setTextColor(Contextor.getInstance().getContext().getResources().getColor(R.color.salePriceColor));
                }
            }
        }

//        if (productDetail.getStockAvailable() > 0){
//            mStock.setText(Contextor.getInstance().getContext().getResources().getString(R.string.product_stock));
//        }else {
//            mStock.setText(Contextor.getInstance().getContext().getResources().getString(R.string.product_out_stock));
//            mStock.setTextColor(Contextor.getInstance().getContext().getResources().getColor(R.color.salePriceColor));
//        }

//        Glide.with(Contextor.getInstance().getContext())
//                .load(R.drawable.newproduct)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .fitCenter()
//                .into(imgFirst);
//
//        Glide.with(Contextor.getInstance().getContext())
//                .load(R.drawable.bestseller)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .fitCenter()
//                .into(imgSecond);
//
//        Glide.with(Contextor.getInstance().getContext())
//                .load(R.drawable.relax)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .fitCenter()
//                .into(imgThird);

//        if (productDetail.getPrice() < productDetail.getOriginalPrice()){
//            mSalePrice.setText(productDetail.getDisplayNewPrice(unit));
//        }else {
//            mSalePrice.setTextColor(ContextCompat.getColor(Contextor.getInstance().getContext(),R.color.powerBuyPurple));
//            namePrice.setTextColor(ContextCompat.getColor(Contextor.getInstance().getContext(),R.color.headerTextColor));
//            mSalePrice.setText(productDetail.getDisplayNewPrice(unit));
//        }

        String redeem = String.format(Contextor.getInstance().getContext().getResources()
                .getString(R.string.the_1_card), productDetail.getT1cPoint());

        mRedeem.setText(redeem);

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
        mCardViewCompare.setTag(productDetail);
        mCardViewCompare.setOnClickListener(this);
    }

    public void setViewHolder(Product product){
        String unit = Contextor.getInstance().getContext().getString(R.string.baht);

        mProductName.setText(product.getName());
        mProductCode.setText(Contextor.getInstance().getContext().getResources().getString(R.string.product_code)+ product.getSku());
//        namePrice.setTextColor(ContextCompat.getColor(Contextor.getInstance().getContext(),R.color.headerTextColor));
        mRegular.setText("Regular Price : " + product.getDisplayOldPrice(unit));

//        ExtensionProductDetail extensionProductDetail = productDetailNew.getExtensionProductDetail();
//        if (extensionProductDetail != null){
//            for (ProductDetailStore productDetailStore : extensionProductDetail.getProductDetailStores()){
//                float oldPrice = Float.parseFloat(productDetailStore.getPrice());
//                float newPrice = Float.parseFloat(productDetailStore.getSpecialPrice());
//                if (oldPrice > newPrice){
//                    mSalePrice.setText(productDetailStore.getDisplayNewPrice(unit));
//                }else {
//                    mSalePrice.setTextColor(ContextCompat.getColor(Contextor.getInstance().getContext(),R.color.powerBuyPurple));
//                    namePrice.setTextColor(ContextCompat.getColor(Contextor.getInstance().getContext(),R.color.headerTextColor));
//                    mSalePrice.setText(productDetailStore.getDisplayNewPrice(unit));
//                }
//
//                mRegular.setText("Regular Price : " +productDetailStore.getDisplayOldPrice(unit));
//
//                if (Integer.parseInt(productDetailStore.getStockAvailable()) > 0){
//                    mStock.setText(Contextor.getInstance().getContext().getResources().getString(R.string.product_stock));
//                }else {
//                    mStock.setText(Contextor.getInstance().getContext().getResources().getString(R.string.product_out_stock));
//                    mStock.setTextColor(Contextor.getInstance().getContext().getResources().getColor(R.color.salePriceColor));
//                }
//            }
//        }
    }

    @Override
    public void onClick(View view) {
        if (view == mCardViewStore){
            ProductDetail productDetail = (ProductDetail) mCardViewStore.getTag();
            EventBus.getDefault().post(new StoreAvaliableBus(view, productDetail.getSku()));
        }else if (view == mCardViewCompare){
            ProductDetail productDetail = (ProductDetail) mCardViewCompare.getTag();
            EventBus.getDefault().post(new ProductBus(productDetail));
        }
    }
}
