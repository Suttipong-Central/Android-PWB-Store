package cenergy.central.com.pwb_store.extensions

import android.content.Context
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.widget.ImageView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager
import com.bumptech.glide.Glide

fun ImageView.setImage(@DrawableRes res: Int) {
    this.setImageResource(res)
}

fun ImageView.setImageUrl(url: String) {
    Glide.with(context)
            .load(url)
            .placeholder(R.drawable.ic_pwb_logo_detail)
            .fitCenter()
            .into(this)
}

fun ImageView.setImageUrl(context: Context, url: String) {
    Glide.with(context)
            .load(url)
            .placeholder(R.drawable.ic_pwb_logo_detail)
            .crossFade()
            .fitCenter()
            .into(this)
}

fun ImageView.setImageUrl(url: String, @DrawableRes placeHolder: Int) {
    Glide.with(context)
            .load(url)
            .placeholder(placeHolder)
            .crossFade()
            .fitCenter()
            .into(this)
}

fun ImageView.set2HourBadge(){
    val preferenceManager = PreferenceManager(this.context)
    val language = preferenceManager.getDefaultLanguage()
    this.setImageDrawable(
            if (language == "th") {
                ContextCompat.getDrawable(this.context ,R.drawable.ic_2h_badge_th)
            } else {
                ContextCompat.getDrawable(this.context ,R.drawable.ic_2h_badge_en)
            })
}