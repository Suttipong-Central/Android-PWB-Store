package cenergy.central.com.pwb_store.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.bus.event.SearchQueryBus;
import cenergy.central.com.pwb_store.model.SearchSuggestionItem;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 8/9/2017 AD.
 */

public class SearchSuggestionItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    @BindView(R.id.txt_view_search_item)
    PowerBuyTextView mTextViewSearchItem;

    public SearchSuggestionItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setViewHolder(SearchSuggestionItem searchSuggestionItem) {
        mTextViewSearchItem.setText(searchSuggestionItem.getType());

        itemView.setTag(searchSuggestionItem);
        mTextViewSearchItem.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        SearchSuggestionItem searchSuggestionItem = (SearchSuggestionItem) itemView.getTag();
        EventBus.getDefault().post(new SearchQueryBus(itemView, searchSuggestionItem.getName(),
                String.valueOf(searchSuggestionItem.getId())));
    }
}
