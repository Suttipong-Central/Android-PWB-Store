package cenergy.central.com.pwb_store.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    private static final String TAG = CompareProductViewHolder.class.getSimpleName();

    @BindView(R.id.img_cancel)
    ImageView imgCancel;

    @BindView(R.id.img_product)
    ImageView imgProduct;

    @BindView(R.id.txt_product_brand)
    PowerBuyTextView productBrand;

    @BindView(R.id.txt_product_description)
    PowerBuyTextView productDescription;

    @BindView(R.id.txt_product_old_price)
    PowerBuyTextView oldPrice;

    @BindView(R.id.txt_product_new_price)
    PowerBuyTextView newPrice;

    @BindView(R.id.layout_card)
    RelativeLayout cardLayout;

    private CompareList mCompareList;
    private RealmController database = RealmController.getInstance();

    public CompareProductViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setViewHolder(CompareList compareList, ProductCompareList productCompareList) {
        this.mCompareList = compareList;
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
