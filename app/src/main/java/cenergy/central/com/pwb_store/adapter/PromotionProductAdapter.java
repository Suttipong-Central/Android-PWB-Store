package cenergy.central.com.pwb_store.adapter;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.viewholder.PromotionItemProductViewHolder;
import cenergy.central.com.pwb_store.model.IViewType;
import cenergy.central.com.pwb_store.model.Promotion;
import cenergy.central.com.pwb_store.model.PromotionItem;

/**
 * Created by napabhat on 8/2/2017 AD.
 */

public class PromotionProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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

    public PromotionProductAdapter(Context mContext) {
        this.mContext = mContext;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ID_PROMOTION_ITEM:
                return new PromotionItemProductViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_promotion_product, parent, false)
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
                if (viewType instanceof PromotionItem && holder instanceof PromotionItemProductViewHolder) {
                    PromotionItem promotionItem = (PromotionItem) viewType;
                    PromotionItemProductViewHolder promotionItemProductViewHolder = (PromotionItemProductViewHolder) holder;
                    promotionItemProductViewHolder.setViewHolder(promotionItem);
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

    public void setPromotionProduct(Promotion promotion){

        for (PromotionItem promotionItem : promotion.getPromotionItemList()) {
            promotionItem.setViewTypeId(VIEW_TYPE_ID_PROMOTION_ITEM);
            mListViewType.add(promotionItem);
        }

        notifyDataSetChanged();
    }

    public GridLayoutManager.SpanSizeLookup getSpanSize() {
        return mSpanSize;
    }
}
