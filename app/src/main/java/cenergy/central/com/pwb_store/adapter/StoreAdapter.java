package cenergy.central.com.pwb_store.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.viewholder.StoreSelectedViewHolder;
import cenergy.central.com.pwb_store.model.IViewType;
import cenergy.central.com.pwb_store.model.StoreDao;
import cenergy.central.com.pwb_store.model.StoreList;

/**
 * Created by napabhat on 9/07/2017 AD.
 */

public class StoreAdapter extends ListDialogAbstractItemAdapter {
    private static final String TAG = StoreAdapter.class.getSimpleName();

    private static final int VIEW_TYPE_ID_STORE_ITEM = 668;

    //Data Members
    private Context mContext;
    private List<IViewType> mListViewType = new ArrayList<>();
    private StoreDao mStoreDao;

    public StoreAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ID_STORE_ITEM:
                return new StoreSelectedViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_selected_store, parent, false)
                );
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewTypeId = getItemViewType(position);
        IViewType viewType = mListViewType.get(position);
        switch (viewTypeId) {
            case VIEW_TYPE_ID_STORE_ITEM:
                if (viewType instanceof StoreList && holder instanceof StoreSelectedViewHolder) {
                    StoreList storeList = (StoreList) viewType;
                    StoreSelectedViewHolder storeSelectedViewHolder = (StoreSelectedViewHolder) holder;
                    storeSelectedViewHolder.setViewHolder(storeList, mListener);
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

    public void setStore(StoreDao storedao) {
        this.mStoreDao = storedao;
        mListViewType.clear();

        for (StoreList storeList : storedao.getStoreLists()) {
            storeList.setViewTypeId(VIEW_TYPE_ID_STORE_ITEM);
            mListViewType.add(storeList);
        }

        notifyDataSetChanged();
    }

    public void updateSingleStore(StoreList storeList) {
        if (!mStoreDao.isStoreEmpty()) {
            if (mStoreDao.isStoreListItemListAvailable()) {
                List<StoreList> storeLists = mStoreDao.getStoreLists();
                for (StoreList headerStore :
                        storeLists) {
                    Log.d(TAG, "loop : " + headerStore.getStoreName());
                    headerStore.setSelected(headerStore.getStoreId().equalsIgnoreCase(storeList.getStoreId()));
                    Log.d(TAG, "bus : " + storeList.getStoreId());
                }
                notifyItemRangeChanged(0, storeLists.size());
            }
        }
    }
}
