package cenergy.central.com.pwb_store.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.activity.AvaliableStoreActivity;
import cenergy.central.com.pwb_store.activity.ProductDetailActivity;
import cenergy.central.com.pwb_store.activity.WebViewActivity;
import cenergy.central.com.pwb_store.adapter.ProductDetailAdapter;
import cenergy.central.com.pwb_store.adapter.decoration.ProductGridSpacesItemDecoration;
import cenergy.central.com.pwb_store.manager.bus.event.BookTimeBus;
import cenergy.central.com.pwb_store.manager.bus.event.ProductDetailOptionItemBus;
import cenergy.central.com.pwb_store.manager.bus.event.StoreAvaliableBus;
import cenergy.central.com.pwb_store.model.ProductDetail;
import cenergy.central.com.pwb_store.model.ProductDetailOptionItem;
import cenergy.central.com.pwb_store.model.Recommend;

/**
 * Created by napabhat on 7/6/2017 AD.
 */

public class ProductDetailFragment extends Fragment {

    private static final String ARG_PRODUCT_DETAIL = "ARG_PRODUCT_DETAIL";
    private static final String ARG_RECOMMEND = "ARG_RECOMMEND";

    //View Members
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    //Data Member
    private ProductDetailAdapter mAdapter;
    private GridLayoutManager mLayoutManager;
    private ProductDetail mProductDetail;
    private Recommend mRecommend;

    public ProductDetailFragment() {
        super();
    }

    @Subscribe
    public void onEvent(ProductDetailOptionItemBus productDetailOptionItemBus) {
        ProductDetailOptionItem productDetailOptionItem = productDetailOptionItemBus.getProductDetailOptionItem();
        mProductDetail.getProductDetailOption().setSelectedProductDetailOptionItem(productDetailOptionItem);
        mProductDetail.replaceProduct(productDetailOptionItem);
        mAdapter.updateProductDetailOption(mProductDetail, productDetailOptionItem, productDetailOptionItemBus.getAdapterPosition());
    }

    @Subscribe
    public void onEvent(StoreAvaliableBus storeAvaliableBus){
        Intent intent = new Intent(getContext(), AvaliableStoreActivity.class);
        //intent.putExtra(AvaliableStoreActivity.ARG_PRODUCT_ID, storeAvaliableBus.getProductId());
        ActivityCompat.startActivity(getContext(), intent,
                ActivityOptionsCompat
                        .makeScaleUpAnimation(storeAvaliableBus.getView(), 0, 0, storeAvaliableBus.getView().getWidth(), storeAvaliableBus.getView().getHeight())
                        .toBundle());
    }

    @Subscribe
    public void onEvent(BookTimeBus bookTimeBus){
        Intent intent = new Intent(getContext(), WebViewActivity.class);
        intent.putExtra(WebViewActivity.ARG_WEB_URL, getContext().getResources().getString(R.string.hdl_url));
        intent.putExtra(WebViewActivity.ARG_MODE, WebViewFragment.MODE_URL);
        intent.putExtra(WebViewActivity.ARG_TITLE, "Web");

        ActivityCompat.startActivity(getContext(), intent,
                ActivityOptionsCompat
                        .makeScaleUpAnimation(bookTimeBus.getView(), 0, 0, bookTimeBus.getView().getWidth(), bookTimeBus.getView().getHeight())
                        .toBundle());
    }

    @SuppressWarnings("unused")
    public static ProductDetailFragment newInstance(ProductDetail productDetail, Recommend recommend) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PRODUCT_DETAIL, productDetail);
        args.putParcelable(ARG_RECOMMEND, recommend);
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
        View rootView = inflater.inflate(R.layout.fragment_product_detail, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        if (getArguments() != null) {
            //productId = getArguments().getString(ARG_PRODUCT_ID);
            mProductDetail = getArguments().getParcelable(ARG_PRODUCT_DETAIL);
            mRecommend = getArguments().getParcelable(ARG_RECOMMEND);
        }
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        ButterKnife.bind(this, rootView);

        mAdapter = new ProductDetailAdapter(getContext(), getChildFragmentManager());
        mLayoutManager = new GridLayoutManager(getContext(), 7, LinearLayoutManager.VERTICAL, false);
        mLayoutManager.setSpanSizeLookup(mAdapter.getSpanSize());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new ProductGridSpacesItemDecoration(getContext(), R.dimen.product_item_spacing));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setProductDetail(mProductDetail);

        mAdapter.setProductRecommend(mRecommend);
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
        outState.putParcelable(ARG_PRODUCT_DETAIL, mProductDetail);
        outState.putParcelable(ARG_RECOMMEND, mRecommend);
    }

    /*
     * Restore Instance State Here
     */
    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
        mProductDetail = savedInstanceState.getParcelable(ARG_PRODUCT_DETAIL);
        mRecommend = savedInstanceState.getParcelable(ARG_RECOMMEND);
    }

}
