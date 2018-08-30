package cenergy.central.com.pwb_store.adapter.viewholder;

import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import org.greenrobot.eventbus.EventBus;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.activity.WebViewActivity;
import cenergy.central.com.pwb_store.fragment.WebViewFragment;
import cenergy.central.com.pwb_store.manager.bus.event.SpecDaoBus;
import cenergy.central.com.pwb_store.model.Overview;
import cenergy.central.com.pwb_store.model.SpecDao;

/**
 * Created by napabhat on 7/18/2017 AD.
 */

public class ProductSpecViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public ProductSpecViewHolder(View itemView) {
        super(itemView);
        LinearLayout mSpec = itemView.findViewById(R.id.layout_spec);
        mSpec.setOnClickListener(this);
    }

    public void setViewHolder(SpecDao specDao){
        itemView.setTag(specDao);
    }

    public void setViewHolder(Overview overview){
        itemView.setTag(overview);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.layout_spec){
            if(itemView.getTag() instanceof Overview){
                Overview overview = (Overview) itemView.getTag();
                openWebViewActivity(v, overview.getOverviewHTML());
            } else {
                SpecDao specDao = (SpecDao) itemView.getTag();
                EventBus.getDefault().post(new SpecDaoBus(v, specDao));
            }
        }
    }

    private void openWebViewActivity(View view, String overviewHTML) {
        Intent intent = new Intent(itemView.getContext(), WebViewActivity.class);
        intent.putExtra(WebViewActivity.ARG_WEB_URL, overviewHTML);
        intent.putExtra(WebViewActivity.ARG_MODE, WebViewFragment.MODE_HTML);
        intent.putExtra(WebViewActivity.ARG_TITLE, "Web");
        ActivityCompat.startActivity(itemView.getContext(), intent,
                ActivityOptionsCompat
                        .makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight())
                        .toBundle());
    }
}
