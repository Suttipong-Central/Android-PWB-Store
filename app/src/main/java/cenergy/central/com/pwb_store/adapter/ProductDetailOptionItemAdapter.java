package cenergy.central.com.pwb_store.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import java.util.List;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.viewholder.ProductDetailOptionItemViewHolder;
import cenergy.central.com.pwb_store.model.ProductDetailOption;
import cenergy.central.com.pwb_store.model.ProductDetailOptionItem;

/**
 * Created by napabhat on 02/02/2017 AD.
 */

public class ProductDetailOptionItemAdapter extends BaseAdapter {
    private static final String TAG = "ProductDetailOptionSect";

    private static final int VIEW_TYPE_ID_COLOR = 41;
    private static final int VIEW_TYPE_ID_OTHER = 433;

    private List<ProductDetailOptionItem> productDetailOptionItemList;
    private boolean isColor;
    private int adapterPosition;

    public ProductDetailOptionItemAdapter() {
        productDetailOptionItemList = new ArrayList<>();
    }

    public ProductDetailOptionItemAdapter(ProductDetailOption productDetailOption, int adapterPosition) {
        this.productDetailOptionItemList = productDetailOption.getProductDetailOptionItemList();
        this.adapterPosition = adapterPosition;

        isColor = productDetailOption.getSlug().equalsIgnoreCase("color");
    }

    @Override
    public int getCount() {
        return productDetailOptionItemList.size();
    }

    @Override
    public ProductDetailOptionItem getItem(int position) {
        return productDetailOptionItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ProductDetailOptionItem productDetailOptionItem = getItem(position);

        if (view == null) {
            view = isColor ? LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_product_selector_color, parent, false)
                    : LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_product_detail_select_color, parent, false);
        } else {
            ProductDetailOptionItem tagged = (ProductDetailOptionItem) view.getTag();
            if (tagged != null && tagged.equals(productDetailOptionItem)) {
                return view;
            }
        }

        ProductDetailOptionItemViewHolder productDetailOptionItemViewHolder = new ProductDetailOptionItemViewHolder(view);
        productDetailOptionItemViewHolder.setViewHolder(productDetailOptionItem, adapterPosition);
        view.setTag(productDetailOptionItem);

        return view;
    }
}
