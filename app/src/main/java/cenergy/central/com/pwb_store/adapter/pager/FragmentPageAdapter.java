package cenergy.central.com.pwb_store.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cenergy.central.com.pwb_store.fragment.CategoryFragment;
import cenergy.central.com.pwb_store.fragment.FragmentTemplate;
import cenergy.central.com.pwb_store.fragment.ProductListFragment;
import cenergy.central.com.pwb_store.fragment.PromotionPaymentFragment;
import cenergy.central.com.pwb_store.fragment.PromotionProductFragment;
import cenergy.central.com.pwb_store.model.FragmentType;
import cenergy.central.com.pwb_store.model.IFragmentType;
import cenergy.central.com.pwb_store.model.ProductDetail;

/**
 * Created by napabhat on 8/2/2017 AD.
 */

public class FragmentPageAdapter extends FragmentPagerAdapter {
    private static final String TAG = FragmentPageAdapter.class.getSimpleName();

    //Static Members
    private static final long FRAGMENT_TYPE_ID_PROMOTION_PAYMENT = 0;
    private static final long FRAGMENT_TYPE_ID_PROMOTION_PRODUCT = 1;

    //Data Members
    private List<IFragmentType> mFragmentTypeList = new ArrayList<>();
    private ProductDetail mProductDetail;

    public FragmentPageAdapter(FragmentManager fm , ProductDetail productDetail) {
        super(fm);
        this.mProductDetail = productDetail;
        mFragmentTypeList.add(new FragmentType(FRAGMENT_TYPE_ID_PROMOTION_PAYMENT, "Payment"));
        mFragmentTypeList.add(new FragmentType(FRAGMENT_TYPE_ID_PROMOTION_PRODUCT, "Product"));
    }

    @Override
    public Fragment getItem(int position) {
        long fragmentId = getItemId(position);

        if (fragmentId == FRAGMENT_TYPE_ID_PROMOTION_PAYMENT){
            return PromotionPaymentFragment.newInstance(mProductDetail);
        } else if (fragmentId == FRAGMENT_TYPE_ID_PROMOTION_PRODUCT){
            return PromotionProductFragment.newInstance(mProductDetail);
        }
        Log.e(TAG, "getItem: No matching fragmentId");
        return FragmentTemplate.newInstance();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTypeList.get(position).getFragmentTitle();
    }

    @Override
    public long getItemId(int position) {
        return mFragmentTypeList.get(position).getFragmentTypeId();
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
