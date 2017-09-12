package cenergy.central.com.pwb_store.adapter.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.StoreAdapter;
import cenergy.central.com.pwb_store.manager.UserInfoManager;
import cenergy.central.com.pwb_store.model.StoreDao;
import cenergy.central.com.pwb_store.model.StoreList;
import cenergy.central.com.pwb_store.view.PowerBuyListDialog;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 9/6/2017 AD.
 */
public class DrawerUserViewHolder extends RecyclerView.ViewHolder{

    private static final String TAG = DrawerUserViewHolder.class.getSimpleName();

    @BindView(R.id.image_view_profile)
    ImageView imgProfile;

    @BindView(R.id.txt_view_full_name)
    PowerBuyTextView fullName;

    @BindView(R.id.txt_store)
    PowerBuyTextView storeName;

    private Context mContext;
    private PowerBuyListDialog mPowerBuyListDialog;
    private StoreDao mStoreDao;
    private StoreAdapter storeAdapter;

    public DrawerUserViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setViewHolder(Context context, StoreDao storeDao){
        this.mContext = context;
        this.mStoreDao = storeDao;
        storeAdapter = new StoreAdapter(mContext);
        String storeId = UserInfoManager.getInstance().getUserId();
        for (StoreList storeList : storeDao.getStoreLists()){
            if (storeList.getStoreId().equalsIgnoreCase(storeId)){
                storeName.setText(storeList.getStoreNameEN());
            }
        }
        //storeName.setOnClickListener(this);
        storeName.setTag(storeDao);
    }

    //Listener
    final PowerBuyListDialog.OnItemClickListener onClickStore = new PowerBuyListDialog.OnItemClickListener() {
        @Override
        public void onItemClick(Object object) {
            if (object instanceof StoreList) {
                StoreList storeList = (StoreList) object;
                if (!mStoreDao.isSelectedStoreTheSame(storeList)) {
                        mStoreDao.setSelectStore(storeList);
                        storeName.setText(storeList.getStoreNameEN());
                        UserInfoManager.getInstance().setUserIdLogin(storeList.getStoreId());
                        storeAdapter.updateSingleStore(storeList);
                }
            }else {
                Log.e(TAG, "onItemClick: No matching type");
            }

        }
    };

    @OnClick(R.id.txt_store)
    protected void onStoreClick(PowerBuyTextView powerBuyTextView) {
        storeAdapter.setStore(mStoreDao);

        mPowerBuyListDialog = new PowerBuyListDialog(powerBuyTextView.getContext(), storeAdapter);
        mPowerBuyListDialog.setOnItemClickListener(onClickStore);
        mPowerBuyListDialog.show();
    }

}
