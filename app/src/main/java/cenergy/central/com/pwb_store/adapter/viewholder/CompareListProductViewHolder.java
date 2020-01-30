package cenergy.central.com.pwb_store.adapter.viewholder;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.CompareListAdapter;
import cenergy.central.com.pwb_store.adapter.decoration.SpacesItemDecoration;
import cenergy.central.com.pwb_store.model.CompareList;

/**
 * Created by napabhat on 7/26/2017 AD.
 */

public class CompareListProductViewHolder extends RecyclerView.ViewHolder {

    private RecyclerView mRecyclerView;

    public CompareListProductViewHolder(View itemView) {
        super(itemView);
        mRecyclerView = itemView.findViewById(R.id.recycler_view);
    }

    public void setViewHolder(Context context, CompareList compareList){
        //Data Member
        CompareListAdapter mCompareListAdapter = new CompareListAdapter(context);
        //mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        GridLayoutManager mLayoutManager = new GridLayoutManager(context, 4, LinearLayoutManager.VERTICAL, false);
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
