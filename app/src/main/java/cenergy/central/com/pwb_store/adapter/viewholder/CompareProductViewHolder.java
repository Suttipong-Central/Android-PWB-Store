package cenergy.central.com.pwb_store.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
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
import cenergy.central.com.pwb_store.model.ProductCompareList;
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

    @BindView(R.id.txt_product_name)
    PowerBuyTextView productName;

    @BindView(R.id.txt_product_description)
    PowerBuyTextView productDescription;

    @BindView(R.id.txt_product_old_price)
    PowerBuyTextView oldPrice;

    @BindView(R.id.txt_product_new_price)
    PowerBuyTextView newPrice;

    @BindView(R.id.layout_card)
    RelativeLayout cardLayout;

    public CompareProductViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setViewHolder(ProductCompareList productCompareList) {
        String unit = Contextor.getInstance().getContext().getString(R.string.baht);

        Glide.with(Contextor.getInstance().getContext())
                .load(Contextor.getInstance().getContext().getString(R.string.url_image) + productCompareList.getImageUrl())
                .placeholder(R.drawable.ic_pwb_logo_detail)
                .crossFade()
                .fitCenter()
                .into(imgProduct);

        productName.setText(productCompareList.getName());
        productDescription.setText(productCompareList.getDescription());
        double price = productCompareList.getOldPrice();
        double specialPrice = productCompareList.getNewPrice();
        if (price <= specialPrice) {
            oldPrice.setText(productCompareList.getDisplayOldPrice(unit));
            newPrice.setText("");
        } else {
            oldPrice.setText(productCompareList.getDisplayOldPrice(unit));
            oldPrice.setEnableStrikeThrough(true);
            newPrice.setText(productCompareList.getDisplayNewPrice(unit));
        }

        //itemView.setOnClickListener(this);
        imgCancel.setOnClickListener(this);
        imgCancel.setTag(productCompareList);
        cardLayout.setOnClickListener(this);
        //cardLayout.setTag();
    }

    @Override
    public void onClick(View v) {
        if (v == imgCancel) {
            ProductCompareList productCompareList = (ProductCompareList) imgCancel.getTag();
            EventBus.getDefault().post(new CompareDeleteBus(productCompareList, true));
        }
    }
}
