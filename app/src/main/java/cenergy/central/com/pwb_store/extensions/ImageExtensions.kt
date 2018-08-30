package cenergy.central.com.pwb_store.extensions

import android.support.annotation.DrawableRes
import android.widget.ImageView
import cenergy.central.com.pwb_store.R
import com.bumptech.glide.Glide

/**
 * Created by Anuphap Suwannamas on 30/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

fun ImageView.setImage(@DrawableRes res: Int) {
    this.setImageResource(res)
}

fun ImageView.setImageUrl(url: String) {
    Glide.with(this.context)
            .load(url)
            .placeholder(R.drawable.ic_pwb_logo_detail)
            .crossFade()
            .fitCenter()
            .into(this)
}

fun ImageView.setImageUrl(url: String, @DrawableRes placeHolder: Int) {
    Glide.with(this.context)
            .load(url)
            .placeholder(R.drawable.ic_pwb_logo_detail)
            .crossFade()
            .fitCenter()
            .into(this)
}