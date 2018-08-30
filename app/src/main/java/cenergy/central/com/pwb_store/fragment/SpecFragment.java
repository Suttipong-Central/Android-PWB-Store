package cenergy.central.com.pwb_store.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.SpecDetailAdapter;
import cenergy.central.com.pwb_store.adapter.decoration.SpacesItemDecoration;
import cenergy.central.com.pwb_store.manager.bus.event.SpecAddToCompareBus;
import cenergy.central.com.pwb_store.manager.bus.event.UpdateBadgeBus;
import cenergy.central.com.pwb_store.model.AddCompare;
import cenergy.central.com.pwb_store.model.ExtensionProductDetail;
import cenergy.central.com.pwb_store.model.Product;
import cenergy.central.com.pwb_store.model.ProductDetail;
import cenergy.central.com.pwb_store.model.ProductDetailImageItem;
import cenergy.central.com.pwb_store.model.ProductDetailStore;
import cenergy.central.com.pwb_store.model.SpecDao;
import cenergy.central.com.pwb_store.realm.RealmController;
import cenergy.central.com.pwb_store.utils.DialogUtils;
import io.realm.Realm;

/**
 * Created by napabhat on 7/6/2017 AD.
 */

public class SpecFragment extends Fragment {
    private static final String TAG = SpecFragment.class.getSimpleName();
    private static final String ARG_SPEC_DAO = "ARG_SPEC_DAO";
    private static final String ARG_PRODUCT = "ARG_PRODUCT";
    private static final String ARG_PRODUCT_DETAIL = "ARG_PRODUCT_DETAIL";

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private Product product;

    private SpecDao mSpecDao;
    private SpecDetailAdapter mAdapter;
    private GridLayoutManager mLayoutManager;
    private ProductDetail mRealmProductDetail;
    // Realm
    private Realm mRealm;
    private Handler mHandler;
    private Runnable runOnUiThread;
    private AddCompare addCompare;
    private ProgressDialog mProgressDialog;

    public SpecFragment() {
        super();
    }

    @Subscribe
    public void onEvent(SpecAddToCompareBus specAddToCompareBus){
        if (specAddToCompareBus.isAdded() == true){
            showProgressDialog();
            long count = RealmController.with(this).getCompareProducts().size();
            Log.d(TAG, "" + count);
            if (count >= 4){
                mProgressDialog.dismiss();
                showAlertDialog(getContext().getResources().getString(R.string.alert_count));
            }else {
                String id = checkPrimary();
                if (id.equalsIgnoreCase(mRealmProductDetail.getProductCode())){
                    mProgressDialog.dismiss();
                    showAlertDialog(getContext().getResources().getString(R.string.alert_compare)
                            + "" + mRealmProductDetail.getProductName() + "" +
                            getContext().getResources().getString(R.string.alert_compare_yes));
                }else {
                    insertCompare();
                }
            }
        }
    }

    @SuppressWarnings("unused")
    public static SpecFragment newInstance(SpecDao specDao, ProductDetail productDetail) {
        SpecFragment fragment = new SpecFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_SPEC_DAO, specDao);
        args.putParcelable(ARG_PRODUCT_DETAIL, productDetail);
        fragment.setArguments(args);
        return fragment;
    }

    public static SpecFragment newInstance(Product product) {
        SpecFragment fragment = new SpecFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PRODUCT, product);
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
        View rootView = inflater.inflate(R.layout.fragment_spec, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here

        if (getArguments() != null) {
            mSpecDao = getArguments().getParcelable(ARG_SPEC_DAO);
            mRealmProductDetail = getArguments().getParcelable(ARG_PRODUCT_DETAIL);
            product = getArguments().getParcelable(ARG_PRODUCT);
        }

        mHandler = new Handler();
//        List<SpecItem> specItems = new ArrayList<>();
//        specItems.add(new SpecItem("Instant Film","Fujifilm Instant Color “Instax mini”"));
//        specItems.add(new SpecItem("Picture size","62x46mm"));
//        specItems.add(new SpecItem("Shutter","Shutter speed : 1/60 sec"));
//        specItems.add(new SpecItem("Exposure Control","Manual Switching System (LED indecator in exposure meter)"));
//        specItems.add(new SpecItem("Flash","Constant firing flash (automatic light adjustment)\n" +
//                "Recycle time : 0.2 sec. to 6 sec. (when using new batteries), Effective flash\n" +
//                "range : 0.6m - 2.7m"));
//        specItems.add(new SpecItem("Display Screen","3.0 Inches."));

//        mSpecDao = new SpecDao(specItems);
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        ButterKnife.bind(this, rootView);

        //get realm instance
        this.mRealm = RealmController.with(this).getRealm();
        mAdapter = new SpecDetailAdapter(getContext());
        mLayoutManager = new GridLayoutManager(getContext(), 4, LinearLayoutManager.VERTICAL, false);
        mLayoutManager.setSpanSizeLookup(mAdapter.getSpanSize());
//        mAdapter.setSpecDetail(mSpecDao);
        mAdapter.setProduct(product);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(0, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void insertCompare() {

        if (runOnUiThread != null) {
            mHandler.removeCallbacks(runOnUiThread);
        }

        runOnUiThread = new Runnable() {
            @Override
            public void run() {
                mRealm.executeTransactionAsync(
                        new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                if (mRealmProductDetail != null){
                                    addCompare = new AddCompare();

                                    addCompare.setProductId(mRealmProductDetail.getProductCode());
                                    addCompare.setProductName(mRealmProductDetail.getProductName());
                                    addCompare.setProductNameEN(mRealmProductDetail.getProductNameEN());
                                    addCompare.setDescription(mRealmProductDetail.getDescription());
                                    addCompare.setDescriptionEN(mRealmProductDetail.getDescriptionEN());
                                    ExtensionProductDetail extensionProductDetail = mRealmProductDetail.getExtensionProductDetail();
                                    if (extensionProductDetail != null){
                                        for (ProductDetailStore productDetailStore : extensionProductDetail.getProductDetailStores()){
                                            addCompare.setBarcode(productDetailStore.getBarCode());
                                            addCompare.setOriginalPrice(Double.parseDouble(productDetailStore.getPrice()));
                                            addCompare.setPrice(Double.parseDouble(productDetailStore.getSpecialPrice()));
                                            addCompare.setStoreId(productDetailStore.getStoreId());
                                            addCompare.setStockAvailable(Integer.parseInt(productDetailStore.getStockAvailable()));
                                            addCompare.setUrlName(mRealmProductDetail.getExtensionProductDetail().getImageUrl());
                                            addCompare.setUrlNameEN(mRealmProductDetail.getExtensionProductDetail().getImageUrl());
                                        }
                                    }
                                    addCompare.setReview(mRealmProductDetail.getReview());
                                    addCompare.setReviewEN(mRealmProductDetail.getReviewEN());
                                    addCompare.setNumOfImage(mRealmProductDetail.getNumOfImage());
                                    addCompare.setCanInstallment(mRealmProductDetail.isCanInstallment());
                                    addCompare.setT1cPoint(mRealmProductDetail.getT1cPoint());
                                    addCompare.setProductSku(mRealmProductDetail.getSku());
                                    //addCompare.setDepartmentId(mRealmProductDetail.getDepartmentId());
                                    addCompare.setDepartmentId(Integer.parseInt(mRealmProductDetail.getSku()));
                                    addCompare.setBrandId(mRealmProductDetail.getBrandId());
                                    addCompare.setBrand(mRealmProductDetail.getBrand());
                                    addCompare.setBrandEN(mRealmProductDetail.getBrandEN());
                                    if (mRealmProductDetail.getProductDetailImageItems() != null){
                                        for (ProductDetailImageItem productDetailImageItem :
                                                mRealmProductDetail.getProductDetailImageItems()) {
                                            addCompare.setUrl(productDetailImageItem.getImgUrl());
                                        }
                                    }

                                }

                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                mRealm.beginTransaction();
                                //mRealm.copyToRealm(addCompare);
                                mRealm.copyToRealmOrUpdate(addCompare);
                                mRealm.commitTransaction();
                                EventBus.getDefault().post(new UpdateBadgeBus(true));
                                mProgressDialog.dismiss();
                                Toast.makeText(getContext(), "Generate compare complete.", Toast.LENGTH_SHORT).show();

                            }

                        }, new Realm.Transaction.OnError() {
                            @Override
                            public void onError(Throwable error) {
                                error.printStackTrace();
                                mProgressDialog.dismiss();
                                Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "" + error.getMessage());
                            }
                        });
            }
        };

        mHandler.postDelayed(runOnUiThread, 1000);

    }

    private void clearCompare() {
        mRealm.beginTransaction();
        mRealm.delete(AddCompare.class);
        mRealm.commitTransaction();
    }

    private String checkPrimary(){
        String result;
        String id = mRealmProductDetail.getProductCode();
        addCompare = RealmController.with(this).getCompare(id);
        if (addCompare == null){
            result = "";
        }else {
            result = addCompare.getProductId();
        }

        return result;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
        outState.putParcelable(ARG_SPEC_DAO, mSpecDao);
        outState.putParcelable(ARG_PRODUCT_DETAIL, mRealmProductDetail);
    }

    /*
     * Restore Instance State Here
     */
    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
        mSpecDao = savedInstanceState.getParcelable(ARG_SPEC_DAO);
        mRealmProductDetail = savedInstanceState.getParcelable(ARG_PRODUCT_DETAIL);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);
        //mRealm.close();
        super.onDetach();
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });

        builder.show();
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
