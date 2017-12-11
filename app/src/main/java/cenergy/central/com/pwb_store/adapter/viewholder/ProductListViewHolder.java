package cenergy.central.com.pwb_store.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.Contextor;
import cenergy.central.com.pwb_store.manager.bus.event.ProductDetailBus;
import cenergy.central.com.pwb_store.model.Extension;
import cenergy.central.com.pwb_store.model.ProductList;
import cenergy.central.com.pwb_store.model.ProductListStore;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 7/6/2017 AD.
 */

public class ProductListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private static final String TAG = ProductListViewHolder.class.getSimpleName();

    @BindView(R.id.img_product)
    ImageView mImageView;

    @BindView(R.id.txt_product_name)
    PowerBuyTextView productName;

    @BindView(R.id.txt_product_description)
    PowerBuyTextView productDescription;

    @BindView(R.id.txt_product_old_price)
    PowerBuyTextView oldPrice;

    @BindView(R.id.txt_product_new_price)
    PowerBuyTextView newPrice;

    @BindView(R.id.txt_product_brand)
    PowerBuyTextView productBrand;

    public ProductListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setViewHolder(ProductList productList){

        String unit = Contextor.getInstance().getContext().getString(R.string.baht);

        Extension extension = productList.getExtension();
        if (extension != null){

            for (ProductListStore productListStore : extension.getProductListStores()){
                Log.d(TAG, "price : " + productListStore.getPrice());
                Log.d(TAG, "specialPrice : " + productListStore.getSpecialPrice());
                oldPrice.setText("");
                oldPrice.setEnableStrikeThrough(false);
                newPrice.setText("");
                if (productListStore.getPrice().equals(productListStore.getSpecialPrice())){
                    Log.d(TAG, "In if");
                    oldPrice.setText(productListStore.getDisplayOldPrice(unit));
                    newPrice.setText("");
                }else {
                    Log.d(TAG, "In else");
                    oldPrice.setText(productListStore.getDisplayOldPrice(unit));
                    oldPrice.setEnableStrikeThrough(true);
                    newPrice.setText(productListStore.getDisplayNewPrice(unit));
                    Log.d(TAG, "new : " + productListStore.getDisplayNewPrice(unit));
                }
            }

            productDescription.setText(productList.getName());

            Glide.with(Contextor.getInstance().getContext())
                    .load(extension.getImageUrl())
                    //.load(Contextor.getInstance().getContext().getString(R.string.url_image)+extension.getImageUrl())
                    .placeholder(R.drawable.ic_pwb_logo_detail)
                    .crossFade()
                    .fitCenter()
                    .into(mImageView);

            productBrand.setText(extension.getBrand());
        }
        productName.setText(productList.getName());
        itemView.setOnClickListener(this);
        itemView.setTag(productList);
    }

    @Override
    public void onClick(View v) {
        ProductList productListList = (ProductList) itemView.getTag();
        EventBus.getDefault().post(new ProductDetailBus(productListList.getProductId(), v));
    }
}
