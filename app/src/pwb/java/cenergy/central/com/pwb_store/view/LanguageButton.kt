package cenergy.central.com.pwb_store.view

import android.content.Context
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage

class LanguageButton : RadioGroup {

    // widget view
    private lateinit var languageToggleGroup: RadioGroup
    private lateinit var thaiToggle: RadioButton
    private lateinit var engToggle: RadioButton

    // callback
    private var listener: LanguageListener? = null

    constructor(context: Context) : super(context) {
        prepareView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    fun setOnLanguageChangeListener(listener: LanguageListener) {
        this.listener = listener
    }

    fun setDefaultLanguage(lang: String) {
        when (lang) {
            AppLanguage.TH.key -> {
                thaiToggle.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                engToggle.setTextColor(ContextCompat.getColor(context, R.color.white))
                thaiToggle.isChecked = true
            }
            AppLanguage.EN.key -> {
                engToggle.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                thaiToggle.setTextColor(ContextCompat.getColor(context, R.color.white))
                engToggle.isChecked = true
            }
        }
    }

    private fun prepareView() {
        val view = View.inflate(context, R.layout.view_switch_language, this)
        languageToggleGroup = view.findViewById(R.id.toggle_language)
        thaiToggle = view.findViewById(R.id.lang_th)
        engToggle = view.findViewById(R.id.lang_en)

        languageToggleGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.lang_th -> {
                    thaiToggle.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    engToggle.setTextColor(ContextCompat.getColor(context, R.color.white))

                    listener?.onChangedLanguage(AppLanguage.TH) // callback
                }
                R.id.lang_en -> {
                    engToggle.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    thaiToggle.setTextColor(ContextCompat.getColor(context, R.color.white))

                    listener?.onChangedLanguage(AppLanguage.EN) // callback
                }
            }
        }
    }

    private fun init() {
        prepareView()
        notifyAttributeChanged()
    }

    private fun notifyAttributeChanged() {

    }

    interface LanguageListener {
        fun onChangedLanguage(lang: AppLanguage)
    }
}