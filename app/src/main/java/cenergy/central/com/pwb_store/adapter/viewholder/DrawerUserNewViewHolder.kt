package cenergy.central.com.pwb_store.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.LanguageListener
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.view.PowerBuyTextView

/**
 * Created by napabhat on 9/6/2017 AD.
 */
class DrawerUserNewViewHolder(itemView: View, private val languageListener: LanguageListener?,
                              private val preferenceManager: PreferenceManager) : RecyclerView.ViewHolder(itemView) {

    private val imgProfile: ImageView = itemView.findViewById(R.id.image_view_profile)
    private val fullName: PowerBuyTextView = itemView.findViewById(R.id.txt_view_full_name)
    private val storeName: PowerBuyTextView = itemView.findViewById(R.id.txt_store)
    private val languageToggleGroup: RadioGroup = itemView.findViewById(R.id.toggle_language)
    private val thaiToggle: RadioButton = itemView.findViewById(R.id.lang_th)
    private val engToggle: RadioButton = itemView.findViewById(R.id.lang_en)

    fun setViewHolder() {
        val userInformation = RealmController.getInstance().userInformation
//        imgProfile.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_person))
        fullName.text = userInformation.user?.name ?: ""
        if (userInformation.store != null) {
            storeName.text = userInformation.store!!.storeName ?: ""
        }

        // setup toggle
        Log.d("Lang", preferenceManager.getDefaultLanguage())
        thaiToggle.isChecked = preferenceManager.getDefaultLanguage() == AppLanguage.TH.key
        engToggle.isChecked = preferenceManager.getDefaultLanguage() == AppLanguage.EN.key

        languageToggleGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.lang_th -> {languageListener?.onChangedLanguage(AppLanguage.TH)}
                R.id.lang_en -> {languageListener?.onChangedLanguage(AppLanguage.EN)}
            }
        }
    }
}
