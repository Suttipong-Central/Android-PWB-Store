package cenergy.central.com.pwb_store.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.viewholder.ProductFilterHeaderViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.ProductFilterItemViewHolder;
import cenergy.central.com.pwb_store.model.IViewType;
import cenergy.central.com.pwb_store.model.ProductFilterHeader;
import cenergy.central.com.pwb_store.model.ProductFilterItem;
import cenergy.central.com.pwb_store.model.ProductFilterList;
import cenergy.central.com.pwb_store.model.ProductFilterSubHeader;

/**
 * Created by napabhat on 7/13/2017 AD.
 */

public class ProductFilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "ProductFilterAdapter";

    //Static Members
    private static final int VIEW_TYPE_ID_FILTER_HEADER = 0;
    private static final int VIEW_TYPE_ID_FILTER_ITEM = 1;

    //Data Members
    private Context mContext;
    private List<IViewType> mListViewType = new ArrayList<>();
    private List<ProductFilterSubHeader> mProductFilterSubHeaders = new ArrayList<>();

    public ProductFilterAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ID_FILTER_HEADER:
                return new ProductFilterHeaderViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_product_filter_header, parent, false)
                );
            case VIEW_TYPE_ID_FILTER_ITEM:
                return new ProductFilterItemViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_product_filter_item, parent, false)
                );

        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewTypeId = getItemViewType(position);
        IViewType viewType = mListViewType.get(position);
        switch (viewTypeId) {
            case VIEW_TYPE_ID_FILTER_HEADER:
                if (viewType instanceof ProductFilterSubHeader && holder instanceof ProductFilterHeaderViewHolder) {
                    ProductFilterSubHeader productFilterSubHeader = (ProductFilterSubHeader) viewType;
                    ProductFilterHeaderViewHolder productFilterHeaderViewHolder = (ProductFilterHeaderViewHolder) holder;
                    productFilterHeaderViewHolder.setViewHolder(productFilterSubHeader);
                }
                break;
            case VIEW_TYPE_ID_FILTER_ITEM:
                if (viewType instanceof ProductFilterItem && holder instanceof ProductFilterItemViewHolder) {
                    ProductFilterItem productFilterItem = (ProductFilterItem) viewType;
                    ProductFilterItemViewHolder productFilterItemViewHolder = (ProductFilterItemViewHolder) holder;
                    productFilterItemViewHolder.setViewHolder(productFilterItem);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mListViewType.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mListViewType.get(position).getViewTypeId();
    }


    public void setProductFilter(ProductFilterList productFilterList) {
        this.mProductFilterSubHeaders = productFilterList.getProductFilterSubHeaders();
        mListViewType.clear();
        for (ProductFilterSubHeader productFilterHeader :
                productFilterList.getProductFilterSubHeaders()) {
                productFilterHeader.setViewTypeId(VIEW_TYPE_ID_FILTER_HEADER);
                mListViewType.add(productFilterHeader);

        }

        notifyDataSetChanged();
    }

    public void addProductLevel2(ProductFilterSubHeader productFilterSubHeader) {
        List<ProductFilterItem> productFilterItemList = productFilterSubHeader.getProductFilterItemList();
        mListViewType.clear();
        productFilterSubHeader.setViewTypeId(VIEW_TYPE_ID_FILTER_HEADER);
        mListViewType.add(productFilterSubHeader);

        for (ProductFilterItem productFilterItem :
                productFilterSubHeader.getProductFilterItemList()) {
            //productFilterItem.setParentId(productFilterHeader.getId());
            productFilterItem.setViewTypeId(VIEW_TYPE_ID_FILTER_ITEM);
        }

        int positionAdded = -1;
        for (int i = 0; i < mListViewType.size(); i++) {
            if (mListViewType.get(i) instanceof ProductFilterSubHeader) {
                ProductFilterSubHeader filterHeader = (ProductFilterSubHeader) mListViewType.get(i);
                if (filterHeader.getId().equals(productFilterSubHeader.getId())) {
                    mListViewType.addAll(i + 1, productFilterItemList);
                    positionAdded = i;
                    break;
                }
            }
        }

        if (positionAdded != -1) {
            notifyItemRangeInserted(positionAdded + 1, productFilterItemList.size());
        }

        notifyDataSetChanged();
    }



    public void removeProductLevel3(ProductFilterList productFilterList) {
        mListViewType.clear();
        for (ProductFilterSubHeader productFilterSubHeader :
                productFilterList.getProductFilterSubHeaders()) {
            productFilterSubHeader.setViewTypeId(VIEW_TYPE_ID_FILTER_HEADER);
            mListViewType.add(productFilterSubHeader);
        }

        notifyDataSetChanged();
    }

    public void  updateSingleProductFilterItem(ProductFilterItem productFilterItem) {
        int parentId = Integer.parseInt(productFilterItem.getId());
        for (ProductFilterSubHeader productFilterSubHeader : mProductFilterSubHeaders) {
            if (parentId == Integer.parseInt(productFilterSubHeader.getId())) {
                if (!productFilterSubHeader.isMultipleType()) {
                    if (productFilterSubHeader.isProductFilterItemListAvailable()) {
                        List<ProductFilterItem> productFilterItemList = productFilterSubHeader.getProductFilterItemList();
                        for (ProductFilterItem headerProductFilterItem :
                                productFilterItemList) {
                            headerProductFilterItem.setSelected(headerProductFilterItem.getId() == productFilterItem.getId());
                        }

                        notifyItemRangeChanged(1, productFilterItemList.size());
                    }
                }
            }

        }

    }
}