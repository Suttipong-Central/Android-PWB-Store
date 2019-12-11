package cenergy.central.com.pwb_store.adapter;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.viewholder.PromotionItemPaymentViewHolder;
import cenergy.central.com.pwb_store.model.IViewType;
import cenergy.central.com.pwb_store.model.PromotionPayment;
import cenergy.central.com.pwb_store.model.PromotionPaymentItem;

/**
 * Created by napabhat on 8/2/2017 AD.
 */

public class PromotionPaymentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //Static Members
    private static final int VIEW_TYPE_ID_PROMOTION_ITEM = 0;

    //Data Members
    private Context mContext;
    private List<IViewType> mListViewType = new ArrayList<>();
    private final GridLayoutManager.SpanSizeLookup mSpanSize = new GridLayoutManager.SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
            switch (getItemViewType(position)) {
                case VIEW_TYPE_ID_PROMOTION_ITEM:
                    return 1;
                default:
                    return 1;
            }
        }
    };

    public PromotionPaymentAdapter(Context mContext) {
        this.mContext = mContext;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ID_PROMOTION_ITEM:
                return new PromotionItemPaymentViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_promotion_payment, parent, false)
                );
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewTypeId = getItemViewType(position);
        IViewType viewType = mListViewType.get(position);
        switch (viewTypeId) {
            case VIEW_TYPE_ID_PROMOTION_ITEM:
                if (viewType instanceof PromotionPaymentItem && holder instanceof PromotionItemPaymentViewHolder) {
                    PromotionPaymentItem promotionPaymentItem = (PromotionPaymentItem) viewType;
                    PromotionItemPaymentViewHolder promotionItemPaymentViewHolder = (PromotionItemPaymentViewHolder) holder;
                    promotionItemPaymentViewHolder.setViewHolder(promotionPaymentItem);
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

    public void setPromotionPayment(PromotionPayment promotionPayment){

        for (PromotionPaymentItem promotionPaymentItem : promotionPayment.getPromotionPaymentItemList()) {
            promotionPaymentItem.setViewTypeId(VIEW_TYPE_ID_PROMOTION_ITEM);
            mListViewType.add(promotionPaymentItem);
        }

        notifyDataSetChanged();
    }

    public GridLayoutManager.SpanSizeLookup getSpanSize() {
        return mSpanSize;
    }
}
