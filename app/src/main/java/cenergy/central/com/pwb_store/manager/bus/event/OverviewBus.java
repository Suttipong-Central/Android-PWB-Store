package cenergy.central.com.pwb_store.manager.bus.event;

import android.view.View;

import cenergy.central.com.pwb_store.model.Overview;
import cenergy.central.com.pwb_store.model.ReviewDetailText;

/**
 * Created by napabhat on 9/12/2017 AD.
 */

//public class OverviewBus {
//    private View view;
//    private ReviewDetailText mReviewDetailText;
//
//    public OverviewBus(View view, ReviewDetailText reviewDetailText){
//        this.view = view;
//        this.mReviewDetailText = reviewDetailText;
//    }
//
//    public View getView() {
//        return view;
//    }
//
//    public ReviewDetailText getReviewDetailText() {
//        return mReviewDetailText;
//    }
//}
public class OverviewBus {
    private View view;
    private Overview overview;

    public OverviewBus(View view, Overview overview){
        this.view = view;
        this.overview = overview;
    }

    public View getView() {
        return view;
    }

    public Overview getOverview() {
        return overview;
    }
}
