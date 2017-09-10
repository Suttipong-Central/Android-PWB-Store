package cenergy.central.com.pwb_store.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.viewholder.EmptyViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.LoadingViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.SearchResultViewHolder;
import cenergy.central.com.pwb_store.model.IViewType;
import cenergy.central.com.pwb_store.model.ViewType;


public abstract class PowerBuyBaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements BaseAdapterFunction {
    private static final String TAG = PowerBuyBaseAdapter.class.getSimpleName();

    private static final int VIEW_TYPE_ID_DIVIDER_PADDING = 500;
    private static final int VIEW_TYPE_ID_DIVIDER = 501;
    private static final int VIEW_TYPE_ID_LOADING = 502;
    protected static final int VIEW_TYPE_ID_LOADING_ITEM = 503;
    private static final int VIEW_TYPE_ID_ERROR = 504;
    private static final int VIEW_TYPE_ID_EMPTY = 505;

    protected static final ViewType VIEW_TYPE_DIVIDER_PADDING = new ViewType(VIEW_TYPE_ID_DIVIDER_PADDING);
    protected static final ViewType VIEW_TYPE_DIVIDER = new ViewType(VIEW_TYPE_ID_DIVIDER);
    protected static final ViewType VIEW_TYPE_LOADING = new ViewType(VIEW_TYPE_ID_LOADING);
    protected static final ViewType VIEW_TYPE_LOADING_ITEM = new ViewType(VIEW_TYPE_ID_LOADING_ITEM);
    protected static final ViewType VIEW_TYPE_ERROR = new ViewType(VIEW_TYPE_ID_ERROR);
    protected static final ViewType VIEW_TYPE_EMPTY = new ViewType(VIEW_TYPE_ID_EMPTY);

    //Data Member
    protected Context mContext;
    protected List<IViewType> mListViewType = new ArrayList<>();
    protected boolean isLoadingShow = false;
    protected boolean isErrorShow = false;
    protected boolean isEmptyShow = false;

    public PowerBuyBaseAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ID_DIVIDER_PADDING:
                return new EmptyViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_divider_padding, parent, false)
                );
            case VIEW_TYPE_ID_DIVIDER:
                return new EmptyViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_divider, parent, false)
                );
            case VIEW_TYPE_ID_LOADING:
                return new LoadingViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_loading, parent, false)
                );
            case VIEW_TYPE_ID_ERROR:
                return new SearchResultViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_loading_result, parent, false)
                );
            case VIEW_TYPE_ID_LOADING_ITEM:
                return new LoadingViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_loading_item, parent, false)
                );
            case VIEW_TYPE_ID_EMPTY:
                return new EmptyViewHolder(
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
            case VIEW_TYPE_ID_ERROR:
                if (holder instanceof SearchResultViewHolder) {
                    SearchResultViewHolder errorViewHolder = (SearchResultViewHolder) holder;
                    errorViewHolder.setViewHolder();
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

    @Override
    public void resetAdapter() {
        int previousSize = mListViewType.size();
        mListViewType.clear();
        notifyItemRangeRemoved(0, previousSize);

        showLoading();
    }

    @Override
    public void showLoading() {
        mListViewType.add(VIEW_TYPE_LOADING);
        isLoadingShow = true;
        isErrorShow = false;
        isEmptyShow = false;
        notifyItemInserted(0);
    }

    @Override
    public void showError() {
        int previousSize = mListViewType.size();
        mListViewType.clear();
        isLoadingShow = false;
        notifyItemRangeRemoved(0, previousSize);

        mListViewType.add(VIEW_TYPE_ERROR);
        isErrorShow = true;
        notifyItemInserted(0);
    }

    @Override
    public void showEmpty() {
        int previousSize = mListViewType.size();
        mListViewType.clear();
        isLoadingShow = false;
        notifyItemRangeRemoved(0, previousSize);

        mListViewType.add(VIEW_TYPE_EMPTY);
        isEmptyShow = true;
        notifyItemInserted(0);
    }

    @Override
    public boolean isEmpty() {
        if (isLoadingShow || isErrorShow) {
            return mListViewType.size() == 1;
        }

        return mListViewType.isEmpty();
    }

    public boolean isErrorShow() {
        return isErrorShow;
    }
}

interface BaseAdapterFunction {
    void resetAdapter();

    void showLoading();

    void showError();

    void showEmpty();

    boolean isEmpty();
}
