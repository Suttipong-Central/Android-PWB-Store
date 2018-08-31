package cenergy.central.com.pwb_store.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.viewholder.CompareHeaderDetailViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.CompareItemDetailViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.CompareNotShowSpecViewHolder;
import cenergy.central.com.pwb_store.model.CompareDao;
import cenergy.central.com.pwb_store.model.CompareDetail;
import cenergy.central.com.pwb_store.model.CompareDetailItem;
import cenergy.central.com.pwb_store.model.IViewType;
import cenergy.central.com.pwb_store.model.ViewType;

/**
 * Created by napabhat on 7/31/2017 AD.
 */

public class CompareDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //Static Members
    private static final int VIEW_TYPE_ID_COMPARE_HEADER = 0;
    private static final int VIEW_TYPE_ID_COMPARE_ITEM = 1;
    private static final int VIEW_TYPE_ID_CANNOT_SHOW_SPEC = 2;

    private static final ViewType VIEW_TYPE_CANNOT_SHOW_SPEC = new ViewType(VIEW_TYPE_ID_CANNOT_SHOW_SPEC);

    //Data Members
    private Context mContext;
    private List<IViewType> mListViewType = new ArrayList<>();
    private final GridLayoutManager.SpanSizeLookup mSpanSize = new GridLayoutManager.SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
            switch (getItemViewType(position)) {
                case VIEW_TYPE_ID_COMPARE_HEADER:
                    return 4;
                case VIEW_TYPE_ID_COMPARE_ITEM:
                    return 1;
                case VIEW_TYPE_ID_CANNOT_SHOW_SPEC:
                    return 4;
                default:
                    return 1;
            }
        }
    };

    public CompareDetailAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ID_COMPARE_HEADER:
                return new CompareHeaderDetailViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_text_header_compare_detail, parent, false)
                );
            case VIEW_TYPE_ID_CANNOT_SHOW_SPEC:
                return new CompareNotShowSpecViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_cannot_show_spec_compare_detail, parent, false)
                );
            case VIEW_TYPE_ID_COMPARE_ITEM:
                return new CompareItemDetailViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_text_compare_detail, parent, false)
                );
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewTypeId = getItemViewType(position);
        IViewType viewType = mListViewType.get(position);
        switch (viewTypeId) {
            case VIEW_TYPE_ID_COMPARE_HEADER:
                if (viewType instanceof CompareDetail && holder instanceof CompareHeaderDetailViewHolder) {
                    CompareDetail compareDetail = (CompareDetail) viewType;
                    CompareHeaderDetailViewHolder compareHeaderDetailViewHolder = (CompareHeaderDetailViewHolder) holder;
                    compareHeaderDetailViewHolder.setViewHolder(compareDetail);
                }
                break;
            case VIEW_TYPE_ID_COMPARE_ITEM:
                if (viewType instanceof CompareDetailItem && holder instanceof CompareItemDetailViewHolder) {
                    CompareDetailItem compareDetailItem = (CompareDetailItem) viewType;
                    CompareItemDetailViewHolder compareItemDetailViewHolder = (CompareItemDetailViewHolder) holder;
                    compareItemDetailViewHolder.setViewHolder(compareDetailItem);
                }
                break;
            case VIEW_TYPE_ID_CANNOT_SHOW_SPEC:
                if (holder instanceof CompareNotShowSpecViewHolder) {
                    CompareNotShowSpecViewHolder compareNotShowSpecViewHolder = (CompareNotShowSpecViewHolder) holder;
                    compareNotShowSpecViewHolder.bindView();
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

    public void setCompareDetail(CompareDao compareDao){
//        for (CompareDetail compareDetail : compareDao.getCompareDetails()){
//            compareDetail.setViewTypeId(VIEW_TYPE_ID_COMPARE_HEADER);
//            mListViewType.add(compareDetail);
//
//            for (CompareDetailItem compareDetailItem : compareDetail.getCompareDetailItems()){
//                compareDetailItem.setViewTypeId(VIEW_TYPE_ID_COMPARE_ITEM);
//                mListViewType.add(compareDetailItem);
//            }
//        }
        mListViewType.add(VIEW_TYPE_CANNOT_SHOW_SPEC);
        notifyDataSetChanged();
    }

    public GridLayoutManager.SpanSizeLookup getSpanSize() {
        return mSpanSize;
    }
}
