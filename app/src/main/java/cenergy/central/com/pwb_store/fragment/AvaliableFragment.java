package cenergy.central.com.pwb_store.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.AvaliableStoreAdapter;
import cenergy.central.com.pwb_store.adapter.decoration.SpacesItemDecoration;
import cenergy.central.com.pwb_store.manager.Contextor;
import cenergy.central.com.pwb_store.manager.bus.event.StoreFilterHeaderBus;
import cenergy.central.com.pwb_store.model.AvaliableStoreDao;
import cenergy.central.com.pwb_store.model.StoreDao;
import cenergy.central.com.pwb_store.model.StoreFilterHeader;
import cenergy.central.com.pwb_store.model.StoreFilterItem;
import cenergy.central.com.pwb_store.model.StoreFilterList;
import cenergy.central.com.pwb_store.utils.DialogUtils;
import cenergy.central.com.pwb_store.view.PowerBuyPopupWindow;

/**
 * Created by napabhat on 7/6/2017 AD.
 */

public class AvaliableFragment extends Fragment {
    public static final String ARG_AVALIABLE_DAO = "ARG_AVALIABLEDAO";
    public static final String ARG_STORE_DAO = "ARG_STORE_DAO";

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.layout_store)
    LinearLayout mLinearLayoutStore;

    //Data Member
    private AvaliableStoreAdapter mAdapter;
    private AvaliableStoreDao mAvaliableStoreDao;
    private StoreFilterList mStoreFilterList;
    private StoreFilterList mTempStoreFilterList;
    private GridLayoutManager mLayoutManager;
    private PowerBuyPopupWindow mPowerBuyPopupWindow;
    private boolean isDoneFilter;
    private StoreDao mStoreDao;
    private ProgressDialog mProgressDialog;

    public AvaliableFragment() {
        super();
    }

    final PowerBuyPopupWindow.OnDismissListener ON_POPUP_DISMISS_LISTENER = new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            if (!isDoneFilter) {
                mStoreFilterList = mTempStoreFilterList;
            }
            if (mStoreFilterList != null) {
                for (StoreFilterHeader storeFilterHeader : mStoreFilterList.getStoreFilterHeaders()) {
                    storeFilterHeader.setExpanded(false);
                }
            }
            isDoneFilter = false;
        }
    };

    @Subscribe
    public void onEvent(StoreFilterHeaderBus storeFilterHeaderBus) {
        mPowerBuyPopupWindow.setStoreItem(storeFilterHeaderBus.getStoreFilterHeader());
    }


    @SuppressWarnings("unused")
    public static AvaliableFragment newInstance(AvaliableStoreDao avaliableStoreDao, StoreDao storeDao) {
        AvaliableFragment fragment = new AvaliableFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_AVALIABLE_DAO, avaliableStoreDao);
        args.putParcelable(ARG_STORE_DAO, storeDao);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_avaliable, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here

        if (getArguments() != null) {
            mAvaliableStoreDao = getArguments().getParcelable(ARG_AVALIABLE_DAO);
            mStoreDao = getArguments().getParcelable(ARG_STORE_DAO);
        }
//        List<AvaliableStoreItem> avaliableStoreItems = new ArrayList<>();
//        avaliableStoreItems.add(new AvaliableStoreItem("Rama 3","79/3 Sathupadit Chong Non Si Yannawa Bangkok 10120\n" +
//                "Call : 0-2673-6140, 0-2673-6147", 2));
//        avaliableStoreItems.add(new AvaliableStoreItem("Rama 9","79/3 9/9 (Room 427 FR.4) Central Plaza Rama9 Rama9 Huai Khwang Huaikhwang Bangkok 10310\n" +
//                "Call : 02-103-4983-4", 10));
//        avaliableStoreItems.add(new AvaliableStoreItem("Central Festival Samui","7209,209/1,209/2 Moo 2 (Room no.149,257 FL.1-2) Bo Phut Ko Samui Surat Thani 84320\n" +
//                "Call : 077-963 400-6", 4));
//        avaliableStoreItems.add(new AvaliableStoreItem("Central World","4,4/1-2,4/4 Central World FL.4 Zone D Rajadamri Pathumwan  Bangkok 10330\n" +
//                "Call : 0-2646-1222, 0-2646-1243", 8));
//        avaliableStoreItems.add(new AvaliableStoreItem("Bangna","585 Bangna-Trad Bangna Bangna Bangkok 10260\n" +
//                "Call : 0-2361-1060-2", 8));
//        avaliableStoreItems.add(new AvaliableStoreItem("Rama 2","7160 Central Plaza Rama 2 4th.,FL. Rama 2 Samae Dam Bangkhunthian Bangkok 10150\n" +
//                "Call : 0-2361-1060-2", 8));
//        avaliableStoreItems.add(new AvaliableStoreItem("Central Festival Pattaya Beach","333/99 Moo9,(Room No.336-337) Central Festival Pattaya Beach 3Fl. Nong Prue Bang Lamung Chonburi 20150\n" +
//                "Call : 038-043-303, 038-043-304", 4));

//        mAvaliableStoreDao = new AvaliableStoreDao(avaliableStoreItems);

        // filter
        List<StoreFilterItem> storeFilterItems1 = new ArrayList<>();
        storeFilterItems1.add(new StoreFilterItem(1, "Rama 2", "rama_2", "single", "", false));
        storeFilterItems1.add(new StoreFilterItem(1, "Rama 3", "rama_3", "single", "", false));
        storeFilterItems1.add(new StoreFilterItem(1, "Rama 9", "rama_9", "single", "", false));
        storeFilterItems1.add(new StoreFilterItem(1, "Chidlom", "chidlom", "single", "", false));
        storeFilterItems1.add(new StoreFilterItem(1, "Lardprao", "lardprao", "single", "", false));

        List<StoreFilterItem> storeFilterItems2 = new ArrayList<>();
        storeFilterItems2.add(new StoreFilterItem(2, "ห้างสรรพสินค้าโรบินสัน สาขาบางรัก", "tablet_10", "single", "", false));
        storeFilterItems2.add(new StoreFilterItem(2, "ห้างสรรพสินค้าโรบินสัน สาขาบางแค", "iphone", "single", "", false));
        storeFilterItems2.add(new StoreFilterItem(2, "ห้างสรรพสินค้าโรบินสัน สาขาบางนา", "samsung", "single", "", false));
        storeFilterItems2.add(new StoreFilterItem(2, "ห้างสรรพสินค้าโรบินสัน สาขารัตนาธิเบศก์", "oppo", "single", "", false));
        storeFilterItems2.add(new StoreFilterItem(2, "ห้างสรรพสินค้าโรบินสัน สาขาซีคอนสแควร์ (ศรีนครินทร์)", "sony", "single", "", false));

        List<StoreFilterItem> storeFilterItems3 = new ArrayList<>();
        storeFilterItems3.add(new StoreFilterItem(3, "วงศ์สว่าง", "tablet_10", "single", "", false));
        storeFilterItems3.add(new StoreFilterItem(3, "บางพลี", "iphone", "single", "", false));
        storeFilterItems3.add(new StoreFilterItem(3, "สมุทรปราการ", "samsung", "single", "", false));
        storeFilterItems3.add(new StoreFilterItem(3, "สุขสวัสดิ์", "oppo", "single", "", false));
        storeFilterItems3.add(new StoreFilterItem(3, "สุขาภิบาล3", "sony", "single", "", false));

        List<StoreFilterItem> storeFilterItems4 = new ArrayList<>();
        storeFilterItems4.add(new StoreFilterItem(4, "มาบุญครอง", "tablet_10", "single", "", false));
        storeFilterItems4.add(new StoreFilterItem(4, "แหลมทองระยอง", "iphone", "single", "", false));
        storeFilterItems4.add(new StoreFilterItem(4, "จังซีลอน", "samsung", "single", "", false));
        storeFilterItems4.add(new StoreFilterItem(4, "ตึกคอม ขอนแก่น", "oppo", "single", "", false));
        storeFilterItems4.add(new StoreFilterItem(4, "พัทยา ตึกคอม", "sony", "single", "", false));

        List<StoreFilterItem> storeFilterItems5 = new ArrayList<>();
        storeFilterItems5.add(new StoreFilterItem(5, "ตึกคอม ขอนแก่น", "tablet_10", "single", "", false));
        storeFilterItems5.add(new StoreFilterItem(5, "บิ๊กซีโคราช", "iphone", "single", "", false));
        storeFilterItems5.add(new StoreFilterItem(5, "เซ็นทรัลพลาซา อุดรธานี", "samsung", "single", "", false));
        storeFilterItems5.add(new StoreFilterItem(5, "สบุรีรัมย์ ห้างทวีกิจ", "oppo", "single", "", false));
        storeFilterItems5.add(new StoreFilterItem(5, "บิ๊กซีอุดรธานี", "sony", "single", "", false));

        List<StoreFilterItem> storeFilterItems6 = new ArrayList<>();
        storeFilterItems6.add(new StoreFilterItem(6, "โรบินสัน ศรีราชา", "tablet_10", "single", "", false));
        storeFilterItems6.add(new StoreFilterItem(6, "โฮมเวิร์คพัทยา", "iphone", "single", "", false));
        storeFilterItems6.add(new StoreFilterItem(6, "โรบินสัน จันทบุรี", "samsung", "single", "", false));
        storeFilterItems6.add(new StoreFilterItem(6, "บิ๊กซี ระยอง", "oppo", "single", "", false));
        storeFilterItems6.add(new StoreFilterItem(6, "พัทยา ตึกคอม", "sony", "single", "", false));

        List<StoreFilterItem> storeFilterItems7 = new ArrayList<>();
        storeFilterItems7.add(new StoreFilterItem(7, "สีลมคอมเพล็กซ์", "tablet_10", "single", "", false));
        storeFilterItems7.add(new StoreFilterItem(7, "สาขาเซ็นทรัล ชิดลม", "iphone", "single", "", false));
        storeFilterItems3.add(new StoreFilterItem(7, "สาขาเซ็นทรัล ปิ่นเกล้า", "samsung", "single", "", false));
        storeFilterItems3.add(new StoreFilterItem(7, "สาขาเซ็นทรัล ลาดพร้าว", "oppo", "single", "", false));
        storeFilterItems3.add(new StoreFilterItem(7, "ซ็นทรัล พระราม 3", "sony", "single", "", false));

        List<StoreFilterItem> storeFilterItems8 = new ArrayList<>();
        storeFilterItems8.add(new StoreFilterItem(8, "โรบินสัน หาดใหญ่", "tablet_10", "single", "", false));
        storeFilterItems8.add(new StoreFilterItem(8, "สาขาเซ็นทรัลเฟสติวัล ภูเก็ต", "iphone", "single", "", false));
        storeFilterItems8.add(new StoreFilterItem(8, "จังซีลอน", "samsung", "single", "", false));
        storeFilterItems8.add(new StoreFilterItem(8, "โฮมเวิร์คภูเก็ต", "oppo", "single", "", false));
        storeFilterItems8.add(new StoreFilterItem(8, "สโรบินสัน ตรัง", "sony", "single", "", false));

        List<StoreFilterHeader> storeFilterHeaders = new ArrayList<>();
        storeFilterHeaders.add(new StoreFilterHeader("1", "Central Department Store", "central_department_store", "multiple", storeFilterItems1));
        storeFilterHeaders.add(new StoreFilterHeader("2", "Robinson Department Store", "robinson_department_store", "multiple", storeFilterItems2));
        storeFilterHeaders.add(new StoreFilterHeader("3", "Big C Supercenter", "big_c_supercenter", "multiple", storeFilterItems3));
        storeFilterHeaders.add(new StoreFilterHeader("4", "Other", "other", "multiple", storeFilterItems4));
        storeFilterHeaders.add(new StoreFilterHeader("5", "North Eastern Area", "north_eastern_area", "multiple", storeFilterItems5));
        storeFilterHeaders.add(new StoreFilterHeader("6", "Eastern Area", "eastern_area", "multiple", storeFilterItems6));
        storeFilterHeaders.add(new StoreFilterHeader("7", "Central Area", "central_area", "multiple", storeFilterItems7));
        storeFilterHeaders.add(new StoreFilterHeader("8", "Soutern Area", "soutern_area", "multiple", storeFilterItems8));

        mStoreFilterList = new StoreFilterList(storeFilterHeaders);

    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        ButterKnife.bind(this, rootView);
        popUpShow();
        mAdapter = new AvaliableStoreAdapter(getContext(), mStoreDao);
        mLayoutManager = new GridLayoutManager(getContext(), 4, LinearLayoutManager.VERTICAL, false);
        mLayoutManager.setSpanSizeLookup(mAdapter.getSpanSize());
        mAdapter.setCompareAvaliable(mAvaliableStoreDao);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(0, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);
        super.onDetach();
    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
    }

    /*
     * Restore Instance State Here
     */
    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
    }

    @OnClick(R.id.layout_store)
    public void onStoreClick(LinearLayout linearLayout) {
        if (mStoreFilterList == null) {
            mPowerBuyPopupWindow.dismiss();
        } else {
            mTempStoreFilterList = new StoreFilterList(mStoreFilterList);
            mPowerBuyPopupWindow.setRecyclerViewStore(mStoreFilterList);
            mPowerBuyPopupWindow.showAsDropDown(linearLayout);
            mTempStoreFilterList = new StoreFilterList(mStoreFilterList);
        }
    }

    private void popUpShow() {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPowerBuyPopupWindow = new PowerBuyPopupWindow(getActivity(), layoutInflater);
        mPowerBuyPopupWindow.setOnDismissListener(ON_POPUP_DISMISS_LISTENER);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtils.createProgressDialog(Contextor.getInstance().getContext());
            mProgressDialog.show();
        } else {
            mProgressDialog.show();
        }
    }
}
