package cenergy.central.com.pwb_store.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.Contextor;
import cenergy.central.com.pwb_store.manager.bus.event.CompareDeleteBus;
import cenergy.central.com.pwb_store.manager.bus.event.CompareDetailBus;
import cenergy.central.com.pwb_store.model.Brand;
import cenergy.central.com.pwb_store.model.CompareList;
import cenergy.central.com.pwb_store.model.CompareListStore;
import cenergy.central.com.pwb_store.model.CompareProduct;
import cenergy.central.com.pwb_store.model.ExtensionCompare;
import cenergy.central.com.pwb_store.model.ProductCompareList;
import cenergy.central.com.pwb_store.realm.RealmController;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 7/26/2017 AD.
 */

public class CompareProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ImageView imgCancel;
    private ImageView imgProduct;
    private PowerBuyTextView productBrand;
    private PowerBuyTextView productDescription;
    private PowerBuyTextView oldPrice;
    private PowerBuyTextView newPrice;
    private RelativeLayout cardLayout;

    private RealmController database = RealmController.getInstance();

    public CompareProductViewHolder(View itemView) {
        super(itemView);
        imgCancel = itemView.findViewById(R.id.img_cancel);
        imgProduct = itemView.findViewById(R.id.img_product);
        productBrand = itemView.findViewById(R.id.txt_product_brand);
        productDescription = itemView.findViewById(R.id.txt_product_description);
        oldPrice = itemView.findViewById(R.id.txt_product_old_price);
        newPrice = itemView.findViewById(R.id.txt_product_new_price);
        cardLayout = itemView.findViewById(R.id.layout_card);
    }

    public void setViewHolder(CompareList compareList, ProductCompareList productCompareList) {
        String unit = Contextor.getInstance().getContext().getString(R.string.baht);
        ExtensionCompare extensionCompare = productCompareList.getExtensionCompare();
        if (extensionCompare != null){
            Glide.with(Contextor.getInstance().getContext())
                    .load(extensionCompare.getImageUrl())
                    //.load(Contextor.getInstance().getContext().getString(R.string.url_image) + extensionCompare.getImageUrl())
                    .placeholder(R.drawable.ic_placeholder)
                    .crossFade()
                    .fitCenter()
                    .into(imgProduct);

            productDescription.setText(productCompareList.getName());
            for (CompareListStore compareListStore : extensionCompare.getCompareListStores()){
                double price = Double.parseDouble(compareListStore.getPrice());
                double specialPrice = Double.parseDouble(compareListStore.getSpecialPrice());
                if (price <= specialPrice) {
                    oldPrice.setText(compareListStore.getDisplayOldPrice(unit));
                    newPrice.setText("");
                } else {
                    oldPrice.setText(compareListStore.getDisplayOldPrice(unit));
                    oldPrice.setEnableStrikeThrough(true);
                    newPrice.setText(compareListStore.getDisplayNewPrice(unit));
                }
            }
            productBrand.setText(extensionCompare.getBrand());
        }

        //itemView.setOnClickListener(this);
        imgCancel.setOnClickListener(this);
        imgCancel.setTag(productCompareList);
        cardLayout.setOnClickListener(this);
        cardLayout.setTag(productCompareList);
    }


    public void bindItem(final CompareProduct compareProduct) {
        String unit = itemView.getContext().getString(R.string.baht);
        oldPrice.setText(compareProduct.normalPrice(unit));
       Brand brand =  database.getBrand(Long.parseLong(compareProduct.getBrand()));
        productBrand.setText((brand == null)? compareProduct.getBrand() : brand.getName());
        productDescription.setText(compareProduct.getName());

        Glide.with(Contextor.getInstance().getContext())
                .load(compareProduct.getImageUrl())
                .placeholder(R.drawable.ic_placeholder)
                .crossFade()
                .fitCenter()
                .into(imgProduct);

        // setup OnClick
        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new CompareDeleteBus(compareProduct, true));
            }
        });

        cardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new CompareDetailBus(compareProduct, true, v));
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == imgCancel) {
            ProductCompareList productCompareList = (ProductCompareList) imgCancel.getTag();
            EventBus.getDefault().post(new CompareDeleteBus(productCompareList, true));
        }else if (v == cardLayout){
            ProductCompareList productCompareList = (ProductCompareList) cardLayout.getTag();
            EventBus.getDefault().post(new CompareDetailBus(productCompareList, true, v));
        }
    }
}
