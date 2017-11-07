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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.CompareProductAdapter;
import cenergy.central.com.pwb_store.adapter.decoration.SpacesItemDecoration;
import cenergy.central.com.pwb_store.manager.bus.event.CompareDeleteBus;
import cenergy.central.com.pwb_store.model.AddCompare;
import cenergy.central.com.pwb_store.model.CompareDao;
import cenergy.central.com.pwb_store.model.CompareDetail;
import cenergy.central.com.pwb_store.model.CompareDetailItem;
import cenergy.central.com.pwb_store.model.CompareList;
import cenergy.central.com.pwb_store.model.ProductList;
import cenergy.central.com.pwb_store.realm.RealmController;
import cenergy.central.com.pwb_store.utils.DialogUtils;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by napabhat on 7/6/2017 AD.
 */

public class CompareFragment extends Fragment {
    private static final String TAG = CompareFragment.class.getSimpleName();

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    //Data Member
    private CompareProductAdapter mAdapter;
    private GridLayoutManager mLayoutManager;
    private CompareList mCompareList;
    private CompareDao mCompareDao;

    private Realm mRealm;
    private RealmResults<AddCompare> results;
    private ProgressDialog mProgressDialog;

    public CompareFragment() {
        super();
    }

    @Subscribe
    public void onEvent(CompareDeleteBus compareDeleteBus){
        ProductList productList = compareDeleteBus.getProductList();
        results = RealmController.with(this).deletedCompare(productList.getProductId());
        List<ProductList> mProductListList = new ArrayList<>();
        for (AddCompare addCompare : results){
            mProductListList.add(new ProductList(addCompare.getProductId(), addCompare.getUrlName(), addCompare.getProductName(),
                    addCompare.getDescription(), addCompare.getOriginalPrice(), addCompare.getPrice()));
        }

        List<CompareDetailItem> compareDetailItems1 = new ArrayList<>();
        compareDetailItems1.add(new CompareDetailItem(""));
        compareDetailItems1.add(new CompareDetailItem(""));
        compareDetailItems1.add(new CompareDetailItem(""));
        compareDetailItems1.add(new CompareDetailItem(""));

        List<CompareDetailItem> compareDetailItems2 = new ArrayList<>();
        compareDetailItems2.add(new CompareDetailItem(""));
        compareDetailItems2.add(new CompareDetailItem(""));
        compareDetailItems2.add(new CompareDetailItem(""));
        compareDetailItems2.add(new CompareDetailItem(""));

        List<CompareDetailItem> compareDetailItems3 = new ArrayList<>();
        compareDetailItems3.add(new CompareDetailItem(""));
        compareDetailItems3.add(new CompareDetailItem(""));
        compareDetailItems3.add(new CompareDetailItem(""));
        compareDetailItems3.add(new CompareDetailItem(""));

        List<CompareDetailItem> compareDetailItems4 = new ArrayList<>();
        compareDetailItems4.add(new CompareDetailItem(""));
        compareDetailItems4.add(new CompareDetailItem(""));
        compareDetailItems4.add(new CompareDetailItem(""));
        compareDetailItems4.add(new CompareDetailItem(""));

        List<CompareDetailItem> compareDetailItems5 = new ArrayList<>();
        compareDetailItems5.add(new CompareDetailItem(""));
        compareDetailItems5.add(new CompareDetailItem(""));
        compareDetailItems5.add(new CompareDetailItem(""));
        compareDetailItems5.add(new CompareDetailItem(""));

        List<CompareDetail> compareDetails = new ArrayList<>();
        compareDetails.add(new CompareDetail("", compareDetailItems1));
        compareDetails.add(new CompareDetail("", compareDetailItems2));
        compareDetails.add(new CompareDetail("", compareDetailItems3));
        compareDetails.add(new CompareDetail("", compareDetailItems4));
        compareDetails.add(new CompareDetail("", compareDetailItems5));

        mCompareDao = new CompareDao(5, compareDetails);
        mCompareList = new CompareList(4, mProductListList, mCompareDao);

        mAdapter.setUpdateCompare(mCompareList);

    }

    @SuppressWarnings("unused")
    public static CompareFragment newInstance() {
        CompareFragment fragment = new CompareFragment();
        Bundle args = new Bundle();
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
        View rootView = inflater.inflate(R.layout.fragment_compare, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        //get realm instance
        this.mRealm = RealmController.with(this).getRealm();
        mockData();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        ButterKnife.bind(this, rootView);

        mAdapter = new CompareProductAdapter(getContext());
        mLayoutManager = new GridLayoutManager(getContext(), 4, LinearLayoutManager.VERTICAL, false);
        mLayoutManager.setSpanSizeLookup(mAdapter.getSpanSize());
        mAdapter.setCompare(mCompareList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(0, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);

    }

    private void mockData(){

        results = RealmController.with(this).getCompares();
        if (results == null){
            List<ProductList> mProductListList = new ArrayList<>();
            mProductListList.add(new ProductList("1111","http://www.mx7.com/i/004/aOc9VL.png","iPhone SE","หัวใจหลักของ iPhone SE ก็คือชิพ A9 ซึ่งเป็น\n" +
                    "ชิพอันล้ำสมัยแบบเดียวกับที่ใช้ใน iPhone 6s",16500,12600));
            mProductListList.add(new ProductList("2222","http://www.mx7.com/i/0e5/oFp5mm.png","iPhone SE","หัวใจหลักของ iPhone SE ก็คือชิพ A9 ซึ่งเป็น\n" +
                    "ชิพอันล้ำสมัยแบบเดียวกับที่ใช้ใน iPhone 6s",16500,12600));
            mProductListList.add(new ProductList("2345","http://www.mx7.com/i/1cb/TL8yVR.png","Lenovo","Lenovo Yoga 720 เป็นแล็ปท็อป Windows 10 มีสองโมเดลคือ 13 นิ้ว (น้ำหนัก 1.3 กิโลกรัม)",35000,30000));
            mProductListList.add(new ProductList("1122","http://www.mx7.com/i/215/WAY0dD.png","EPSON","เครื่องพิมพ์มัลติฟังก์ชั่นอิงค์เจ็ท Print/ Copy/ Scan/ Fax(With ADF)",9490,9000));

            List<CompareDetailItem> compareDetailItems1 = new ArrayList<>();
            compareDetailItems1.add(new CompareDetailItem(""));
            compareDetailItems1.add(new CompareDetailItem(""));
            compareDetailItems1.add(new CompareDetailItem(""));
            compareDetailItems1.add(new CompareDetailItem(""));

            List<CompareDetailItem> compareDetailItems2 = new ArrayList<>();
            compareDetailItems2.add(new CompareDetailItem(""));
            compareDetailItems2.add(new CompareDetailItem(""));
            compareDetailItems2.add(new CompareDetailItem(""));
            compareDetailItems2.add(new CompareDetailItem(""));

            List<CompareDetailItem> compareDetailItems3 = new ArrayList<>();
            compareDetailItems3.add(new CompareDetailItem(""));
            compareDetailItems3.add(new CompareDetailItem(""));
            compareDetailItems3.add(new CompareDetailItem(""));
            compareDetailItems3.add(new CompareDetailItem(""));

            List<CompareDetailItem> compareDetailItems4 = new ArrayList<>();
            compareDetailItems4.add(new CompareDetailItem(""));
            compareDetailItems4.add(new CompareDetailItem(""));
            compareDetailItems4.add(new CompareDetailItem(""));
            compareDetailItems4.add(new CompareDetailItem(""));

            List<CompareDetailItem> compareDetailItems5 = new ArrayList<>();
            compareDetailItems5.add(new CompareDetailItem(""));
            compareDetailItems5.add(new CompareDetailItem(""));
            compareDetailItems5.add(new CompareDetailItem(""));
            compareDetailItems5.add(new CompareDetailItem(""));

            List<CompareDetail> compareDetails = new ArrayList<>();
            compareDetails.add(new CompareDetail("", compareDetailItems1));
            compareDetails.add(new CompareDetail("", compareDetailItems2));
            compareDetails.add(new CompareDetail("", compareDetailItems3));
            compareDetails.add(new CompareDetail("", compareDetailItems4));
            compareDetails.add(new CompareDetail("", compareDetailItems5));

            mCompareDao = new CompareDao(5, compareDetails);
            mCompareList = new CompareList(4, mProductListList, mCompareDao);
        }else {
            List<ProductList> mProductListList = new ArrayList<>();
            for (AddCompare addCompare : results){
                mProductListList.add(new ProductList(addCompare.getProductId(), addCompare.getUrlName(), addCompare.getProductName(),
                        addCompare.getDescription(), addCompare.getOriginalPrice(), addCompare.getPrice()));
            }

            List<CompareDetailItem> compareDetailItems1 = new ArrayList<>();
            compareDetailItems1.add(new CompareDetailItem(""));
            compareDetailItems1.add(new CompareDetailItem(""));
            compareDetailItems1.add(new CompareDetailItem(""));
            compareDetailItems1.add(new CompareDetailItem(""));

            List<CompareDetailItem> compareDetailItems2 = new ArrayList<>();
            compareDetailItems2.add(new CompareDetailItem(""));
            compareDetailItems2.add(new CompareDetailItem(""));
            compareDetailItems2.add(new CompareDetailItem(""));
            compareDetailItems2.add(new CompareDetailItem(""));

            List<CompareDetailItem> compareDetailItems3 = new ArrayList<>();
            compareDetailItems3.add(new CompareDetailItem(""));
            compareDetailItems3.add(new CompareDetailItem(""));
            compareDetailItems3.add(new CompareDetailItem(""));
            compareDetailItems3.add(new CompareDetailItem(""));

            List<CompareDetailItem> compareDetailItems4 = new ArrayList<>();
            compareDetailItems4.add(new CompareDetailItem(""));
            compareDetailItems4.add(new CompareDetailItem(""));
            compareDetailItems4.add(new CompareDetailItem(""));
            compareDetailItems4.add(new CompareDetailItem(""));

            List<CompareDetailItem> compareDetailItems5 = new ArrayList<>();
            compareDetailItems5.add(new CompareDetailItem(""));
            compareDetailItems5.add(new CompareDetailItem(""));
            compareDetailItems5.add(new CompareDetailItem(""));
            compareDetailItems5.add(new CompareDetailItem(""));

            List<CompareDetail> compareDetails = new ArrayList<>();
            compareDetails.add(new CompareDetail("", compareDetailItems1));
            compareDetails.add(new CompareDetail("", compareDetailItems2));
            compareDetails.add(new CompareDetail("", compareDetailItems3));
            compareDetails.add(new CompareDetail("", compareDetailItems4));
            compareDetails.add(new CompareDetail("", compareDetailItems5));

            mCompareDao = new CompareDao(5, compareDetails);
            mCompareList = new CompareList(4, mProductListList, mCompareDao);
        }


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

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtils.createProgressDialog(getContext());
            mProgressDialog.show();
        } else {
            mProgressDialog.show();
        }
    }

}
