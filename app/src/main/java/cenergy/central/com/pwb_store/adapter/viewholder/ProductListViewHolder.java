package cenergy.central.com.pwb_store.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.Contextor;
import cenergy.central.com.pwb_store.model.Product;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 7/6/2017 AD.
 */

public class ProductListViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = ProductListViewHolder.class.getSimpleName();

    private ImageView mImageView;
    private PowerBuyTextView productName;
    private PowerBuyTextView productDescription;
    private PowerBuyTextView oldPrice;
    private PowerBuyTextView newPrice;
    private PowerBuyTextView productBrand;

    public ProductListViewHolder(View itemView) {
        super(itemView);
        mImageView = itemView.findViewById(R.id.img_product);
        productName = itemView.findViewById(R.id.txt_product_name);
        productDescription = itemView.findViewById(R.id.txt_product_description);
        oldPrice = itemView.findViewById(R.id.txt_product_old_price);
        newPrice = itemView.findViewById(R.id.txt_product_new_price);
        productBrand = itemView.findViewById(R.id.txt_product_brand);
    }

    public void setViewHolder(Product product) {

        String unit = Contextor.getInstance().getContext().getString(R.string.baht);

        Glide.with(Contextor.getInstance().getContext())
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_pwb_logo_detail)
                .crossFade()
                .fitCenter()
                .into(mImageView);

        oldPrice.setText(product.getDisplayOldPrice(unit));
        newPrice.setText(product.getDisplaySpecialPrice(unit));

        if (product.isSpecialPrice()) {
            showSpecialPrice();
        } else {
            hideSpecialPrice();
        }
        String brand = product.getBrand();
        productBrand.setText(!brand.equals("") ? brand : "Brand");
        productName.setText(product.getName());
        itemView.setTag(product);
    }

    private void showSpecialPrice() {
        newPrice.setVisibility(View.VISIBLE);
        oldPrice.setEnableStrikeThrough(true);
    }

    private void hideSpecialPrice() {
        newPrice.setVisibility(View.GONE);
        oldPrice.setEnableStrikeThrough(false);
    }
}
