package cenergy.central.com.pwb_store.model;

/**
 * Created by C.Thawanapong on 9/7/2016 AD.
 * Email chavit.t@th.zalora.com
 */

public class FragmentType implements IFragmentType {
    private long mFragmentType;
    private String mFragmentTitle;

    public FragmentType(long mFragmentType, String mFragmentTitle) {
        this.mFragmentType = mFragmentType;
        this.mFragmentTitle = mFragmentTitle;
    }

    @Override
    public long getFragmentTypeId() {
        return mFragmentType;
    }

    @Override
    public void setFragmentTypeId(long fragmentTypeId) {
        this.mFragmentType = fragmentTypeId;
    }

    @Override
    public String getFragmentTitle() {
        return mFragmentTitle;
    }
}