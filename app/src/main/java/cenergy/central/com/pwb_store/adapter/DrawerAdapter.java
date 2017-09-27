package cenergy.central.com.pwb_store.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.viewholder.DrawerChangeViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.DrawerCompareViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.DrawerDeliveryViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.DrawerHeaderViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.DrawerHelpViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.DrawerItemViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.DrawerUserViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.StoreSelectedViewHolder;
import cenergy.central.com.pwb_store.model.Category;
import cenergy.central.com.pwb_store.model.CategoryDao;
import cenergy.central.com.pwb_store.model.DrawerDao;
import cenergy.central.com.pwb_store.model.DrawerItem;
import cenergy.central.com.pwb_store.model.IViewType;
import cenergy.central.com.pwb_store.model.StoreDao;
import cenergy.central.com.pwb_store.model.StoreList;
import cenergy.central.com.pwb_store.model.ViewType;

/**
 * Created by napabhat on 6/29/2017 AD.
 */

public class DrawerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    //Static members
    private static final int VIEW_TYPE_ID_HEADER = 0;
    private static final int VIEW_TYPE_ID_ITEM = 1;
    private static final int VIEW_TYPE_ID_USER = 2;
    private static final int VIEW_TYPE_ID_DELIVERY = 3;
    private static final int VIEW_TYPE_ID_COMPARE = 4;
    private static final int VIEW_TYPE_ID_CHANGE = 5;
    private static final int VIEW_TYPE_ID_HELP = 6;

    private static final ViewType VIEW_TYPE_HEADER = new ViewType(VIEW_TYPE_ID_HEADER);
    private static final ViewType VIEW_TYPE_DELIVERY = new ViewType(VIEW_TYPE_ID_DELIVERY);
    private static final ViewType VIEW_TYPE_COMPARE = new ViewType(VIEW_TYPE_ID_COMPARE);
    private static final ViewType VIEW_TYPE_CHANGE = new ViewType(VIEW_TYPE_ID_CHANGE);
    private static final ViewType VIEW_TYPE_HELP = new ViewType(VIEW_TYPE_ID_HELP);

    private Context mContext;
    private List<IViewType> mListViewType = new ArrayList<>();

    public DrawerAdapter(Context context) {
        this.mContext = context;
    }

        @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ID_HEADER:
                return new DrawerHeaderViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.drawer_header, parent, false)
                );

            case VIEW_TYPE_ID_ITEM:
                return new DrawerItemViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.drawer_item, parent, false)
                );

            case VIEW_TYPE_ID_USER:
                return new DrawerUserViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.drawer_user, parent, false)
                );

            case VIEW_TYPE_ID_DELIVERY:
                return new DrawerDeliveryViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.drawer_delivery, parent, false)
                );

            case VIEW_TYPE_ID_COMPARE:
                return new DrawerCompareViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.drawer_compare, parent, false)
                );

            case VIEW_TYPE_ID_CHANGE:
                return new DrawerChangeViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.drawer_change, parent, false)
                );

            case VIEW_TYPE_ID_HELP:
                return new DrawerHelpViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.drawer_help, parent, false)
                );
        }
        return null;
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewTypeId = getItemViewType(position);
        IViewType viewType = mListViewType.get(position);
        switch (viewTypeId) {
            case VIEW_TYPE_ID_HEADER:
                if (holder instanceof DrawerHeaderViewHolder) {
                    DrawerHeaderViewHolder drawerHeaderViewHolder = (DrawerHeaderViewHolder) holder;
                    drawerHeaderViewHolder.setViewHolder();
                }
                break;

            case VIEW_TYPE_ID_ITEM:
                if (viewType instanceof DrawerItem && holder instanceof DrawerItemViewHolder) {
                    DrawerItem drawerItem = (DrawerItem) viewType;
                    DrawerItemViewHolder drawerItemViewHolder = (DrawerItemViewHolder) holder;
                    drawerItemViewHolder.setViewHolder(drawerItem);
                }
                break;

            case VIEW_TYPE_ID_USER:
                if (viewType instanceof StoreDao && holder instanceof DrawerUserViewHolder){
                    StoreDao storeDao = (StoreDao) viewType;
                    DrawerUserViewHolder drawerUserViewHolder = (DrawerUserViewHolder) holder;
                    drawerUserViewHolder.setViewHolder(mContext, storeDao);
                }
                break;
        }
    }

    public void setStore(StoreDao storeDao){
        storeDao.setViewTypeId(VIEW_TYPE_ID_USER);
        mListViewType.add(storeDao);
    }

//    public void setDrawItem(ArrayList<DrawerItem> drawItem){
//
//        mListViewType.add(VIEW_TYPE_HEADER);
//
//        for (DrawerItem drawerItem : drawItem){
//            drawerItem.setViewTypeId(VIEW_TYPE_ID_ITEM);
//            mListViewType.add(drawerItem);
//        }
//
//        notifyDataSetChanged();
//    }

    public void setDrawItem(DrawerDao drawerDao){

        setStore(drawerDao.getStoreDao());

        mListViewType.add(VIEW_TYPE_HEADER);

        for (DrawerItem drawerItem : drawerDao.getDrawerItems()){
            drawerItem.setViewTypeId(VIEW_TYPE_ID_ITEM);
            mListViewType.add(drawerItem);
        }

        mListViewType.add(VIEW_TYPE_DELIVERY);
        mListViewType.add(VIEW_TYPE_COMPARE);
        mListViewType.add(VIEW_TYPE_CHANGE);
        mListViewType.add(VIEW_TYPE_HELP);

        notifyDataSetChanged();
    }

//    public void setDrawItem(CategoryDao categoryDao){
////        mListViewType.clear();
////        mListViewType.add(VIEW_TYPE_HEADER);
//        for (Category category : categoryDao.getCategoryList()) {
//            category.setViewTypeId(VIEW_TYPE_ID_ITEM);
//            mListViewType.add(category);
//        }
//        notifyDataSetChanged();
//    }
}
