package cenergy.central.com.pwb_store.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.Contextor;
import cenergy.central.com.pwb_store.model.Category;
import cenergy.central.com.pwb_store.model.ProductFilterHeader;
import cenergy.central.com.pwb_store.model.ProductFilterSubHeader;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 7/5/2017 AD.
 */

public class CategoryViewHolder extends RecyclerView.ViewHolder {

    private ImageView mImageView;
    private PowerBuyTextView mTextView;

    public CategoryViewHolder(View itemView) {
        super(itemView);
        mImageView = itemView.findViewById(R.id.image_view);
        mTextView = itemView.findViewById(R.id.txt_view);
    }

    public void setViewHolder(Category category) {
        if (!category.getImageURL().isEmpty()) {
            Glide.with(itemView.getContext())
                    .load(category.getImageURL())
                    .error(R.drawable.ic_category_placeholder)
                    .fitCenter()
                    .into(mImageView);
        } else {
            mImageView.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_category_placeholder));
        }

        mTextView.setText(category.getDepartmentName());
        itemView.setTag(category);
    }

    public void setViewHolder(ProductFilterHeader categoryHeader) {
        if (!categoryHeader.getImageURL().isEmpty()) {
            Glide.with(Contextor.getInstance().getContext())
                    .load(categoryHeader.getImageURL())
                    .error(R.drawable.ic_category_placeholder)
                    .fitCenter()
                    .into(mImageView);
        } else {
            mImageView.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_category_placeholder));
        }

        mTextView.setText(categoryHeader.getName());
        itemView.setTag(categoryHeader);
    }

    public void setViewHolder(ProductFilterSubHeader categorySubHeader) {
        if (!categorySubHeader.getUrlName().isEmpty()) {
            Glide.with(Contextor.getInstance().getContext())
                    .load(categorySubHeader.getUrlName())
                    .error(R.drawable.ic_category_placeholder)
                    .fitCenter()
                    .into(mImageView);
        } else {
            mImageView.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_category_placeholder));
        }

        mTextView.setText(categorySubHeader.getName());
        itemView.setTag(categorySubHeader);
    }
}
