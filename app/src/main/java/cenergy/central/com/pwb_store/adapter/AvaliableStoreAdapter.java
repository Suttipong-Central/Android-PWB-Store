package cenergy.central.com.pwb_store.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.viewholder.AvaliableDetailViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.AvaliableTopicViewHolder;
import cenergy.central.com.pwb_store.model.AvaliableStoreDao;
import cenergy.central.com.pwb_store.model.AvaliableStoreItem;
import cenergy.central.com.pwb_store.model.IViewType;
import cenergy.central.com.pwb_store.model.StoreDao;
import cenergy.central.com.pwb_store.model.StoreList;

/**
 * Created by napabhat on 8/16/2017 AD.
 */

public class AvaliableStoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //Static Members
    //private static final int VIEW_TYPE_ID_STORE_HEADER = 0;
    private static final int VIEW_TYPE_ID_STORE_TOPIC = 1;
    private static final int VIEW_TYPE_ID_STORE_DETAIL = 2;

    //private static final ViewType VIEW_TYPE_STORE_HEADER = new ViewType(VIEW_TYPE_ID_STORE_HEADER);
    //private static final ViewType VIEW_TYPE_STORE_TOPIC = new ViewType(VIEW_TYPE_ID_STORE_TOPIC);

    //Data Members
    private Context mContext;
    private StoreDao mStoreDao;
    private List<IViewType> mListViewType = new ArrayList<>();
    private final GridLayoutManager.SpanSizeLookup mSpanSize = new GridLayoutManager.SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
            switch (getItemViewType(position)) {
//                case VIEW_TYPE_ID_STORE_HEADER:
//                    return 4;
                case VIEW_TYPE_ID_STORE_TOPIC:
                    return 4;
                case VIEW_TYPE_ID_STORE_DETAIL:
                    return 4;
                default:
                    return 1;
            }
        }
    };

    public AvaliableStoreAdapter(Context mContext, StoreDao storeDao) {
        this.mContext = mContext;
        this.mStoreDao = storeDao;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
//            case VIEW_TYPE_ID_STORE_HEADER:
//                return new AvaliableHeaderViewHolder(
//                        LayoutInflater
//                                .from(parent.getContext())
//                                .inflate(R.layout.list_item_avaliable_header, parent, false)
//                );
            case VIEW_TYPE_ID_STORE_TOPIC:
                return new AvaliableTopicViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_avaliable_topic, parent, false)
                );
            case VIEW_TYPE_ID_STORE_DETAIL:
                return new AvaliableDetailViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_avaliable_detail, parent, false)
                );
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewTypeId = getItemViewType(position);
        IViewType viewType = mListViewType.get(position);
        switch (viewTypeId) {
            case VIEW_TYPE_ID_STORE_TOPIC:
                if (holder instanceof AvaliableTopicViewHolder) {
                    AvaliableTopicViewHolder avaliableTopicViewHolder = (AvaliableTopicViewHolder) holder;
                    avaliableTopicViewHolder.setViewHolder();
                }
                break;

            case VIEW_TYPE_ID_STORE_DETAIL:
                if (viewType instanceof AvaliableStoreItem && holder instanceof AvaliableDetailViewHolder) {
                    AvaliableStoreItem avaliableStoreItem = (AvaliableStoreItem) viewType;
                    AvaliableDetailViewHolder avaliableDetailViewHolder = (AvaliableDetailViewHolder) holder;
                    avaliableDetailViewHolder.setViewHolder(avaliableStoreItem, mStoreDao);
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

    public void setCompareAvaliable(AvaliableStoreDao avaliableStoreDao){

        //mListViewType.add(VIEW_TYPE_STORE_HEADER);
        avaliableStoreDao.setViewTypeId(VIEW_TYPE_ID_STORE_TOPIC);
        mListViewType.add(avaliableStoreDao);

        List<AvaliableStoreItem> makeAvaliableStoreItems = new ArrayList<>();

        for (AvaliableStoreItem avaliableStoreItem : avaliableStoreDao.getAvaliableProducts().get(0).getAvaliableStoreItems()) {
            for (StoreList storeList : mStoreDao.getStoreLists()){
                if (avaliableStoreItem.getStoreName().equals(storeList.getStoreId())){
                    makeAvaliableStoreItems.add(new AvaliableStoreItem(avaliableStoreItem.getStoreName(), storeList.getStoreAddrNo(), avaliableStoreItem.getStock()));
                }
            }
        }

        for (AvaliableStoreItem avaliableStoreItemShow : makeAvaliableStoreItems){
            avaliableStoreItemShow.setViewTypeId(VIEW_TYPE_ID_STORE_DETAIL);
            mListViewType.add(avaliableStoreItemShow);
        }

        notifyDataSetChanged();
    }

    public GridLayoutManager.SpanSizeLookup getSpanSize() {
        return mSpanSize;
    }
}
