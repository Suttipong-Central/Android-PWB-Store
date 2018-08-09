package cenergy.central.com.pwb_store.adapter.viewholder;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.pager.FragmentPageAdapter;
import cenergy.central.com.pwb_store.manager.Contextor;
import cenergy.central.com.pwb_store.model.ProductDetail;
import cenergy.central.com.pwb_store.view.PowerBuyTabLayout;

/**
 * Created by napabhat on 7/18/2017 AD.
 */

public class ProductPromotionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private LinearLayout mPromotion;
    private PowerBuyTabLayout mTabLayout;
    private ViewPager mViewPager;
    private NestedScrollView mNestedScrollView;
    private ImageView left;
    private ImageView right;

    private FragmentPageAdapter mPageAdapter;
    private ProductDetail mProductDetail;

    public ProductPromotionViewHolder(View itemView) {
        super(itemView);
        mPromotion = itemView.findViewById(R.id.layout_promotion);
        mTabLayout = itemView.findViewById(R.id.tab_layout);
        mViewPager = itemView.findViewById(R.id.viewpager_main);
        mNestedScrollView = itemView.findViewById(R.id.nest_scroll);
        left = itemView.findViewById(R.id.left_nav);
        right = itemView.findViewById(R.id.right_nav);
    }

    public void setViewHolder(FragmentManager fragmentManager, ProductDetail productDetail){
        this.mProductDetail = productDetail;
        mPageAdapter = new FragmentPageAdapter(fragmentManager, productDetail);
        mViewPager.setAdapter(mPageAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        left.setColorFilter(ContextCompat.getColor(Contextor.getInstance().getContext(), R.color.blackText));
        mNestedScrollView.setFillViewport(true);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        if (v == right) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
            right.setColorFilter(ContextCompat.getColor(Contextor.getInstance().getContext(), R.color.blackText));
            left.setColorFilter(ContextCompat.getColor(Contextor.getInstance().getContext(), R.color.graySelect));
        } else if (v == left) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
            left.setColorFilter(ContextCompat.getColor(Contextor.getInstance().getContext(), R.color.blackText));
            right.setColorFilter(ContextCompat.getColor(Contextor.getInstance().getContext(), R.color.graySelect));
        }
    }
}
