package cenergy.central.com.pwb_store.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.viewholder.SearchHistoryClearViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.SearchSuggestionItemViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.SearchHistoryViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.SearchSuggestionHeaderViewHolder;
import cenergy.central.com.pwb_store.model.IViewType;
import cenergy.central.com.pwb_store.model.ProductList;
import cenergy.central.com.pwb_store.model.SearchHistoryItem;
import cenergy.central.com.pwb_store.model.SearchSuggestion;
import cenergy.central.com.pwb_store.model.SearchSuggestionCategory;
import cenergy.central.com.pwb_store.model.SearchSuggestionCategoryItem;
import cenergy.central.com.pwb_store.model.SearchSuggestionDao;
import cenergy.central.com.pwb_store.model.SearchSuggestionHeader;
import cenergy.central.com.pwb_store.model.SearchSuggestionItem;
import cenergy.central.com.pwb_store.model.SearchSuggestionProduct;
import cenergy.central.com.pwb_store.model.ViewType;

/**
 * Created by napabhat on 8/9/2017 AD.
 */

public class SearchSuggestionAdapter extends PowerBuyBaseAdapter {
    private static final String TAG = SearchSuggestionAdapter.class.getSimpleName();

    private static final int VIEW_TYPE_ID_HISTORY_HEADER = 127;
    private static final int VIEW_TYPE_ID_HISTORY_ITEM = 172;
    private static final int VIEW_TYPE_ID_HISTORY_CLEAR = 487;
    private static final int VIEW_TYPE_ID_SUGGESTION_HEADER = 76;
    private static final int VIEW_TYPE_ID_SUGGESTION_ITEM = 258;
    private static final int VIEW_TYPE_ID_SUGGESTION_CATEGORY_ITEM = 264;
    private static final int VIEW_TYPE_ID_SUGGESTION_PRODUCT = 119;

    private static final ViewType VIEW_TYPE_HISTORY_HEADER = new ViewType(VIEW_TYPE_ID_HISTORY_HEADER);
    private static final ViewType VIEW_TYPE_HISTORY_CLEAR = new ViewType(VIEW_TYPE_ID_HISTORY_CLEAR);

    public SearchSuggestionAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ID_HISTORY_HEADER:
                return new SearchHistoryViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_search_history_header, parent, false)
                );
            case VIEW_TYPE_ID_HISTORY_ITEM:
                return new SearchSuggestionItemViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_search_history_item, parent, false)
                );
            case VIEW_TYPE_ID_HISTORY_CLEAR:
                return new SearchHistoryClearViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_search_history_clear, parent, false)
                );
//            case VIEW_TYPE_ID_SUGGESTION_HEADER:
//                return new SearchSuggestionHeaderViewHolder(
//                        LayoutInflater
//                                .from(parent.getContext())
//                                .inflate(R.layout.list_item_search_suggestion_header, parent, false)
//                );
//            case VIEW_TYPE_ID_SUGGESTION_ITEM:
//                return new SearchSuggestionItemViewHolder(
//                        LayoutInflater
//                                .from(parent.getContext())
//                                .inflate(R.layout.list_item_search_suggestion_item, parent, false)
//                );
//            case VIEW_TYPE_ID_SUGGESTION_CATEGORY_ITEM:
//                return new SearchSuggestionCategoryItemViewHolder(
//                        LayoutInflater
//                                .from(parent.getContext())
//                                .inflate(R.layout.list_item_search_suggestion_item, parent, false)
//                );
//            case VIEW_TYPE_ID_SUGGESTION_PRODUCT:
//                return new SearchSuggestionProductViewHolder(
//                        LayoutInflater
//                                .from(parent.getContext())
//                                .inflate(R.layout.list_item_search_suggestion_product, parent, false)
//                );
        }

        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewTypeId = getItemViewType(position);
        IViewType viewType = mListViewType.get(position);
        switch (viewTypeId) {
            case VIEW_TYPE_ID_HISTORY_ITEM:
                if (viewType instanceof SearchSuggestionItem && holder instanceof SearchSuggestionItemViewHolder) {
                    SearchSuggestionItem searchSuggestionItem = (SearchSuggestionItem) viewType;
                    SearchSuggestionItemViewHolder searchSuggestionItemViewHolder = (SearchSuggestionItemViewHolder) holder;
                    searchSuggestionItemViewHolder.setViewHolder(searchSuggestionItem);
                }
                break;
            case VIEW_TYPE_ID_SUGGESTION_HEADER:
                if (viewType instanceof SearchSuggestionHeader && holder instanceof SearchSuggestionHeaderViewHolder) {
                    SearchSuggestionHeader searchSuggestionHeader = (SearchSuggestionHeader) viewType;
                    SearchSuggestionHeaderViewHolder searchSuggestionHeaderViewHolder = (SearchSuggestionHeaderViewHolder) holder;
                    searchSuggestionHeaderViewHolder.setViewHolder(searchSuggestionHeader);
                }
                break;
//            case VIEW_TYPE_ID_SUGGESTION_ITEM:
//                if (viewType instanceof SearchSuggestionItem && holder instanceof SearchSuggestionItemViewHolder) {
//                    SearchSuggestionItem searchSuggestionItem = (SearchSuggestionItem) viewType;
//                    SearchSuggestionItemViewHolder searchSuggestionItemViewHolder = (SearchSuggestionItemViewHolder) holder;
//                    searchSuggestionItemViewHolder.setViewHolder(searchSuggestionItem);
//                }
//                break;
//            case VIEW_TYPE_ID_SUGGESTION_CATEGORY_ITEM:
//                if (viewType instanceof SearchSuggestionCategoryItem && holder instanceof SearchSuggestionCategoryItemViewHolder) {
//                    SearchSuggestionCategoryItem searchSuggestionCategoryItem = (SearchSuggestionCategoryItem) viewType;
//                    SearchSuggestionCategoryItemViewHolder searchSuggestionCategoryItemViewHolder = (SearchSuggestionCategoryItemViewHolder) holder;
//                    searchSuggestionCategoryItemViewHolder.setViewHolder(searchSuggestionCategoryItem);
//                }
//                break;
//            case VIEW_TYPE_ID_SUGGESTION_PRODUCT:
//                if (viewType instanceof Product && holder instanceof SearchSuggestionProductViewHolder) {
//                    Product product = (Product) viewType;
//                    SearchSuggestionProductViewHolder searchSuggestionProductViewHolder = (SearchSuggestionProductViewHolder) holder;
//                    searchSuggestionProductViewHolder.setViewHolder(product);
//                }
//                break;
            default:
                super.onBindViewHolder(holder, position);
                break;
        }
    }

    public void setSearchSuggestion(SearchSuggestionDao searchSuggestionDao) {
        //mListViewType.clear();

        if (searchSuggestionDao.isSearchSuggestionAvailable()) {
            SearchSuggestion searchSuggestion = searchSuggestionDao.getSearchSuggestion();

            SearchSuggestionHeader searchSuggestionHeader = new SearchSuggestionHeader(searchSuggestionDao.getQuery(), SearchSuggestionHeaderViewHolder.SearchSuggestionHeaderType.SEARCH_RESULT);
            searchSuggestionHeader.setViewTypeId(VIEW_TYPE_ID_SUGGESTION_HEADER);
            mListViewType.add(searchSuggestionHeader);
            boolean isPaddingDividerAdded = false;
            for (SearchSuggestionItem searchSuggestionItem :
                    searchSuggestion.getSearchSuggestionItemList()) {
                searchSuggestionItem.setViewTypeId(VIEW_TYPE_ID_SUGGESTION_ITEM);

                if (searchSuggestionItem.isTypeShop() && !isPaddingDividerAdded) {
                    isPaddingDividerAdded = true;
                    mListViewType.add(VIEW_TYPE_DIVIDER_PADDING);
                }

                mListViewType.add(searchSuggestionItem);
            }
        }

        if (searchSuggestionDao.isSearchSuggestionCategoryAvailable()) {
            mListViewType.add(VIEW_TYPE_DIVIDER_PADDING);
            SearchSuggestionCategory searchSuggestionCategory = searchSuggestionDao.getSearchSuggestionCategory();
            for (SearchSuggestionCategoryItem searchSuggestionCategoryItem :
                    searchSuggestionCategory.getItems()) {
                searchSuggestionCategoryItem.setViewTypeId(VIEW_TYPE_ID_SUGGESTION_CATEGORY_ITEM);
                searchSuggestionCategoryItem.setQuery(searchSuggestionDao.getQuery());
                mListViewType.add(searchSuggestionCategoryItem);
            }
        }

        if (searchSuggestionDao.isSearchSuggestionProductAvailable()) {
            mListViewType.add(VIEW_TYPE_DIVIDER);
            SearchSuggestionProduct searchSuggestionProduct = searchSuggestionDao.getSearchSuggestionProduct();
            SearchSuggestionHeader searchSuggestionHeader = new SearchSuggestionHeader(searchSuggestionDao.getQuery(), SearchSuggestionHeaderViewHolder.SearchSuggestionHeaderType.PRODUCT_SUGGESTION_RESULT);
            searchSuggestionHeader.setViewTypeId(VIEW_TYPE_ID_SUGGESTION_HEADER);
            mListViewType.add(searchSuggestionHeader);
            for (ProductList product :
                    searchSuggestionProduct.getProductList()) {
                product.setViewTypeId(VIEW_TYPE_ID_SUGGESTION_PRODUCT);
                mListViewType.add(product);
            }
        }


        notifyDataSetChanged();
    }

    public void setSearchHistory(List<SearchHistoryItem> searchHistoryItemList) {
        mListViewType.clear();

        if (!searchHistoryItemList.isEmpty()) {
            mListViewType.add(VIEW_TYPE_HISTORY_HEADER);

            for (SearchHistoryItem searchHistoryItem :
                    searchHistoryItemList) {
                searchHistoryItem.setViewTypeId(VIEW_TYPE_ID_HISTORY_ITEM);
                mListViewType.add(searchHistoryItem);
            }

            mListViewType.add(VIEW_TYPE_HISTORY_CLEAR);
        }

        if (isLoadingShow) {
            isLoadingShow = false;
            notifyItemChanged(0);
            notifyItemRangeInserted(1, mListViewType.size() - 1);
        } else {
            notifyItemRangeInserted(0, mListViewType.size());
        }
    }

    @Override
    public void resetAdapter() {
        if (mListViewType.size() != 1 || mListViewType.get(0) != VIEW_TYPE_LOADING) {
            int previousSize = mListViewType.size();
            mListViewType.clear();
            notifyItemRangeRemoved(0, previousSize);

            showLoading();
        }
    }
}
