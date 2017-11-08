package cenergy.central.com.pwb_store.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.Contextor;
import cenergy.central.com.pwb_store.model.BundleSavedState;
import cenergy.central.com.pwb_store.model.ProductDetailImageItem;

/**
 * Created by napabhat on 7/20/2017 AD.
 */

public class PowerBuyProductImageItemView extends FrameLayout {
    private static final String TAG = PowerBuyProductImageItemView.class.getSimpleName();

    private static final String ARG_CHILD_STATES = "childrenStates";
    private static final String ARG_PRODUCT_IMAGE_ITEM = "productDetailImageItem";

    //View Members
    @BindView(R.id.image_view)
    ImageView mImageView;

    //Data Members
    private ProductDetailImageItem productDetailImageItem;

    public PowerBuyProductImageItemView(Context context) {
        super(context);
        initInflate();
        initInstance();
    }

    public PowerBuyProductImageItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initInflate();
        initInstance();
    }

    public PowerBuyProductImageItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInflate();
        initInstance();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PowerBuyProductImageItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initInflate();
        initInstance();
    }

    private void initInflate() {
        //Inflate Layout
        inflate(getContext(), R.layout.list_item_product_detail_image_pager_item, this);
    }

    private void initInstance() {
        ButterKnife.bind(this);
    }

    public void setProductDetailImageItem(ProductDetailImageItem productDetailImageItem) {
        this.productDetailImageItem = productDetailImageItem;

        setInfo();
    }

    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        dispatchFreezeSelfOnly(container);
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        //Save Child State
        int id;
        Bundle childrenStates = new Bundle();
        for (int i = 0; i < getChildCount(); i++) {
            id = getChildAt(i).getId();
            if (id != 0) {
                SparseArray childrenState = new SparseArray();
                getChildAt(i).saveHierarchyState(childrenState);
                childrenStates.putSparseParcelableArray(String.valueOf(id), childrenState);
            }
        }
        Bundle bundle = new Bundle();
        bundle.putBundle(ARG_CHILD_STATES, childrenStates);
        bundle.putParcelable(ARG_PRODUCT_IMAGE_ITEM, productDetailImageItem);

        //Save it to Parcelable
        BundleSavedState ss = new BundleSavedState(superState);
        ss.setBundle(bundle);
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        BundleSavedState ss = (BundleSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        //Restore SparseArray

        Bundle bundle = ss.getBundle();
        Bundle childStates = bundle.getBundle(ARG_CHILD_STATES);

        //Restore Children's state
        int id;
        for (int i = 0; i < getChildCount(); i++) {
            id = getChildAt(i).getId();
            if (id != 0) {
                if (childStates.containsKey(String.valueOf(id))) {
                    SparseArray childrenState =
                            childStates.getSparseParcelableArray(String.valueOf(id));
                    getChildAt(i).restoreHierarchyState(childrenState);
                }
            }
        }

        //Restore State Here
        this.productDetailImageItem = bundle.getParcelable(ARG_PRODUCT_IMAGE_ITEM);
    }

    private void setInfo() {
        if (productDetailImageItem != null) {

            Glide.with(Contextor.getInstance().getContext())
                    .load(productDetailImageItem.getImgUrl())
//                    .error(R.drawable.ic_error_placeholder)
//                    .placeholder(R.drawable.ic_banner_placeholder)
                    .crossFade()
                    .fitCenter()
                    .into(mImageView);
            mImageView.setTag(productDetailImageItem);
        }
    }
}