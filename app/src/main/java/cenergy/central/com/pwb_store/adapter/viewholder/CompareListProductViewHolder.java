package cenergy.central.com.pwb_store.adapter.viewholder;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.CompareListAdapter;
import cenergy.central.com.pwb_store.adapter.decoration.SpacesItemDecoration;
import cenergy.central.com.pwb_store.model.CompareList;

/**
 * Created by napabhat on 7/26/2017 AD.
 */

public class CompareListProductViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    //Data Member
    private CompareListAdapter mCompareListAdapter;
    private GridLayoutManager mLayoutManager;

    public CompareListProductViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void setViewHolder(Context context, CompareList compareList){
        mCompareListAdapter = new CompareListAdapter(context);
        //mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        mLayoutManager = new GridLayoutManager(context, 4, LinearLayoutManager.VERTICAL, false);
        mLayoutManager.setReverseLayout(true);// Show Start Right.
        mRecyclerView.setHasFixedSize(true);
        if (compareList.getCompareProducts() == null) {
            mCompareListAdapter.setProductCompareList(compareList, compareList.getProductCompareLists());
        } else {
            // TBD- mockup
            mCompareListAdapter.setCompareProducts(compareList.getCompareProducts());
        }
        mLayoutManager.setSpanSizeLookup(mCompareListAdapter.getSpanSize());
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(0, LinearLayoutManager.VERTICAL));
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mCompareListAdapter);
    }
}
