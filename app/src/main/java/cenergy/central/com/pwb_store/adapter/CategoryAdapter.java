package cenergy.central.com.pwb_store.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.viewholder.CategoryFullFillerViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.CategoryViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.SearchProductViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.TextBannerViewHolder;
import cenergy.central.com.pwb_store.model.Category;
import cenergy.central.com.pwb_store.model.CategoryDao;
import cenergy.central.com.pwb_store.model.IViewType;
import cenergy.central.com.pwb_store.model.TextBanner;
import cenergy.central.com.pwb_store.model.ViewType;

/**
 * Created by napabhat on 7/5/2017 AD.
 */

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //Static Members
    private static final int VIEW_TYPE_ID_CATEGORY = 0;
    private static final int VIEW_TYPE_ID_TEXT_BANNER = 1;
    private static final int VIEW_TYPE_ID_FULL_FILLER = 2;
    private static final int VIEW_TYPE_ID_SEARCH = 3;
    private static final int VIEW_TYPE_ID_LOADING = 4;

    private static final ViewType VIEW_TYPE_FULL_FILLER = new ViewType(VIEW_TYPE_ID_FULL_FILLER);
    private static final ViewType VIEW_TYPE_SEARCH = new ViewType(VIEW_TYPE_ID_SEARCH);
    private static final ViewType VIEW_TYPE_LOADING = new ViewType(VIEW_TYPE_ID_LOADING);

    //Data Members
    private Context mContext;
    private List<IViewType> mListViewType = new ArrayList<>();
    private final GridLayoutManager.SpanSizeLookup mSpanSize = new GridLayoutManager.SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
            switch (getItemViewType(position)) {
                case VIEW_TYPE_ID_CATEGORY:
                    return 1;
                case VIEW_TYPE_ID_FULL_FILLER:
                    return 1;
                case VIEW_TYPE_ID_TEXT_BANNER:
                    return 3;
                case VIEW_TYPE_ID_SEARCH:
                    return 3;
//                case VIEW_TYPE_ID_LOADING:
//                    return 4;
                default:
                    return 1;
            }
        }
    };

    public CategoryAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ID_CATEGORY:
                return new CategoryViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_category, parent, false)
                );
            case VIEW_TYPE_ID_TEXT_BANNER:
                return new TextBannerViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_text_banner, parent, false)
                );
            case VIEW_TYPE_ID_FULL_FILLER:
                return new CategoryFullFillerViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_category_fullfiller, parent, false)
                );
            case VIEW_TYPE_ID_SEARCH:
                return new SearchProductViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_search, parent, false)
                );
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewTypeId = getItemViewType(position);
        IViewType viewType = mListViewType.get(position);
        switch (viewTypeId) {
            case VIEW_TYPE_ID_CATEGORY:
                if (viewType instanceof Category && holder instanceof CategoryViewHolder) {
                    Category category = (Category) viewType;

                    Log.d("TESTEST", category.getId());
                    CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;
                    categoryViewHolder.setViewHolder(category);
                }
                break;

            case VIEW_TYPE_ID_TEXT_BANNER:
            if (viewType instanceof TextBanner && holder instanceof TextBannerViewHolder) {
                TextBanner textBanner = (TextBanner) viewType;
                TextBannerViewHolder textBannerViewHolder = (TextBannerViewHolder) holder;
                textBannerViewHolder.setViewHolder(textBanner.getTitle(), false);
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

    public void setCategory(CategoryDao categoryDao) {

        //mListViewType.clear();
        mListViewType.add(VIEW_TYPE_SEARCH);
        TextBanner textBanner = new TextBanner(mContext.getResources().getString(R.string.category));
        textBanner.setViewTypeId(VIEW_TYPE_ID_TEXT_BANNER);
        mListViewType.add(textBanner);

        //int startPosition = mListViewType.size();
        for (Category category : categoryDao.getCategoryList()) {
            category.setViewTypeId(VIEW_TYPE_ID_CATEGORY);
            mListViewType.add(category);
        }

        if (categoryDao.getCategoryList().size() % 3 != 0) {
            int fullFillerNo = 3 - categoryDao.getCategoryList().size() % 3;
            for (int i = 0; i < fullFillerNo; i++) {
                mListViewType.add(VIEW_TYPE_FULL_FILLER);
            }
        }

        notifyDataSetChanged();

    }

    public GridLayoutManager.SpanSizeLookup getSpanSize() {
        return mSpanSize;
    }
}
