package cenergy.central.com.pwb_store.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.bus.event.ProductDetailImageBus;
import cenergy.central.com.pwb_store.model.ProductDetailImage;
import cenergy.central.com.pwb_store.model.ProductDetailImageItem;
import cenergy.central.com.pwb_store.view.PowerBuyProductImageItemView;


/**
 * Created by nuuneoi on 11/16/2014.
 * Modified by napabhat on 10/31/2016 AD.
 */
@SuppressWarnings("unused")
public class ProductDetailImagePagerFragment extends Fragment {
    private static final String TAG = "ProductDetailImagePagerFragment";
    private static final String FRAGMENT_TYPE_ID_PRODUCT_IMAGE_ITEM = "FRAGMENT_TYPE_ID_PRODUCT_IMAGE_ITEM";
    private static final String FRAGMENT_TYPE_ID_POSITION = "FRAGMENT_TYPE_ID_POSITION";
    private static final String FRAGMENT_TYPE_ID_PRODUCT_IMAGE = "FRAGMENT_TYPE_ID_PRODUCT_IMAGE";

    //View Members
    @BindViews({R.id.image_01, R.id.image_02, R.id.image_03})
    List<PowerBuyProductImageItemView> mPowerBuyProductImageItemViews;

    //Data Members
    private ArrayList<ProductDetailImageItem> productDetailImageItems;
    private ProductDetailImage mProductDetailImage;

    public ProductDetailImagePagerFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static ProductDetailImagePagerFragment newInstance(ArrayList<ProductDetailImageItem> productDetailImageItems, ProductDetailImage productDetailImage) {
        ProductDetailImagePagerFragment fragment = new ProductDetailImagePagerFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(FRAGMENT_TYPE_ID_PRODUCT_IMAGE_ITEM, productDetailImageItems);
        args.putParcelable(FRAGMENT_TYPE_ID_PRODUCT_IMAGE, productDetailImage);
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
        View rootView = inflater.inflate(R.layout.fragment_product_detail_image_item, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        if (getArguments() != null) {
            productDetailImageItems = getArguments().getParcelableArrayList(FRAGMENT_TYPE_ID_PRODUCT_IMAGE_ITEM);
            mProductDetailImage = getArguments().getParcelable(FRAGMENT_TYPE_ID_PRODUCT_IMAGE);
        }
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        ButterKnife.bind(this, rootView);

        for (int i = 0; i < mPowerBuyProductImageItemViews.size(); i++) {
            PowerBuyProductImageItemView powerBuyProductImageItemView = mPowerBuyProductImageItemViews.get(i);
            try {
                final ProductDetailImageItem productDetailImageItem = productDetailImageItems.get(i);
                powerBuyProductImageItemView.setProductDetailImageItem(productDetailImageItems.get(i));
                powerBuyProductImageItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        productDetailImageItem.setSelected(true);
                        EventBus.getDefault().post(new ProductDetailImageBus(v, productDetailImageItem, true, mProductDetailImage));
                    }
                });
            } catch (IndexOutOfBoundsException ie) {
                powerBuyProductImageItemView.setVisibility(View.INVISIBLE);
            }
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

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
        outState.putParcelableArrayList(FRAGMENT_TYPE_ID_PRODUCT_IMAGE_ITEM, productDetailImageItems);
        outState.putParcelable(FRAGMENT_TYPE_ID_PRODUCT_IMAGE, mProductDetailImage);
    }

    /*
     * Restore Instance State Here
     */
    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
        productDetailImageItems = savedInstanceState.getParcelableArrayList(FRAGMENT_TYPE_ID_PRODUCT_IMAGE_ITEM);
        mProductDetailImage = savedInstanceState.getParcelable(FRAGMENT_TYPE_ID_PRODUCT_IMAGE);
    }

}
