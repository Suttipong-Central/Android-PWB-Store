package cenergy.central.com.pwb_store.adapter;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.interfaces.CompareItemListener;
import cenergy.central.com.pwb_store.adapter.viewholder.CompareDetailViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.CompareHeaderViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.CompareListProductViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.CompareShoppingCartViewHolder;
import cenergy.central.com.pwb_store.model.CompareDao;
import cenergy.central.com.pwb_store.model.CompareDetail;
import cenergy.central.com.pwb_store.model.CompareDetailItem;
import cenergy.central.com.pwb_store.model.CompareList;
import cenergy.central.com.pwb_store.model.CompareProduct;
import cenergy.central.com.pwb_store.model.IViewType;
import cenergy.central.com.pwb_store.model.ViewType;

/**
 * Created by napabhat on 7/26/2017 AD.
 */

public class CompareProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = CompareProductAdapter.class.getSimpleName();
    //Static Members
    private static final int VIEW_TYPE_ID_PRODUCT_LIST = 0;
    private static final int VIEW_TYPE_ID_COMPARE_HEADER = 1;
    private static final int VIEW_TYPE_ID_COMPARE_DETAIL = 2;
    private static final int VIEW_TYPE_ID_SHOPPING_CART = 3;

    private static final ViewType VIEW_TYPE_COMPARE_HEADER = new ViewType(VIEW_TYPE_ID_COMPARE_HEADER);
    private static final ViewType VIEW_TYPE_SHOPPING_CART = new ViewType(VIEW_TYPE_ID_SHOPPING_CART);

    //Data Members
    private Context mContext;
    private CompareItemListener listener;
    private List<IViewType> mListViewType = new ArrayList<>();
    private CompareDao mCompareDao;
    private List<CompareDetail> mCompareDetail = new ArrayList<>();
    private final GridLayoutManager.SpanSizeLookup mSpanSize = new GridLayoutManager.SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
            switch (getItemViewType(position)) {
                case VIEW_TYPE_ID_PRODUCT_LIST:
                    return 4;
                case VIEW_TYPE_ID_COMPARE_HEADER:
                    return 4;
                case VIEW_TYPE_ID_COMPARE_DETAIL:
                    return 4;
                case VIEW_TYPE_ID_SHOPPING_CART:
                    return 4;
                default:
                    return 1;
            }
        }
    };


    public CompareProductAdapter(Context context) {
        this.mContext = context;
        this.listener = (CompareItemListener) context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ID_PRODUCT_LIST:
                return new CompareListProductViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_horizontal_product_compare, parent, false)
                );
            case VIEW_TYPE_ID_COMPARE_HEADER:
                return new CompareHeaderViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_compare_product_header, parent, false)
                );
            case VIEW_TYPE_ID_COMPARE_DETAIL:
                return new CompareDetailViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_grid_compare_detail, parent, false)
                );
            case VIEW_TYPE_ID_SHOPPING_CART:
                return new CompareShoppingCartViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_compare_schopping_cart, parent, false)
                );
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewTypeId = getItemViewType(position);
        IViewType viewType = mListViewType.get(position);
        switch (viewTypeId) {
            case VIEW_TYPE_ID_PRODUCT_LIST:
                if (viewType instanceof CompareList && holder instanceof CompareListProductViewHolder) {
                    CompareList compareList = (CompareList) viewType;
                    CompareListProductViewHolder compareListProductViewHolder = (CompareListProductViewHolder) holder;
                    compareListProductViewHolder.setViewHolder(mContext, compareList);
                }
                break;

            case VIEW_TYPE_ID_COMPARE_DETAIL:
                if (viewType instanceof CompareDao && holder instanceof CompareDetailViewHolder) {
                    CompareDao compareDao = (CompareDao) viewType;
                    CompareDetailViewHolder compareDetailViewHolder = (CompareDetailViewHolder) holder;
                    compareDetailViewHolder.setViewHolder(mContext, compareDao);
                }
                break;

            case VIEW_TYPE_ID_SHOPPING_CART:
                if (holder instanceof CompareShoppingCartViewHolder) {
                    CompareList compareList = (CompareList) viewType;
                    CompareShoppingCartViewHolder compareDetailViewHolder = (CompareShoppingCartViewHolder) holder;
                    compareDetailViewHolder.setViewHolder(mContext, listener, compareList);
                }
                break;
        }
    }

    public int getItemCount() {
        return mListViewType.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mListViewType.get(position).getViewTypeId();
    }

    public void setCompare(CompareList compareList) {

        mListViewType.add(VIEW_TYPE_COMPARE_HEADER);

        compareList.setViewTypeId(VIEW_TYPE_ID_PRODUCT_LIST);
        mListViewType.add(compareList);

        if (compareList.getCompareDao() != null) {
            CompareDao compareDao = compareList.getCompareDao();
            for (CompareDetail compareDetail : compareDao.getCompareDetails()) {
                List<CompareDetailItem> compareDetailItems = new ArrayList<>();
                if (compareDetail.getCompareDetailItems().size() < 4) {
                    int j = 4 - compareDetail.getCompareDetailItems().size();

                    for (CompareDetailItem compareDetailItem : compareDetail.getCompareDetailItems()) {
                        compareDetailItems.add(new CompareDetailItem(compareDetailItem.getType()));
                        Log.d(TAG, "" + compareDetailItem.getType());
                    }
                    for (int i = 1; i <= j; i++) {
                        Log.d(TAG, "i" + i);
                        compareDetailItems.add(new CompareDetailItem(""));
                    }

                } else {
                    for (CompareDetailItem compareDetailItem : compareDetail.getCompareDetailItems()) {
                        compareDetailItems.add(new CompareDetailItem(compareDetailItem.getType()));
                    }
                }
                mCompareDetail.add(new CompareDetail(compareDetail.getName(), compareDetailItems));
            }
            mCompareDao = new CompareDao(0, mCompareDetail);
            mCompareDao.setViewTypeId(VIEW_TYPE_ID_COMPARE_DETAIL);
            mListViewType.add(mCompareDao);
        }
        mListViewType.add(VIEW_TYPE_SHOPPING_CART);
        notifyDataSetChanged();
    }

    public void setUpdateCompare(CompareList compareList) {

        mListViewType.clear();

        mListViewType.add(VIEW_TYPE_COMPARE_HEADER);

        compareList.setViewTypeId(VIEW_TYPE_ID_PRODUCT_LIST);
        mListViewType.add(compareList);

//        if (compareList.getCompareDao() != null) {
//            CompareDao compareDao = compareList.getCompareDao();
//            compareDao.setViewTypeId(VIEW_TYPE_ID_COMPARE_DETAIL);
//            mListViewType.add(compareDao);
//        }

        if (compareList.getCompareDao() != null) {
            CompareDao compareDao = compareList.getCompareDao();
            for (CompareDetail compareDetail : compareDao.getCompareDetails()) {
                List<CompareDetailItem> compareDetailItems = new ArrayList<>();
                if (compareDetail.getCompareDetailItems().size() < 4) {
                    int j = 4 - compareDetail.getCompareDetailItems().size();

                    for (CompareDetailItem compareDetailItem : compareDetail.getCompareDetailItems()) {
                        compareDetailItems.add(new CompareDetailItem(compareDetailItem.getType()));
                        Log.d(TAG, "" + compareDetailItem.getType());
                    }
                    for (int i = 1; i <= j; i++) {
                        Log.d(TAG, "i" + i);
                        compareDetailItems.add(new CompareDetailItem(""));
                    }

                } else {
                    for (CompareDetailItem compareDetailItem : compareDetail.getCompareDetailItems()) {
                        compareDetailItems.add(new CompareDetailItem(compareDetailItem.getType()));
                    }
                }
                mCompareDetail.add(new CompareDetail(compareDetail.getName(), compareDetailItems));
            }
            mCompareDao = new CompareDao(0, mCompareDetail);
            mCompareDao.setViewTypeId(VIEW_TYPE_ID_COMPARE_DETAIL);
            mListViewType.add(mCompareDao);
        }
        mListViewType.add(VIEW_TYPE_SHOPPING_CART);
        notifyDataSetChanged();
    }

    public void updateCompareProducts(List<CompareProduct> compareProducts, CompareDao compare) {
        // TBD- for show product
        CompareList compareList = new CompareList(compareProducts, compare);
        CompareList compareListProduct = new CompareList(compareProducts);
        mListViewType.clear();
        mCompareDetail.clear();
        mListViewType.add(VIEW_TYPE_COMPARE_HEADER);
        compareList.setViewTypeId(VIEW_TYPE_ID_PRODUCT_LIST);
        mListViewType.add(compareList);
        // region mockup from older code version
        if (compareList.getCompareDao() != null) {
            CompareDao compareDao = compareList.getCompareDao();
            for (CompareDetail compareDetail : compareDao.getCompareDetails()) {
                List<CompareDetailItem> compareDetailItems = new ArrayList<>();
                if (compareDetail.getCompareDetailItems().size() < 4) {
                    int j = 4 - compareDetail.getCompareDetailItems().size();

                    for (CompareDetailItem compareDetailItem : compareDetail.getCompareDetailItems()) {
                        compareDetailItems.add(new CompareDetailItem(compareDetailItem.getType()));
                        Log.d(TAG, "" + compareDetailItem.getType());
                    }
                    for (int i = 1; i <= j; i++) {
                        Log.d(TAG, "i" + i);
                        compareDetailItems.add(new CompareDetailItem(""));
                    }

                } else {
                    for (CompareDetailItem compareDetailItem : compareDetail.getCompareDetailItems()) {
                        compareDetailItems.add(new CompareDetailItem(compareDetailItem.getType()));
                    }
                }
                mCompareDetail.add(new CompareDetail(compareDetail.getName(), compareDetailItems));
            }
            mCompareDao = new CompareDao(0, mCompareDetail);
            mCompareDao.setViewTypeId(VIEW_TYPE_ID_COMPARE_DETAIL);
            mListViewType.add(mCompareDao);
        }
        // endregion

        compareListProduct.setViewTypeId(VIEW_TYPE_ID_SHOPPING_CART);
        mListViewType.add(compareListProduct);
        notifyDataSetChanged();
    }

    public GridLayoutManager.SpanSizeLookup getSpanSize() {
        return mSpanSize;
    }
}
