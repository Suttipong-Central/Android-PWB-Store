package cenergy.central.com.pwb_store.adapter.viewholder;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.CompareDetailAdapter;
import cenergy.central.com.pwb_store.adapter.decoration.SpacesItemDecoration;
import cenergy.central.com.pwb_store.model.CompareDao;
import cenergy.central.com.pwb_store.model.CompareDetailItem;

/**
 * Created by napabhat on 7/31/2017 AD.
 */

public class CompareDetailViewHolder extends RecyclerView.ViewHolder{

//    @BindView(R.id.grid_view)
//    GridView mGridView;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private CompareDetailAdapter mAdapter;
    private GridLayoutManager mLayoutManager;
    private List<CompareDetailItem> mCompareDetailItems;

    public CompareDetailViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setViewHolder(Context context, CompareDao compareDao){
//        for (CompareDetail compareDetail : compareDao.getCompareDetails()){
//            mCompareDetailItems = compareDetail.getCompareDetailItems();
//        }
//        mAdapter = new CompareDetailBaseAdapter(context, (ArrayList<CompareDetail>) compareDao.getCompareDetails(), (ArrayList<CompareDetailItem>) mCompareDetailItems);
//       mGridView.setAdapter(mAdapter);

        mAdapter = new CompareDetailAdapter(context);

        mLayoutManager = new GridLayoutManager(context, 4, LinearLayoutManager.VERTICAL, false);
        mAdapter.setCompareDetail(compareDao);

        mLayoutManager.setSpanSizeLookup(mAdapter.getSpanSize());
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(0, LinearLayoutManager.VERTICAL));
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(mScrollTouchListener);
        mRecyclerView.setNestedScrollingEnabled(false);
    }

    RecyclerView.OnItemTouchListener mScrollTouchListener = new RecyclerView.OnItemTouchListener() {
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            int action = e.getAction();
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    rv.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    };
}
