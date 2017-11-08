package cenergy.central.com.pwb_store.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.viewholder.CategoryFullFillerViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.CompareProductViewHolder;
import cenergy.central.com.pwb_store.model.IViewType;
import cenergy.central.com.pwb_store.model.ProductCompareList;
import cenergy.central.com.pwb_store.model.ViewType;

/**
 * Created by napabhat on 7/26/2017 AD.
 */

public class CompareListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //Static Members
    private static final int VIEW_TYPE_ID_PRODUCT = 0;
    private static final int VIEW_TYPE_ID_FULL_FILLER = 1;

    private static final ViewType VIEW_TYPE_FULL_FILLER = new ViewType(VIEW_TYPE_ID_FULL_FILLER);

    //Data Members
    private Context mContext;
    private List<IViewType> mListViewType = new ArrayList<>();
    private final GridLayoutManager.SpanSizeLookup mSpanSize = new GridLayoutManager.SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
            switch (getItemViewType(position)) {
                case VIEW_TYPE_ID_PRODUCT:
                    return 1;
                default:
                    return 1;
            }
        }
    };

    public CompareListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ID_PRODUCT:
                return new CompareProductViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_compare_product, parent, false)
                );

            case VIEW_TYPE_ID_FULL_FILLER:
                return new CategoryFullFillerViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_category_fullfiller, parent, false)
                );


        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewTypeId = getItemViewType(position);
        IViewType viewType = mListViewType.get(position);
        switch (viewTypeId) {
            case VIEW_TYPE_ID_PRODUCT:
                if (viewType instanceof ProductCompareList && holder instanceof CompareProductViewHolder) {
                    ProductCompareList productCompareList = (ProductCompareList) viewType;
                    CompareProductViewHolder compareProductViewHolder = (CompareProductViewHolder) holder;
                    compareProductViewHolder.setViewHolder(productCompareList);
                }
                break;
        }
    }

    public int getItemCount() {
        //return mListViewType.size();
        return (null != mListViewType ? mListViewType.size() : 0);
    }

    @Override
    public int getItemViewType(int position) {
        return mListViewType.get(position).getViewTypeId();
    }

    public void setproductCompareList(List<ProductCompareList> productCompareList) {

        for (ProductCompareList newproductCompareList : productCompareList){
            newproductCompareList.setViewTypeId(VIEW_TYPE_ID_PRODUCT);
            mListViewType.add(newproductCompareList);
        }
//
//        if (productList.size() % 3 != 0) {
//            int fullFillerNo = 3 - productList.size() % 3;
//            for (int i = 0; i < fullFillerNo; i++) {
//                mListViewType.add(VIEW_TYPE_FULL_FILLER);
//            }
//        }

        notifyDataSetChanged();
    }

    public GridLayoutManager.SpanSizeLookup getSpanSize() {
        return mSpanSize;
    }
}
