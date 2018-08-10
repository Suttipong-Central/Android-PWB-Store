package cenergy.central.com.pwb_store.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import cenergy.central.com.pwb_store.manager.HttpManagerMagentoOld;
import cenergy.central.com.pwb_store.manager.UserInfoManager;
import cenergy.central.com.pwb_store.manager.bus.event.CompareDeleteBus;
import cenergy.central.com.pwb_store.model.APIError;
import cenergy.central.com.pwb_store.model.AddCompare;
import cenergy.central.com.pwb_store.model.CompareDao;
import cenergy.central.com.pwb_store.model.CompareDetail;
import cenergy.central.com.pwb_store.model.CompareDetailItem;
import cenergy.central.com.pwb_store.model.CompareList;
import cenergy.central.com.pwb_store.model.CompareProduct;
import cenergy.central.com.pwb_store.model.ProductCompareList;
import cenergy.central.com.pwb_store.realm.RealmController;
import cenergy.central.com.pwb_store.utils.APIErrorUtils;
import cenergy.central.com.pwb_store.utils.DialogUtils;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by napabhat on 7/6/2017 AD.
 */

public class CompareFragment extends Fragment {
    private static final String TAG = CompareFragment.class.getSimpleName();
    private static final String ARG_COMPARE_LIST = "ARG_COMPARE_LIST";
    private static final String ARG_COMPARE_DAO = "ARG_COMPARE_DAO";
    private static final String ARG_SKU = "ARG_SKU";
    private static final String ARG_IS_DELETE = "ARG_IS_DELETE";
    private static final String ARG_RESULTS = "ARG_RESULTS";

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
    private String sku = "";
    private boolean isDelete;

    public CompareFragment() {
        super();
    }

    final Callback<CompareList> CALLBACK_COMPARE = new Callback<CompareList>() {
        @Override
        public void onResponse(Call<CompareList> call, Response<CompareList> response) {
            if (response.isSuccessful()){
                //mProgressDialog.dismiss();
                mCompareList = response.body();
                HttpManagerMagentoOld.getInstance().getCompareService().getCompareItem(sku).enqueue(CALLBACK_ITEM);

            }else {
                mProgressDialog.dismiss();
                APIError error = APIErrorUtils.parseError(response);
                Log.e(TAG, "onResponse: " + error.getErrorMessage());
                mockData();
                mAdapter.setUpdateCompare(mCompareList);
            }
        }

        @Override
        public void onFailure(Call<CompareList> call, Throwable t) {
            Log.e(TAG, "onFailure: ", t);
            mProgressDialog.dismiss();
        }
    };

    final Callback<List<CompareDao>> CALLBACK_ITEM = new Callback<List<CompareDao>>() {
        @Override
        public void onResponse(Call<List<CompareDao>> call, Response<List<CompareDao>> response) {
            if (response.isSuccessful()){
                mProgressDialog.dismiss();
                mCompareDao = response.body().get(0);
                mCompareList.setCompareDao(mCompareDao);
                if (isDelete == true){
                    mAdapter.setUpdateCompare(mCompareList);
                }else {
                    mAdapter.setCompare(mCompareList);
                }

            }else {
                mProgressDialog.dismiss();
                APIError error = APIErrorUtils.parseError(response);
                Log.e(TAG, "onResponse: " + error.getErrorMessage());
                mockData();
                mAdapter.setUpdateCompare(mCompareList);
            }
        }

        @Override
        public void onFailure(Call<List<CompareDao>> call, Throwable t) {
            Log.e(TAG, "onFailure: ", t);
            mProgressDialog.dismiss();
            mockData();
        }
    };

    @Subscribe
    public void onEvent(CompareDeleteBus compareDeleteBus){
        isDelete = true;
        ProductCompareList productList = compareDeleteBus.getProductCompareList();
        results = RealmController.with(this).deletedCompare(productList.getProductId());
        sku = getSku();
        Log.d(TAG, "id" +productList.getProductId());
        showProgressDialog();
        HttpManagerMagentoOld.getInstance().getCompareService().getCompareProductList("sku",sku,"in","in_stores",
                UserInfoManager.getInstance().getUserId(),"finset", "sku").enqueue(CALLBACK_COMPARE);
//        List<ProductCompareList> mProductCompareListList = new ArrayList<>();
//        for (AddCompare addCompare : results){
//            mProductCompareListList.add(new ProductCompareList(addCompare.getProductId(), addCompare.getUrlName(), addCompare.getProductName(),
//                    addCompare.getDescription(), addCompare.getOriginalPrice(), addCompare.getPrice()));
//        }
//
//        List<CompareDetailItem> compareDetailItems1 = new ArrayList<>();
//        compareDetailItems1.add(new CompareDetailItem(""));
//        compareDetailItems1.add(new CompareDetailItem(""));
//        compareDetailItems1.add(new CompareDetailItem(""));
//        compareDetailItems1.add(new CompareDetailItem(""));
//
//        List<CompareDetailItem> compareDetailItems2 = new ArrayList<>();
//        compareDetailItems2.add(new CompareDetailItem(""));
//        compareDetailItems2.add(new CompareDetailItem(""));
//        compareDetailItems2.add(new CompareDetailItem(""));
//        compareDetailItems2.add(new CompareDetailItem(""));
//
//        List<CompareDetailItem> compareDetailItems3 = new ArrayList<>();
//        compareDetailItems3.add(new CompareDetailItem(""));
//        compareDetailItems3.add(new CompareDetailItem(""));
//        compareDetailItems3.add(new CompareDetailItem(""));
//        compareDetailItems3.add(new CompareDetailItem(""));
//
//        List<CompareDetailItem> compareDetailItems4 = new ArrayList<>();
//        compareDetailItems4.add(new CompareDetailItem(""));
//        compareDetailItems4.add(new CompareDetailItem(""));
//        compareDetailItems4.add(new CompareDetailItem(""));
//        compareDetailItems4.add(new CompareDetailItem(""));
//
//        List<CompareDetailItem> compareDetailItems5 = new ArrayList<>();
//        compareDetailItems5.add(new CompareDetailItem(""));
//        compareDetailItems5.add(new CompareDetailItem(""));
//        compareDetailItems5.add(new CompareDetailItem(""));
//        compareDetailItems5.add(new CompareDetailItem(""));
//
//        List<CompareDetail> compareDetails = new ArrayList<>();
//        compareDetails.add(new CompareDetail("", compareDetailItems1));
//        compareDetails.add(new CompareDetail("", compareDetailItems2));
//        compareDetails.add(new CompareDetail("", compareDetailItems3));
//        compareDetails.add(new CompareDetail("", compareDetailItems4));
//        compareDetails.add(new CompareDetail("", compareDetailItems5));
//
//        mCompareDao = new CompareDao(5, compareDetails);
//        mCompareList = new CompareList(4, mProductCompareListList, mCompareDao);

//        mAdapter.setUpdateCompare(mCompareList);

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
        isDelete = false;
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        ButterKnife.bind(this, rootView);

        mAdapter = new CompareProductAdapter(getContext());
        mLayoutManager = new GridLayoutManager(getContext(), 4, LinearLayoutManager.VERTICAL, false);
        mLayoutManager.setSpanSizeLookup(mAdapter.getSpanSize());
        //mAdapter.setCompare(mCompareList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(0, LinearLayoutManager.VERTICAL));
//        if (savedInstanceState == null){
//            sku = getSku();
//            if (sku.equalsIgnoreCase("")){
//                sku = getSku();
//            }else {
//                showProgressDialog();
//                Log.d(TAG, "skuString " + sku);
//                HttpManagerMagentoOld.getInstance().getCompareService().getCompareProductList("sku",sku,"in","in_stores",
//                        UserInfoManager.getInstance().getUserId(),"finset", "sku").enqueue(CALLBACK_COMPARE);
//            }
//
//        }else {
//            mockData();
//        }
        mRecyclerView.setAdapter(mAdapter);

        // TDB- example
       List<CompareProduct> compareProducts =  RealmController.getInstance().getCompareProducts();
       mAdapter.updateCompareProducts(compareProducts);

    }

    private void mockData(){

        results = RealmController.with(this).getCompares();
        if (results == null){
            List<ProductCompareList> mProductCompareListList = new ArrayList<>();
            mProductCompareListList.add(new ProductCompareList("1111","http://www.mx7.com/i/004/aOc9VL.png","iPhone SE","หัวใจหลักของ iPhone SE ก็คือชิพ A9 ซึ่งเป็น\n" +
                    "ชิพอันล้ำสมัยแบบเดียวกับที่ใช้ใน iPhone 6s",16500,12600));
            mProductCompareListList.add(new ProductCompareList("2222","http://www.mx7.com/i/0e5/oFp5mm.png","iPhone SE","หัวใจหลักของ iPhone SE ก็คือชิพ A9 ซึ่งเป็น\n" +
                    "ชิพอันล้ำสมัยแบบเดียวกับที่ใช้ใน iPhone 6s",16500,12600));
            mProductCompareListList.add(new ProductCompareList("2345","http://www.mx7.com/i/1cb/TL8yVR.png","Lenovo","Lenovo Yoga 720 เป็นแล็ปท็อป Windows 10 มีสองโมเดลคือ 13 นิ้ว (น้ำหนัก 1.3 กิโลกรัม)",35000,30000));
            mProductCompareListList.add(new ProductCompareList("1122","http://www.mx7.com/i/215/WAY0dD.png","EPSON","เครื่องพิมพ์มัลติฟังก์ชั่นอิงค์เจ็ท Print/ Copy/ Scan/ Fax(With ADF)",9490,9000));

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
            mCompareList = new CompareList(4, mProductCompareListList, mCompareDao);
        }else {

            List<ProductCompareList> mProductCompareListList = new ArrayList<>();
            for (AddCompare addCompare : results){
                mProductCompareListList.add(new ProductCompareList(addCompare.getProductId(), addCompare.getUrlName(), addCompare.getProductName(),
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
            mCompareList = new CompareList(4, mProductCompareListList, mCompareDao);
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
        outState.putParcelable(ARG_COMPARE_LIST, mCompareList);
        outState.putParcelable(ARG_COMPARE_DAO, mCompareDao);
        outState.putString(ARG_SKU, sku);
        outState.putBoolean(ARG_IS_DELETE, isDelete);
    }

    /*
     * Restore Instance State Here
     */
    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
        mCompareList = savedInstanceState.getParcelable(ARG_COMPARE_LIST);
        mCompareDao = savedInstanceState.getParcelable(ARG_COMPARE_DAO);
        sku = savedInstanceState.getString(ARG_SKU);
        isDelete = savedInstanceState.getBoolean(ARG_IS_DELETE);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtils.createProgressDialog(getContext());
            mProgressDialog.show();
        } else {
            mProgressDialog.show();
        }
    }

    public String getSku(){
        String result = "";
        String listString1 = "";
        String listString2 = "";
        String detail = "";
        results = RealmController.with(this).getCompares();
        ArrayList<String> list = new ArrayList<String>();
        if (results != null){
            for (AddCompare addCompare :
                    results) {
                list.add(addCompare.getProductSku());
            }
            listString1 = list.toString();
            listString2 = listString1.replace("[", "");
            detail = listString2.replace("]", "");
            result = detail.replace(" ", "");
        }

        return result;
    }

}
