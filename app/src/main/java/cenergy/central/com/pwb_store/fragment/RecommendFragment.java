package cenergy.central.com.pwb_store.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.model.ProductRelatedList;
import cenergy.central.com.pwb_store.view.PowerBuyRecommendItemView;


/**
 * Created by napabhat on 7/18/2017 AD.
 */

@SuppressWarnings("unused")
public class RecommendFragment extends Fragment {
    private static final String TAG = "RecommendFragment";
    private static final String ARG_PRODUCT_RECOMMEND_ITEM_LIST = "ARG_PRODUCT_RECOMMEND_ITEM_LIST";

    //View Members
    @BindViews({R.id.item_01, R.id.item_02, R.id.item_03})
    List<PowerBuyRecommendItemView> mPowerBuyRecommendItemViews;

    //Data Members
    private ArrayList<ProductRelatedList> mProductRelatedLists;

    public RecommendFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static RecommendFragment newInstance(ArrayList<ProductRelatedList> productRelatedLists) {
        RecommendFragment fragment = new RecommendFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PRODUCT_RECOMMEND_ITEM_LIST, productRelatedLists);
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
        View rootView = inflater.inflate(R.layout.fragment_recommend_item, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        if (getArguments() != null) {
            mProductRelatedLists = getArguments().getParcelableArrayList(ARG_PRODUCT_RECOMMEND_ITEM_LIST);
        }
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        ButterKnife.bind(this, rootView);

        for (int i = 0; i < mPowerBuyRecommendItemViews.size(); i++) {
            PowerBuyRecommendItemView powerBuyRecommendItemView = mPowerBuyRecommendItemViews.get(i);
            try {
                powerBuyRecommendItemView.setProductRecommendItem(mProductRelatedLists.get(i));
            } catch (IndexOutOfBoundsException ie) {
                powerBuyRecommendItemView.setVisibility(View.INVISIBLE);
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
        outState.putParcelableArrayList(ARG_PRODUCT_RECOMMEND_ITEM_LIST, mProductRelatedLists);
    }

    /*
     * Restore Instance State Here
     */
    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
        mProductRelatedLists = savedInstanceState.getParcelableArrayList(ARG_PRODUCT_RECOMMEND_ITEM_LIST);
    }

}
