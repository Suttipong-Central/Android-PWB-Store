package cenergy.central.com.pwb_store.view;

import android.animation.Animator;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.model.BundleSavedState;
import cenergy.central.com.pwb_store.model.CompareCount;
import io.realm.Realm;

public class PowerBuyShoppingCartView extends FrameLayout {
    private static final String TAG = "PowerBuyShoppingCartView";

    private static final String ARG_CHILD_STATES = "ARG_CHILD_STATES";
    private static final String ARG_SHOPPING_CART_ITEM_COUNT = "ARG_SHOPPING_CART_ITEM_COUNT";
    private static final String ARG_SHOPPING_CART_COUNT = "ARG_SHOPPING_CART_COUNT";

    //View Members
    RelativeLayout mLayoutCart;
    ViewGroup mLayoutBadge;
    TickerView mBadgeCart;

    //Data Members
    private int mCompareItemCount;
    private OnClickListener mListener;
    private CompareCount mCompareCount;
    private boolean isAddCart;
    private Realm mRealm;

    public PowerBuyShoppingCartView(@NonNull Context context) {
        super(context);
        initInflate();
        initInstance();
    }

    public PowerBuyShoppingCartView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initInflate();
        initInstance();
    }

    public PowerBuyShoppingCartView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInflate();
        initInstance();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PowerBuyShoppingCartView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initInflate();
        initInstance();
    }

    private void initInflate() {
        //Inflate Layout
        View view = inflate(getContext(), R.layout.view_button_shopping_cart, this);

        // bind view
        mLayoutBadge = view.findViewById(R.id.layout_badge);
        mLayoutCart = view.findViewById(R.id.layout_cart);
        mBadgeCart = view.findViewById(R.id.txt_view_badge_shopping_cart);
        view.findViewById(R.id.image_view_shopping_cart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onAddShoppingCartClick(v);
                }
            }
        });
    }

    private void initInstance() {
        mBadgeCart.setCharacterList(TickerUtils.getDefaultNumberList());
        //this.mRealm = RealmController.getInstance().getRealm();
    }

    public void setBadgeCart(int count) {
        this.mCompareItemCount = count;
        setInfo();
    }

    private void setInfo() {
        mBadgeCart.setText(String.valueOf(mCompareItemCount));
        if (mCompareItemCount == 0) {
            mLayoutBadge.animate()
                    .alpha(0.0f)
                    .setDuration(300)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            mBadgeCart.setVisibility(INVISIBLE);
                            mLayoutBadge.setVisibility(INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });
            mLayoutBadge.setVisibility(INVISIBLE);
            mBadgeCart.setVisibility(INVISIBLE);
        } else {
            mLayoutBadge.animate().alpha(1.0f)
                    .setDuration(300)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                            mBadgeCart.setVisibility(VISIBLE);
                            mLayoutBadge.setVisibility(VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            mLayoutBadge.clearAnimation();
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });
            mLayoutBadge.setVisibility(VISIBLE);
            mBadgeCart.setVisibility(VISIBLE);
        }
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
        bundle.putInt(ARG_SHOPPING_CART_ITEM_COUNT, mCompareItemCount);

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
        this.mCompareItemCount = bundle.getInt(ARG_SHOPPING_CART_ITEM_COUNT);
        this.mCompareCount = bundle.getParcelable(ARG_SHOPPING_CART_COUNT);
    }

    public void updateCartCount(int count) {
        setBadgeCart(count);
//        HttpManager.getInstance().getCartService().getCartCount().enqueue(new Callback<CartCountDao>() {
//            @Override
//            public void onResponse(Call<CartCountDao> call, Response<CartCountDao> response) {
//                if (response.isSuccessful()) {
//                    CartCountDao cartCountDao = response.body();
//                    CartCount cartCount = cartCountDao.getCartCount();
//                    setBadgeCart(cartCount.getCount());
//                } else {
//                    //Parse Error Body
//                    setBadgeCart(0);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<CartCountDao> call, Throwable t) {
//                //TODO Handle no internet
//            }
//        });
//
//        //mCartCount.setCount(mCartItemCount);
////        HttpManager.getInstance().getCartService().getCartCount().enqueue(new Callback<CartCountDao>() {
////            @Override
////            public void onResponse(Call<CartCountDao> call, Response<CartCountDao> response) {
////                if (response.isSuccessful()) {
////                    CartCountDao cartCountDao = response.body();
////                    CartCount cartCount = cartCountDao.getCartCount();
////                    setBadgeCart(cartCount.getCount());
////                } else {
////                    //Parse Error Body
////                    setBadgeCart(0);
////                }
////            }
////
////            @Override
////            public void onFailure(Call<CartCountDao> call, Throwable t) {
////                //TODO Handle no internet
////            }
////        });
    }

    public void setListener(OnClickListener listener) {
        this.mListener = listener;
    }

    public interface OnClickListener {
        void onAddShoppingCartClick(View view);
    }
}