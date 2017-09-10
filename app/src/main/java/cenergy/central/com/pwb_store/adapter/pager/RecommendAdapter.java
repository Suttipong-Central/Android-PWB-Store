package cenergy.central.com.pwb_store.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

import cenergy.central.com.pwb_store.fragment.FragmentTemplate;
import cenergy.central.com.pwb_store.fragment.RecommendFragment;
import cenergy.central.com.pwb_store.model.FragmentType;
import cenergy.central.com.pwb_store.model.IFragmentType;
import cenergy.central.com.pwb_store.model.ProductRelatedList;
import cenergy.central.com.pwb_store.model.Recommend;

/**
 * Created by napabhat on 7/18/2017 AD.
 */

public class RecommendAdapter extends FragmentStatePagerAdapter {
    //Static Members
    private static final int FRAGMENT_TYPE_ID_RECOMMEND_ITEM = 0;

    //Data Members
    private List<List<ProductRelatedList>> mPartition;
    private List<IFragmentType> mFragmentTypeList = new ArrayList<>();

    public RecommendAdapter(FragmentManager fm, Recommend recommend) {
        super(fm);
        mPartition = Lists.partition(recommend.getProductRelatedLists(), 3);

        for (List<ProductRelatedList> productRelatedLists : mPartition) {
            mFragmentTypeList.add(new FragmentType(FRAGMENT_TYPE_ID_RECOMMEND_ITEM, "Recommend"));
        }
    }

    @Override
    public Fragment getItem(int position) {
        long fragmentId = mFragmentTypeList.get(position).getFragmentTypeId();

        if (fragmentId == FRAGMENT_TYPE_ID_RECOMMEND_ITEM) {
            return RecommendFragment.newInstance(new ArrayList<>(mPartition.get(position)));
        }
        return FragmentTemplate.newInstance();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTypeList.get(position).getFragmentTitle();
    }

    @Override
    public int getCount() {
        return mFragmentTypeList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }
}