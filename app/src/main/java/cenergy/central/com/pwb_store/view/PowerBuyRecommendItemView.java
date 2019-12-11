package cenergy.central.com.pwb_store.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.cardview.widget.CardView;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.Contextor;
import cenergy.central.com.pwb_store.manager.bus.event.RecommendBus;
import cenergy.central.com.pwb_store.model.BundleSavedState;
import cenergy.central.com.pwb_store.model.ProductRelatedList;

/**
 * Created by napabhat on 7/18/2017 AD.
 */

public class PowerBuyRecommendItemView extends FrameLayout implements View.OnClickListener{
    private static final String TAG = PowerBuyRecommendItemView.class.getSimpleName();

    private static final String ARG_CHILD_STATES = "childrenStates";
    private static final String ARG_PRODUCT_RECOMMEND_ITEM = "productList";

    @BindView(R.id.card_view_recommend)
    CardView mCardView;

    @BindView(R.id.img_product)
    ImageView mImgProduct;

    @BindView(R.id.txt_product_name)
    PowerBuyTextView mProductName;

    @BindView(R.id.txt_product_description)
    PowerBuyTextView mProductDescription;

    @BindView(R.id.txt_product_old_price)
    PowerBuyTextView oldPrice;

    @BindView(R.id.txt_product_new_price)
    PowerBuyTextView newPrice;

    //Data Members
    private ProductRelatedList productRelatedList;

    public PowerBuyRecommendItemView(Context context) {
        super(context);
        initInflate();
        initInstance();
    }

    public PowerBuyRecommendItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initInflate();
        initInstance();
    }

    public PowerBuyRecommendItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInflate();
        initInstance();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PowerBuyRecommendItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initInflate();
        initInstance();
    }

    private void initInflate() {
        //Inflate Layout
        inflate(getContext(), R.layout.list_item_product_recommend, this);
    }

    private void initInstance() {
        ButterKnife.bind(this);
    }

    public void setProductRecommendItem(ProductRelatedList productRelatedList) {
        this.productRelatedList = productRelatedList;

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
        bundle.putParcelable(ARG_PRODUCT_RECOMMEND_ITEM, productRelatedList);

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
        this.productRelatedList = bundle.getParcelable(ARG_PRODUCT_RECOMMEND_ITEM);
    }

    private void setInfo() {
        String unit = Contextor.getInstance().getContext().getString(R.string.baht);
        if (productRelatedList != null) {

            Glide.with(Contextor.getInstance().getContext())
                    .load(productRelatedList.getImgProduct())
                    //.placeholder(R.drawable.ic_error_placeholder)
                    .crossFade()
                    .fitCenter()
                    .into(mImgProduct);

            mProductName.setText(productRelatedList.getProductName());
            mProductDescription.setText(productRelatedList.getProductDescription());
            oldPrice.setText(productRelatedList.getDisplayOldPrice(unit));
            oldPrice.setEnableStrikeThrough(true);

            newPrice.setText(productRelatedList.getDisplayNewPrice(unit));

            mCardView.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mCardView){
            EventBus.getDefault().post(new RecommendBus(v,productRelatedList));
        }
    }
}
