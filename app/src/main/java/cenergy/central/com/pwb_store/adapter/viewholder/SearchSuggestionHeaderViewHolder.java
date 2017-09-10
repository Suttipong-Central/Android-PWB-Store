package cenergy.central.com.pwb_store.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.Contextor;
import cenergy.central.com.pwb_store.model.SearchSuggestionHeader;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 8/9/2017 AD.
 */

public class SearchSuggestionHeaderViewHolder extends RecyclerView.ViewHolder {

    public enum SearchSuggestionHeaderType {
        SEARCH_RESULT,
        DID_YOU_MEAN,
        PRODUCT_SUGGESTION_RESULT,
        PRODUCT_RESULT
    }

    @BindView(R.id.txt_view_search_header)
    PowerBuyTextView mTextViewSearchHeader;

    @BindView(R.id.txt_view_search_query)
    PowerBuyTextView mTextViewSearchQuery;

    public SearchSuggestionHeaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setViewHolder(SearchSuggestionHeader searchSuggestionHeader) {
        switch (searchSuggestionHeader.getSearchSuggestionHeaderType()) {
            case SEARCH_RESULT:
                mTextViewSearchHeader.setText(Contextor.getInstance().getContext().getString(R.string.search_result_header));
                //mTextViewMore.setVisibility(View.GONE);
                break;
            case DID_YOU_MEAN:
                mTextViewSearchHeader.setText(Contextor.getInstance().getContext().getString(R.string.search_result_header));
                //mTextViewMore.setVisibility(View.GONE);
                break;
            case PRODUCT_SUGGESTION_RESULT:
                mTextViewSearchHeader.setText(Contextor.getInstance().getContext().getString(R.string.search_result_header));
                //mTextViewMore.setVisibility(View.GONE);
                break;
            case PRODUCT_RESULT:
                mTextViewSearchHeader.setText(Contextor.getInstance().getContext().getString(R.string.search_result_header));
                //mTextViewMore.setVisibility(View.VISIBLE);
                break;
        }

        mTextViewSearchQuery.setText(searchSuggestionHeader.getSearchQuery());
    }
}
