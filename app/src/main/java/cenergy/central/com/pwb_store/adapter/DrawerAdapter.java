package cenergy.central.com.pwb_store.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.interfaces.MenuDrawerClickListener;
import cenergy.central.com.pwb_store.adapter.viewholder.DrawerChangeViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.DrawerDeliveryViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.DrawerHeaderViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.DrawerHelpViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.DrawerItemViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.DrawerSubHeaderViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.DrawerUserNewViewHolder;
import cenergy.central.com.pwb_store.manager.bus.event.CompareMenuBus;
import cenergy.central.com.pwb_store.model.DrawerDao;
import cenergy.central.com.pwb_store.model.DrawerItem;
import cenergy.central.com.pwb_store.model.IViewType;
import cenergy.central.com.pwb_store.model.ViewType;

/**
 * Created by napabhat on 6/29/2017 AD.
 */

public class DrawerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //Static members
    private static final int VIEW_TYPE_ID_HEADER = 0;
    private static final int VIEW_TYPE_ID_ITEM = 1;
    private static final int VIEW_TYPE_ID_USER = 2;
    private static final int VIEW_TYPE_ID_DELIVERY = 3;
    private static final int VIEW_TYPE_ID_COMPARE = 4;
    private static final int VIEW_TYPE_ID_CHANGE = 5;
    private static final int VIEW_TYPE_ID_HELP = 6;
    private static final int VIEW_TYPE_ID_CART = 7;
    private static final int VIEW_TYPE_ID_HISTORY = 8;
    private static final int VIEW_TYPE_ID_LOGOUT = 9;

    private static final ViewType VIEW_TYPE_USER = new ViewType(VIEW_TYPE_ID_USER);
    private static final ViewType VIEW_TYPE_HEADER = new ViewType(VIEW_TYPE_ID_HEADER);
//    private static final ViewType VIEW_TYPE_DELIVERY = new ViewType(VIEW_TYPE_ID_DELIVERY);
    private static final ViewType VIEW_TYPE_COMPARE = new ViewType(VIEW_TYPE_ID_COMPARE);
//    private static final ViewType VIEW_TYPE_CHANGE = new ViewType(VIEW_TYPE_ID_CHANGE);
//    private static final ViewType VIEW_TYPE_HELP = new ViewType(VIEW_TYPE_ID_HELP);
    private static final ViewType VIEW_TYPE_CART = new ViewType(VIEW_TYPE_ID_CART);
    private static final ViewType VIEW_TYPE_HISTORY = new ViewType(VIEW_TYPE_ID_HISTORY);
    private static final ViewType VIEW_TYPE_LOGOUT = new ViewType(VIEW_TYPE_ID_LOGOUT);

    private Context mContext;
    private MenuDrawerClickListener listener;
    private List<IViewType> mListViewType = new ArrayList<>();

    public DrawerAdapter(Context context) {
        this.mContext = context;
        this.listener = (MenuDrawerClickListener) context ;
    }

    public void clearItems() {
        this.mListViewType.clear();
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
                return new DrawerUserNewViewHolder(
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

            case VIEW_TYPE_ID_LOGOUT:

            case VIEW_TYPE_ID_CART:

            case VIEW_TYPE_ID_HISTORY:
                return new DrawerSubHeaderViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.drawer_sub_header, parent, false)
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
                if (holder instanceof DrawerUserNewViewHolder) {
                    DrawerUserNewViewHolder drawerUserViewHolder = (DrawerUserNewViewHolder) holder;
                    drawerUserViewHolder.setViewHolder();
                }
                break;

            case VIEW_TYPE_ID_DELIVERY:
                if (holder instanceof DrawerDeliveryViewHolder) {
                    DrawerDeliveryViewHolder drawerDeliveryViewHolder = (DrawerDeliveryViewHolder) holder;
                    drawerDeliveryViewHolder.setViewHolder();
                }
                break;

            case VIEW_TYPE_ID_COMPARE:
                if (holder instanceof DrawerSubHeaderViewHolder) {
                    DrawerSubHeaderViewHolder drawerSubHeaderViewHolder = (DrawerSubHeaderViewHolder) holder;
                    drawerSubHeaderViewHolder.bindItem(mContext.getString(R.string.drawer_compare));
                    holder.itemView.setOnClickListener(v -> EventBus.getDefault().post(new CompareMenuBus(v)));
                }
                break;

            case VIEW_TYPE_ID_CART:
                if (holder instanceof DrawerSubHeaderViewHolder) {
                    DrawerSubHeaderViewHolder drawerSubHeaderViewHolder = (DrawerSubHeaderViewHolder) holder;
                    drawerSubHeaderViewHolder.bindItem(mContext.getString(R.string.drawer_cart));
                    holder.itemView.setOnClickListener(v -> listener.onMenuClickedItem(DrawerAction.ACTION_CART));
                }
                break;

            case VIEW_TYPE_ID_HISTORY:
                if (holder instanceof DrawerSubHeaderViewHolder) {
                    DrawerSubHeaderViewHolder drawerSubHeaderViewHolder = (DrawerSubHeaderViewHolder) holder;
                    drawerSubHeaderViewHolder.bindItem(mContext.getString(R.string.drawer_history));
                    holder.itemView.setOnClickListener(v -> listener.onMenuClickedItem(DrawerAction.ACTION_HISTORY));
                }
                break;

            case VIEW_TYPE_ID_CHANGE:
                if (holder instanceof DrawerChangeViewHolder) {
                    DrawerChangeViewHolder drawerChangeViewHolder = (DrawerChangeViewHolder) holder;
                    drawerChangeViewHolder.setViewHolder();
                }
                break;

            case VIEW_TYPE_ID_HELP:
                if (holder instanceof DrawerHelpViewHolder) {
                    DrawerHelpViewHolder drawerHelpViewHolder = (DrawerHelpViewHolder) holder;
                    drawerHelpViewHolder.setViewHolder();
                }
                break;

            case VIEW_TYPE_ID_LOGOUT:
                if (holder instanceof DrawerSubHeaderViewHolder) {
                    DrawerSubHeaderViewHolder drawerSubHeaderViewHolder = (DrawerSubHeaderViewHolder) holder;
                    drawerSubHeaderViewHolder.bindItem(mContext.getString(R.string.drawer_logout));
                    holder.itemView.setOnClickListener(v -> listener.onMenuClickedItem(DrawerAction.ACTION_LOGOUT));
                }
                break;
        }
    }

    public void setDrawItem(DrawerDao drawerDao) {
        mListViewType.add(VIEW_TYPE_USER);
        mListViewType.add(VIEW_TYPE_HEADER);

        for (DrawerItem drawerItem : drawerDao.getDrawerItems()) {
            drawerItem.setViewTypeId(VIEW_TYPE_ID_ITEM);
            mListViewType.add(drawerItem);
        }

        mListViewType.add(VIEW_TYPE_COMPARE);
        mListViewType.add(VIEW_TYPE_CART);
        mListViewType.add(VIEW_TYPE_HISTORY);
        mListViewType.add(VIEW_TYPE_LOGOUT);

        notifyDataSetChanged();
    }

    //region enum
    public enum DrawerAction {
        ACTION_CART, ACTION_HISTORY, ACTION_LOGOUT
    }
    //region enum
}
