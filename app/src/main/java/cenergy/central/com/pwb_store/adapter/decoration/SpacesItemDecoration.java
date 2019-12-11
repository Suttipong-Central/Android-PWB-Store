package cenergy.central.com.pwb_store.adapter.decoration;

import android.graphics.Rect;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by C.Thawanapong on 9/8/2016 AD.
 * Email chavit.t@th.zalora.com
 *
 * Use by Recycler view to add spacing between each view holder
 */

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private int orientation;

    public SpacesItemDecoration(int space, int orientation) {
        this.space = space;
        this.orientation = orientation;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;
        outRect.top = space;

        if (orientation == LinearLayoutManager.VERTICAL) {
            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }
        } else {
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.left = space;
            } else {
                outRect.left = 0;
            }
        }
    }
}
