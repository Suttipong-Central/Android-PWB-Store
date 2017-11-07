package cenergy.central.com.pwb_store.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.Contextor;
import cenergy.central.com.pwb_store.manager.bus.event.CategoryBus;
import cenergy.central.com.pwb_store.model.Category;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by napabhat on 7/5/2017 AD.
 */

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    @BindView(R.id.image_view)
    ImageView mImageView;

    @BindView(R.id.txt_view)
    PowerBuyTextView mTextView;

    public CategoryViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setViewHolder(Category category) {
        Glide.with(Contextor.getInstance().getContext())
                .load(category.getImageURL())
                .placeholder(R.drawable.ic_circle_grey_placeholder)
                .crossFade()
                //.fitCenter()
                .bitmapTransform(new CropCircleTransformation(Contextor.getInstance().getContext()))
                .into(mImageView);

        mTextView.setText(category.getDepartmentName());
        itemView.setTag(category);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Category category = (Category) itemView.getTag();
        EventBus.getDefault().post(new CategoryBus(category, itemView));
    }
}
