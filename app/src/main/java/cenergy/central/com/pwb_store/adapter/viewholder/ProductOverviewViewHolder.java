package cenergy.central.com.pwb_store.adapter.viewholder;

import android.content.Intent;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.activity.WebViewActivity;
import cenergy.central.com.pwb_store.fragment.WebViewFragment;
import cenergy.central.com.pwb_store.model.Overview;
import cenergy.central.com.pwb_store.model.ReviewDetailText;

/**
 * Created by napabhat on 7/18/2017 AD.
 */

public class ProductOverviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ProductOverviewViewHolder(View itemView) {
        super(itemView);
        LinearLayout overViewLayout = itemView.findViewById(R.id.layout_overview);
        overViewLayout.setOnClickListener(this);
    }

    public void setViewHolder(ReviewDetailText reviewDetailText) {
        itemView.setTag(reviewDetailText);
    }

    public void setViewHolder(Overview productOverview) {
        itemView.setTag(productOverview);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.layout_overview) {
            if (itemView.getTag() instanceof Overview) {
                Overview overview = (Overview) itemView.getTag();
                openWebViewActivity(v, overview.getOverviewHTML());
            } else {
                ReviewDetailText reviewDetailText = (ReviewDetailText) itemView.getTag();
                openWebViewActivity(v, reviewDetailText.getHtml());
            }
        }
    }

    private void openWebViewActivity(View view, String html) {
        Intent intent = new Intent(itemView.getContext(), WebViewActivity.class);
        intent.putExtra(WebViewActivity.ARG_WEB_URL, html);
        intent.putExtra(WebViewActivity.ARG_MODE, WebViewFragment.MODE_HTML);
        intent.putExtra(WebViewActivity.ARG_TITLE, "Web");
        ActivityCompat.startActivity(itemView.getContext(), intent,
                ActivityOptionsCompat
                        .makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight())
                        .toBundle());
    }
}
