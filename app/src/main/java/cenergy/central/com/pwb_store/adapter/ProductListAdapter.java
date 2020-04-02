package cenergy.central.com.pwb_store.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.activity.BaseActivity;
import cenergy.central.com.pwb_store.activity.ProductDetailActivity;
import cenergy.central.com.pwb_store.adapter.viewholder.ProductListViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.SearchResultViewHolder;
import cenergy.central.com.pwb_store.model.IViewType;
import cenergy.central.com.pwb_store.model.Product;
import cenergy.central.com.pwb_store.model.ViewType;
import cenergy.central.com.pwb_store.utils.Analytics;

public class ProductListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //Static Members
    private static final int VIEW_TYPE_ID_GRID_VIEW = 0;
    private static final int VIEW_TYPE_ID_RESULT = 2;

    private static final ViewType VIEW_TYPE_RESULT = new ViewType(VIEW_TYPE_ID_RESULT);

    //Data Members
    private Context mContext;
    private List<IViewType> mListViewType = new ArrayList<>();
    private final GridLayoutManager.SpanSizeLookup mSpanSize = new GridLayoutManager.SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
            if (getItemViewType(position) == VIEW_TYPE_ID_RESULT) {
                return 3;
            }
            return 1;
        }
    };

    private Boolean clicked = true;

    public ProductListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ID_GRID_VIEW) {
            return new ProductListViewHolder(
                    LayoutInflater
                            .from(parent.getContext())
                            .inflate(R.layout.list_item_grid_product, parent, false)
            );
        }
        return new SearchResultViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.list_item_loading_result, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewTypeId = getItemViewType(position);
        IViewType viewType = mListViewType.get(position);
        if (viewTypeId == VIEW_TYPE_ID_GRID_VIEW) {
            if (viewType instanceof Product && holder instanceof ProductListViewHolder) {
                final Product product = (Product) viewType;
                ProductListViewHolder productListViewHolder = (ProductListViewHolder) holder;
                productListViewHolder.setViewHolder(product);
                productListViewHolder.itemView.setOnClickListener(view -> {
                    if (clicked) {
                        clicked = false;
                        Analytics analytics = new Analytics(mContext);
                        analytics.trackViewItem(product.getSku());
                        Intent intent = new Intent(mContext, ProductDetailActivity.class);
                        intent.putExtra(ProductDetailActivity.ARG_PRODUCT_SKU, product.getSku());
                        ((Activity) mContext).startActivityForResult(intent, BaseActivity.REQUEST_UPDATE_LANGUAGE);
                        new Handler().postDelayed(() -> clicked = true, 1000);
                    }
                });
            }
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

    public void setProduct(List<Product> products) {
        mListViewType.clear();
        for (Product product : products){
            product.setAttributeID(VIEW_TYPE_ID_GRID_VIEW);
            mListViewType.add(product);
        }
        notifyDataSetChanged();
    }

    public void setError() {
        mListViewType.clear();
        mListViewType.add(VIEW_TYPE_RESULT);
        notifyDataSetChanged();
    }

    public GridLayoutManager.SpanSizeLookup getSpanSize() {
        return mSpanSize;
    }
}
