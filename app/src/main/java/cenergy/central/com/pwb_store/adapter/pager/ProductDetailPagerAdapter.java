package cenergy.central.com.pwb_store.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

import cenergy.central.com.pwb_store.fragment.FragmentTemplate;
import cenergy.central.com.pwb_store.fragment.ProductDetailImagePagerFragment;
import cenergy.central.com.pwb_store.model.FragmentType;
import cenergy.central.com.pwb_store.model.IFragmentType;
import cenergy.central.com.pwb_store.model.ProductDetailImage;
import cenergy.central.com.pwb_store.model.ProductDetailImageItem;

/**
 * Created by napabhat on 10/31/2016 AD.
 */

public class ProductDetailPagerAdapter extends FragmentStatePagerAdapter {

    //Static Members
    private static final int FRAGMENT_TYPE_ID_PRODUCT_IMAGE_ITEM = 0;

    //Data Members
    private List<List<ProductDetailImageItem>> mPartition;
    private List<IFragmentType> mFragmentTypeList = new ArrayList<>();
    private ProductDetailImage mProductDetailImage;

    public ProductDetailPagerAdapter(FragmentManager fm, ProductDetailImage productDetailImage) {
        super(fm);
        mPartition = Lists.partition(productDetailImage.getProductDetailImageItems(), 3);

        for (List<ProductDetailImageItem> productDetailImageItems : mPartition) {
            mFragmentTypeList.add(new FragmentType(FRAGMENT_TYPE_ID_PRODUCT_IMAGE_ITEM, "ProductDetailImageItem"));
        }

        mProductDetailImage = productDetailImage;
    }

    @Override
    public Fragment getItem(int position) {
        long fragmentId = mFragmentTypeList.get(position).getFragmentTypeId();

        if (fragmentId == FRAGMENT_TYPE_ID_PRODUCT_IMAGE_ITEM) {
            return ProductDetailImagePagerFragment.newInstance(new ArrayList<>(mPartition.get(position)), mProductDetailImage);
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
