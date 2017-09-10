package cenergy.central.com.pwb_store.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.viewholder.SearchSuggestionItemViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.SearchSuggestionHeaderViewHolder;
import cenergy.central.com.pwb_store.model.IViewType;
import cenergy.central.com.pwb_store.model.SearchSuggestion;
import cenergy.central.com.pwb_store.model.SearchSuggestionDao;
import cenergy.central.com.pwb_store.model.SearchSuggestionHeader;
import cenergy.central.com.pwb_store.model.SearchSuggestionItem;

/**
 * Created by napabhat on 8/10/2017 AD.
 */

public class SearchListSuggestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //Static Members
    private static final int VIEW_TYPE_ID_SEARCH_HEADER = 0;
    private static final int VIEW_TYPE_ID_SEARCH_ITEM = 1;

    //Data Members
    private Context mContext;
    private List<IViewType> mListViewType = new ArrayList<>();

    public SearchListSuggestionAdapter(Context mContext) {
        this.mContext = mContext;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ID_SEARCH_HEADER:
                return new SearchSuggestionHeaderViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_search_suggestion_header, parent, false)
                );
            case VIEW_TYPE_ID_SEARCH_ITEM:
                return new SearchSuggestionItemViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_search_history_item, parent, false)
                );
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewTypeId = getItemViewType(position);
        IViewType viewType = mListViewType.get(position);
        switch (viewTypeId) {
            case VIEW_TYPE_ID_SEARCH_HEADER:
                if (viewType instanceof SearchSuggestionHeader && holder instanceof SearchSuggestionHeaderViewHolder) {
                    SearchSuggestionHeader searchSuggestionHeader = (SearchSuggestionHeader) viewType;
                    SearchSuggestionHeaderViewHolder searchSuggestionHeaderViewHolder = (SearchSuggestionHeaderViewHolder) holder;
                    searchSuggestionHeaderViewHolder.setViewHolder(searchSuggestionHeader);
                }
                break;

            case VIEW_TYPE_ID_SEARCH_ITEM:
                if (viewType instanceof SearchSuggestionItem && holder instanceof SearchSuggestionItemViewHolder) {
                    SearchSuggestionItem searchSuggestionItem = (SearchSuggestionItem) viewType;
                    SearchSuggestionItemViewHolder searchSuggestionItemViewHolder = (SearchSuggestionItemViewHolder) holder;
                    searchSuggestionItemViewHolder.setViewHolder(searchSuggestionItem);
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

    public void setSearch(SearchSuggestionDao searchSuggestionDao) {
        SearchSuggestion searchSuggestion = searchSuggestionDao.getSearchSuggestion();

        SearchSuggestionHeader searchSuggestionHeader = new SearchSuggestionHeader(searchSuggestionDao.getQuery(), SearchSuggestionHeaderViewHolder.SearchSuggestionHeaderType.SEARCH_RESULT);
        searchSuggestionHeader.setViewTypeId(VIEW_TYPE_ID_SEARCH_HEADER);
        mListViewType.add(searchSuggestionHeader);
        //boolean isPaddingDividerAdded = false;
        for (SearchSuggestionItem searchSuggestionItem :
                searchSuggestion.getSearchSuggestionItemList()) {
            searchSuggestionItem.setViewTypeId(VIEW_TYPE_ID_SEARCH_ITEM);
//            if (searchSuggestionItem.isTypeShop() && !isPaddingDividerAdded) {
//                isPaddingDividerAdded = true;
//                mListViewType.add(VIEW_TYPE_DIVIDER_PADDING);
//            }
            mListViewType.add(searchSuggestionItem);

            notifyDataSetChanged();
        }

    }

    public void setSearchDetail(List<SearchSuggestionItem> searchSuggestionItems) {
        mListViewType.clear();
        //boolean isPaddingDividerAdded = false;
        for (SearchSuggestionItem searchSuggestionItem :
                searchSuggestionItems) {
            searchSuggestionItem.setViewTypeId(VIEW_TYPE_ID_SEARCH_ITEM);
//            if (searchSuggestionItem.isTypeShop() && !isPaddingDividerAdded) {
//                isPaddingDividerAdded = true;
//                mListViewType.add(VIEW_TYPE_DIVIDER_PADDING);
//            }
            mListViewType.add(searchSuggestionItem);

            notifyDataSetChanged();
        }

    }
}

