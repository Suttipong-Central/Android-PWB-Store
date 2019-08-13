package cenergy.central.com.pwb_store.fragment

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.CompareProtocol
import cenergy.central.com.pwb_store.adapter.CompareProductAdapter
import cenergy.central.com.pwb_store.adapter.decoration.SpacesItemDecoration
import cenergy.central.com.pwb_store.manager.HttpManagerMagentoOld
import cenergy.central.com.pwb_store.manager.bus.event.CompareDeleteBus
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.AddCompare
import cenergy.central.com.pwb_store.model.CompareDao
import cenergy.central.com.pwb_store.model.CompareDetail
import cenergy.central.com.pwb_store.model.CompareDetailItem
import cenergy.central.com.pwb_store.model.CompareList
import cenergy.central.com.pwb_store.model.CompareProduct
import cenergy.central.com.pwb_store.model.ProductCompareList
import cenergy.central.com.pwb_store.model.response.CompareProductResponse
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.APIErrorUtils
import cenergy.central.com.pwb_store.utils.DialogUtils
import io.realm.Realm
import io.realm.RealmResults
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by napabhat on 7/6/2017 AD.
 */

class CompareFragment : Fragment() {

    @BindView(R.id.recycler_view)
    internal var mRecyclerView: RecyclerView? = null

    //Data Member
    lateinit var mAdapter: CompareProductAdapter
    private var mLayoutManager: GridLayoutManager? = null
    private var mCompareList: CompareList? = null
    private var mCompareDao: CompareDao? = null

    private var mRealm: Realm? = null
    private var results: RealmResults<AddCompare>? = null
    private var mProgressDialog: ProgressDialog? = null
    private var sku: String? = ""
    private var isDelete: Boolean = false
    private val database = RealmController.getInstance()

    private lateinit var listener: CompareProtocol
    private var compareProductDetailList: List<CompareProductResponse> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init(savedInstanceState)

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_compare, container, false)
        initInstances(rootView, savedInstanceState)
        return rootView
    }

    private fun init(savedInstanceState: Bundle?) {
        // Init Fragment level's variable(s) here
        //get realm instance
        this.mRealm = database.realm
        isDelete = false
    }

    private fun initInstances(rootView: View, savedInstanceState: Bundle?) {
        // Init 'View' instance(s) with rootView.findViewById here
        ButterKnife.bind(this, rootView)

        mAdapter = CompareProductAdapter(context)
        mLayoutManager = GridLayoutManager(context, 4, LinearLayoutManager.VERTICAL, false)
        mLayoutManager!!.spanSizeLookup = mAdapter.spanSize
        //mAdapter.setCompare(mCompareList);
        mRecyclerView!!.layoutManager = mLayoutManager
        mRecyclerView!!.addItemDecoration(SpacesItemDecoration(0, LinearLayoutManager.VERTICAL))
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
        mRecyclerView!!.adapter = mAdapter

        // TDB- example
        mockData()
        val compareProducts = database.compareProducts
        mAdapter.updateCompareProducts(compareProducts, mCompareDao)

    }

    private fun mockData() {

        //        results = RealmController.with(this).getCompares();
        if (results == null) {
            val mProductCompareListList = ArrayList<ProductCompareList>()
            mProductCompareListList.add(ProductCompareList("1111", "http://www.mx7.com/i/004/aOc9VL.png", "iPhone SE", "หัวใจหลักของ iPhone SE ก็คือชิพ A9 ซึ่งเป็น\n" + "ชิพอันล้ำสมัยแบบเดียวกับที่ใช้ใน iPhone 6s", 16500.0, 12600.0))
            mProductCompareListList.add(ProductCompareList("2222", "http://www.mx7.com/i/0e5/oFp5mm.png", "iPhone SE", "หัวใจหลักของ iPhone SE ก็คือชิพ A9 ซึ่งเป็น\n" + "ชิพอันล้ำสมัยแบบเดียวกับที่ใช้ใน iPhone 6s", 16500.0, 12600.0))
            mProductCompareListList.add(ProductCompareList("2345", "http://www.mx7.com/i/1cb/TL8yVR.png", "Lenovo", "Lenovo Yoga 720 เป็นแล็ปท็อป Windows 10 มีสองโมเดลคือ 13 นิ้ว (น้ำหนัก 1.3 กิโลกรัม)", 35000.0, 30000.0))
            mProductCompareListList.add(ProductCompareList("1122", "http://www.mx7.com/i/215/WAY0dD.png", "EPSON", "เครื่องพิมพ์มัลติฟังก์ชั่นอิงค์เจ็ท Print/ Copy/ Scan/ Fax(With ADF)", 9490.0, 9000.0))

            val compareDetailItems1 = ArrayList<CompareDetailItem>()
            compareDetailItems1.add(CompareDetailItem(""))
            compareDetailItems1.add(CompareDetailItem(""))
            compareDetailItems1.add(CompareDetailItem(""))
            compareDetailItems1.add(CompareDetailItem(""))

            val compareDetailItems2 = ArrayList<CompareDetailItem>()
            compareDetailItems2.add(CompareDetailItem(""))
            compareDetailItems2.add(CompareDetailItem(""))
            compareDetailItems2.add(CompareDetailItem(""))
            compareDetailItems2.add(CompareDetailItem(""))

            val compareDetailItems3 = ArrayList<CompareDetailItem>()
            compareDetailItems3.add(CompareDetailItem(""))
            compareDetailItems3.add(CompareDetailItem(""))
            compareDetailItems3.add(CompareDetailItem(""))
            compareDetailItems3.add(CompareDetailItem(""))

            val compareDetailItems4 = ArrayList<CompareDetailItem>()
            compareDetailItems4.add(CompareDetailItem(""))
            compareDetailItems4.add(CompareDetailItem(""))
            compareDetailItems4.add(CompareDetailItem(""))
            compareDetailItems4.add(CompareDetailItem(""))

            val compareDetailItems5 = ArrayList<CompareDetailItem>()
            compareDetailItems5.add(CompareDetailItem(""))
            compareDetailItems5.add(CompareDetailItem(""))
            compareDetailItems5.add(CompareDetailItem(""))
            compareDetailItems5.add(CompareDetailItem(""))

            val compareDetails = ArrayList<CompareDetail>()
            compareDetails.add(CompareDetail("", compareDetailItems1))
            compareDetails.add(CompareDetail("", compareDetailItems2))
            compareDetails.add(CompareDetail("", compareDetailItems3))
            compareDetails.add(CompareDetail("", compareDetailItems4))
            compareDetails.add(CompareDetail("", compareDetailItems5))

            mCompareDao = CompareDao(5, compareDetails)
            mCompareList = CompareList(4, mProductCompareListList, mCompareDao)
        } else {

            val mProductCompareListList = ArrayList<ProductCompareList>()
            for (addCompare in results!!) {
                mProductCompareListList.add(ProductCompareList(addCompare.productId, addCompare.urlName, addCompare.productName,
                        addCompare.description, addCompare.originalPrice, addCompare.price))
            }

            val compareDetailItems1 = ArrayList<CompareDetailItem>()
            compareDetailItems1.add(CompareDetailItem(""))
            compareDetailItems1.add(CompareDetailItem(""))
            compareDetailItems1.add(CompareDetailItem(""))
            compareDetailItems1.add(CompareDetailItem(""))

            val compareDetailItems2 = ArrayList<CompareDetailItem>()
            compareDetailItems2.add(CompareDetailItem(""))
            compareDetailItems2.add(CompareDetailItem(""))
            compareDetailItems2.add(CompareDetailItem(""))
            compareDetailItems2.add(CompareDetailItem(""))

            val compareDetailItems3 = ArrayList<CompareDetailItem>()
            compareDetailItems3.add(CompareDetailItem(""))
            compareDetailItems3.add(CompareDetailItem(""))
            compareDetailItems3.add(CompareDetailItem(""))
            compareDetailItems3.add(CompareDetailItem(""))

            val compareDetailItems4 = ArrayList<CompareDetailItem>()
            compareDetailItems4.add(CompareDetailItem(""))
            compareDetailItems4.add(CompareDetailItem(""))
            compareDetailItems4.add(CompareDetailItem(""))
            compareDetailItems4.add(CompareDetailItem(""))

            val compareDetailItems5 = ArrayList<CompareDetailItem>()
            compareDetailItems5.add(CompareDetailItem(""))
            compareDetailItems5.add(CompareDetailItem(""))
            compareDetailItems5.add(CompareDetailItem(""))
            compareDetailItems5.add(CompareDetailItem(""))

            val compareDetails = ArrayList<CompareDetail>()
            compareDetails.add(CompareDetail("", compareDetailItems1))
            compareDetails.add(CompareDetail("", compareDetailItems2))
            compareDetails.add(CompareDetail("", compareDetailItems3))
            compareDetails.add(CompareDetail("", compareDetailItems4))
            compareDetails.add(CompareDetail("", compareDetailItems5))

            mCompareDao = CompareDao(5, compareDetails)
            mCompareList = CompareList(4, mProductCompareListList, mCompareDao)
        }


    }

    //
    //    @Override
    //    public void onStart() {
    //        super.onStart();
    //    }
    //
    //    @Override
    //    public void onStop() {
    //        super.onStop();
    //    }
    //
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context != null) {
            //TODO Use this List to make compare product detail
            listener = context as CompareProtocol
            compareProductDetailList = listener.getCompareProductDetailList()
        }
    }
    //
    //    @Override
    //    public void onDetach() {
    //        EventBus.getDefault().unregister(this);
    //        super.onDetach();
    //    }

    /*
     * Save Instance State Here
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save Instance State here
        outState.putParcelable(ARG_COMPARE_LIST, mCompareList)
        outState.putParcelable(ARG_COMPARE_DAO, mCompareDao)
        outState.putString(ARG_SKU, sku)
        outState.putBoolean(ARG_IS_DELETE, isDelete)
    }

    /*
     * Restore Instance State Here
     */
    private fun onRestoreInstanceState(savedInstanceState: Bundle) {
        // Restore Instance State here
        mCompareList = savedInstanceState.getParcelable(ARG_COMPARE_LIST)
        mCompareDao = savedInstanceState.getParcelable(ARG_COMPARE_DAO)
        sku = savedInstanceState.getString(ARG_SKU)
        isDelete = savedInstanceState.getBoolean(ARG_IS_DELETE)
    }

    private fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtils.createProgressDialog(context)
            mProgressDialog!!.show()
        } else {
            mProgressDialog!!.show()
        }
    }

    fun getSku(): String {
        var result = ""
        var listString1 = ""
        var listString2 = ""
        var detail = ""
        results = database.compares
        val list = ArrayList<String>()
        if (results != null) {
            for (addCompare in results!!) {
                list.add(addCompare.productSku)
            }
            listString1 = list.toString()
            listString2 = listString1.replace("[", "")
            detail = listString2.replace("]", "")
            result = detail.replace(" ", "")
        }

        return result
    }

    companion object {
        private val TAG = CompareFragment::class.java.simpleName
        private val ARG_COMPARE_LIST = "ARG_COMPARE_LIST"
        private val ARG_COMPARE_DAO = "ARG_COMPARE_DAO"
        private val ARG_SKU = "ARG_SKU"
        private val ARG_IS_DELETE = "ARG_IS_DELETE"
        private val ARG_RESULTS = "ARG_RESULTS"

        //    final Callback<CompareList> CALLBACK_COMPARE = new Callback<CompareList>() {
        //        @Override
        //        public void onResponse(Call<CompareList> call, Response<CompareList> response) {
        //            if (response.isSuccessful()) {
        //                //mProgressDialog.dismiss();
        //                mCompareList = response.body();
        //                HttpManagerMagentoOld.getInstance().getCompareService().getCompareItem(sku).enqueue(CALLBACK_ITEM);
        //
        //            } else {
        //                mProgressDialog.dismiss();
        //                APIError error = APIErrorUtils.parseError(response);
        //                Log.e(TAG, "onResponse: " + error.getErrorMessage());
        //                mockData();
        //                mAdapter.setUpdateCompare(mCompareList);
        //            }
        //        }
        //
        //        @Override
        //        public void onFailure(Call<CompareList> call, Throwable t) {
        //            Log.e(TAG, "onFailure: ", t);
        //            mProgressDialog.dismiss();
        //        }
        //    };

        //    final Callback<List<CompareDao>> CALLBACK_ITEM = new Callback<List<CompareDao>>() {
        //        @Override
        //        public void onResponse(Call<List<CompareDao>> call, Response<List<CompareDao>> response) {
        //            if (response.isSuccessful()) {
        //                mProgressDialog.dismiss();
        //                mCompareDao = response.body().get(0);
        //                mCompareList.setCompareDao(mCompareDao);
        //                if (isDelete == true) {
        //                    mAdapter.setUpdateCompare(mCompareList);
        //                } else {
        //                    mAdapter.setCompare(mCompareList);
        //                }
        //
        //            } else {
        //                mProgressDialog.dismiss();
        //                APIError error = APIErrorUtils.parseError(response);
        //                Log.e(TAG, "onResponse: " + error.getErrorMessage());
        //                mockData();
        //                mAdapter.setUpdateCompare(mCompareList);
        //            }
        //        }
        //
        //        @Override
        //        public void onFailure(Call<List<CompareDao>> call, Throwable t) {
        //            Log.e(TAG, "onFailure: ", t);
        //            mProgressDialog.dismiss();
        //            mockData();
        //        }
        //    };

        //    @Subscribe
        //    public void onEvent(CompareDeleteBus compareDeleteBus) {
        //        isDelete = true;
        //        ProductCompareList productList = compareDeleteBus.getProductCompareList();
        //        results = RealmController.with(this).deletedCompare(productList.getProductId());
        //        sku = getSku();
        //        Log.d(TAG, "id" +productList.getProductId());
        //        showProgressDialog();

        //        CompareProduct compareProduct = compareDeleteBus.getCompareProduct();
        //        List<CompareProduct> compareProducts = database.deleteCompareProduct(compareProduct.getSku());
        //        mAdapter.updateCompareProducts(compareProducts, mCompareDao);

        //        HttpManagerMagentoOld.getInstance().getCompareService().getCompareProductList("sku",sku,"in","in_stores",
        //                UserInfoManager.getInstance().getUserId(),"finset", "sku").enqueue(CALLBACK_COMPARE);
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
        //    }

        fun newInstance(): CompareFragment {
            val fragment = CompareFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
