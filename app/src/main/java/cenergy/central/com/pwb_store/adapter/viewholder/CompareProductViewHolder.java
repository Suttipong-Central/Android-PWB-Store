package cenergy.central.com.pwb_store.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.Contextor;
import cenergy.central.com.pwb_store.manager.bus.event.CompareDeleteBus;
import cenergy.central.com.pwb_store.model.ProductList;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 7/26/2017 AD.
 */

public class CompareProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

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

    public CompareProductViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setViewHolder(ProductList productList){
        String unit = Contextor.getInstance().getContext().getString(R.string.baht);

        Glide.with(Contextor.getInstance().getContext())
                .load(productList.getImageUrl())
                //.placeholder(R.drawable.ic_error_placeholder)
                .crossFade()
                .fitCenter()
                .into(imgProduct);

        productName.setText(productList.getName());
        productDescription.setText(productList.getDescription());
        oldPrice.setText(productList.getDisplayOldPrice(unit));
        oldPrice.setEnableStrikeThrough(true);

        newPrice.setText(productList.getDisplayNewPrice(unit));
        itemView.setOnClickListener(this);
        imgCancel.setOnClickListener(this);
        itemView.setTag(productList);
    }

    @Override
    public void onClick(View v) {
        if (v == imgCancel){
            ProductList productList = (ProductList) itemView.getTag();
            EventBus.getDefault().post(new CompareDeleteBus(productList, true));
        }
    }
}
