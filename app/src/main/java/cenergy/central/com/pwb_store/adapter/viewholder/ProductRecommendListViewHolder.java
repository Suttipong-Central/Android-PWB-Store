package cenergy.central.com.pwb_store.adapter.viewholder;

import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.pager.RecommendAdapter;
import cenergy.central.com.pwb_store.model.Recommend;

/**
 * Created by napabhat on 7/18/2017 AD.
 */

public class ProductRecommendListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    @BindView(R.id.view_pager_recommend)
    ViewPager mViewPager;

    @BindView(R.id.iv_arrow_prev)
    ImageView mPrev;

    @BindView(R.id.iv_arrow_next)
    ImageView mNext;

    private RecommendAdapter mAdapter;

    public ProductRecommendListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setViewHolder(FragmentManager fragmentManager, Recommend reccommend) {
        mAdapter = new RecommendAdapter(fragmentManager, reccommend);
        mViewPager.setAdapter(mAdapter);
        mNext.setOnClickListener(this);
        mPrev.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mNext) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
        } else if (v == mPrev) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
        }
    }
}
