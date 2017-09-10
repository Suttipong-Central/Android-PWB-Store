package cenergy.central.com.pwb_store.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.viewholder.LoadingViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.ProductHeaderViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.ProductListViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.ProductSortViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.SearchResultViewHolder;
import cenergy.central.com.pwb_store.model.IViewType;
import cenergy.central.com.pwb_store.model.ProductList;
import cenergy.central.com.pwb_store.model.ProductDao;
import cenergy.central.com.pwb_store.model.TextHeader;
import cenergy.central.com.pwb_store.model.ViewType;

/**
 * Created by napabhat on 7/6/2017 AD.
 */

public class ProductListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //Static Members
    private static final int VIEW_TYPE_ID_GRID_VIEW = 0;
    private static final int VIEW_TYPE_ID_LOADING = 1;
    private static final int VIEW_TYPE_ID_RESULT = 2;

    private static final ViewType VIEW_TYPE_LOADING = new ViewType(VIEW_TYPE_ID_LOADING);
    private static final ViewType VIEW_TYPE_RESULT = new ViewType(VIEW_TYPE_ID_RESULT);

    //Data Members
    private Context mContext;
    private List<IViewType> mListViewType = new ArrayList<>();
    private final GridLayoutManager.SpanSizeLookup mSpanSize = new GridLayoutManager.SpanSizeLookup() {

        @Override
        public int getSpanSize(int position) {
            switch (getItemViewType(position)) {
                case VIEW_TYPE_ID_GRID_VIEW:
                    return 1;
                case VIEW_TYPE_ID_LOADING:
                    return 3;
                case VIEW_TYPE_ID_RESULT:
                    return 3;
                default:
                    return 1;
            }
        }
    };
    private boolean isLoadingShow = false;
    public ProductListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
//            case VIEW_TYPE_ID_PRODUCT_HEADER:
//                return new ProductHeaderViewHolder(
//                        LayoutInflater
//                                .from(parent.getContext())
//                                .inflate(R.layout.list_item_product_header, parent, false)
//                );
//
//            case VIEW_TYPE_ID_PRODUCT_SORT:
//                return new ProductSortViewHolder(
//                        LayoutInflater
//                                .from(parent.getContext())
//                                .inflate(R.layout.list_item_product_sort, parent, false)
//                );

            case VIEW_TYPE_ID_GRID_VIEW:
                return new ProductListViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_grid_product, parent, false)
                );

            case VIEW_TYPE_ID_LOADING:
                return new LoadingViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_loading, parent, false)
                );

            case VIEW_TYPE_ID_RESULT:
                return new SearchResultViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_loading_result, parent, false)
                );
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewTypeId = getItemViewType(position);
        IViewType viewType = mListViewType.get(position);
        switch (viewTypeId) {
//            case VIEW_TYPE_ID_PRODUCT_HEADER:
//                if (viewType instanceof TextHeader && holder instanceof ProductHeaderViewHolder) {
//                    TextHeader textHeader = (TextHeader) viewType;
//                    ProductHeaderViewHolder productHeaderViewHolder = (ProductHeaderViewHolder) holder;
//                    productHeaderViewHolder.setViewHolder(textHeader.getTitle());
//                }
//                break;

//            case VIEW_TYPE_ID_PRODUCT_SORT:
//                if (viewType instanceof ProductSortList && holder instanceof ProductSortViewHolder) {
//                    ProductSortList productSortList = (ProductSortList) viewType;
//                    ProductSortViewHolder productSortViewHolder = (ProductSortViewHolder) holder;
//                    productSortViewHolder.setViewHolder(productSortList);
//                }
//                break;

            case VIEW_TYPE_ID_GRID_VIEW:
                if (viewType instanceof ProductList && holder instanceof ProductListViewHolder) {
                    ProductList productList = (ProductList) viewType;
                    ProductListViewHolder productListViewHolder = (ProductListViewHolder) holder;
                    productListViewHolder.setViewHolder(productList);

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

    public void setProduct(ProductDao productDao){

        if (productDao.isFirstPage()) {
            mListViewType.clear();
            notifyDataSetChanged();
        }
        // Add deal paginated
        int startPosition = mListViewType.size();
        for (ProductList productList : productDao.getProductListList()) {
            productList.setViewTypeId(VIEW_TYPE_ID_GRID_VIEW);
            mListViewType.add(productList);
        }

        if (isLoadingShow) {
            isLoadingShow = false;
            if (productDao.getProductListList().size() == 0) {
                mListViewType.clear();
                notifyDataSetChanged();
                mListViewType.add(VIEW_TYPE_RESULT);
                notifyItemInserted(0);
            }
            notifyItemChanged(0);
            notifyItemRangeInserted(0, mListViewType.size());
        } else {
            notifyItemRangeInserted(startPosition, productDao.getProductListList().size());
        }

//        for (ProductList productList : productDao.getProductListList()){
//            productList.setViewTypeId(VIEW_TYPE_ID_GRID_VIEW);
//            mListViewType.add(productList);
//        }
//
//        notifyDataSetChanged();
    }

    public void showLoading() {
        //mListViewType.add(VIEW_TYPE_LOADING);
        isLoadingShow = true;
        //notifyItemInserted(0);
    }

    public void setError() {
        if (isLoadingShow) {
            isLoadingShow = false;
            mListViewType.clear();
            notifyDataSetChanged();
            mListViewType.add(VIEW_TYPE_RESULT);
            notifyItemInserted(0);
            notifyItemRangeInserted(0, mListViewType.size());
        }else {
            mListViewType.clear();
            notifyDataSetChanged();
            mListViewType.add(VIEW_TYPE_RESULT);
            notifyItemInserted(0);
            notifyItemRangeInserted(0, mListViewType.size());
        }
    }

    public GridLayoutManager.SpanSizeLookup getSpanSize() {
        return mSpanSize;
    }
}
