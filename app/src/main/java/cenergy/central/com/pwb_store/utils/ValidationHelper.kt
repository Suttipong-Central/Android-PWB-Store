package cenergy.central.com.pwb_store.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Patterns
import cenergy.central.com.pwb_store.R
import java.util.regex.Pattern

/**
 * Created by Anuphap Suwannamas on 13/9/2018 AD.
 * Email: Anupharpae@gmail.com
 */
class ValidationHelper(private val context: Context) {

    fun validText(text: String): String? {
        return if (isEmptyData(text)) {
            context.getString(R.string.error_form_empty_data)
        } else null
    }

    fun validName(text: String, isThai: Boolean): String? {
        return when {
            isEmptyData(text) -> context.getString(R.string.error_form_empty_data)
            isSpecialCharacter(text) -> context.getString(R.string.error_form_name)
            Pattern.compile(if (isThai) "[0-9]" else "[^ a-z]", Pattern.CASE_INSENSITIVE).matcher(text).find() ->
                context.getString(if (isThai) R.string.error_form_name else R.string.error_form_name_en)
            else -> null
        }
    }

    private fun isSpecialCharacter(s: String): Boolean {
        val specialChars = "/*!@#$%^&*()\"{}_[]|\\?/<>,.0987654321"
        for (i in 0 until s.length) {
            if (specialChars.contains(s[i].toString())) {
                return true
            }
        }
        return false
    }

    fun getLanguage(s: String): String {
        if (Pattern.compile("[^ a-z]", Pattern.CASE_INSENSITIVE).matcher(s).find()) {
            for (i in 0 until Character.codePointCount(s, 0, s.length)) {
                val c = s.codePointAt(i)
                if (s[i].toString() != " ") {
                    if (c < 0x0E00 || c > 0x0E7F) {
                        return "none"
                    }
                }
            }
            return LANGUAGE_THAI
        }
        return LANGUAGE_ENGLISH
    }


    fun validPassport(passport: String): String? {
        return when {
            isEmptyData(passport) -> context.getString(R.string.error_form_empty_data)
            passport.length < MIN_PASSPORT_LENGTH -> context.getString(R.string.error_form_passport_invalid)
            Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE).matcher(passport).find() -> context.getString(R.string.error_form_passport_invalid)
            else -> null
        }
    }

    fun validEmail(email: String): String? {
        if (isEmptyData(email)) {
            return context.getString(R.string.error_form_empty_data)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return context.getString(R.string.error_form_email_pattern_not_match)
        }
        return null
    }

    fun validPassword(password: String): String? {
        if (isEmptyData(password)) {
            return context.getString(R.string.error_form_empty_data)
        } else if (password.length < MIN_PASSWORD_LENGTH) {
            return String.format(context.getString(R.string.error_form_password_too_short), MIN_PASSWORD_LENGTH)
        } else if (!isValidPassword(password)) {
            return context.getString(R.string.error_form_password_not_correctly)
        }
        return null
    }

    fun validMatchPassword(password: String, confirmationPassword: String): String? {
        return if (password != confirmationPassword) {
            context.getString(R.string.error_form_confirmation_password_not_matched)
        } else null
    }

    private fun isValidPassword(password: String): Boolean {
        val specialCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE)
        val upperCasePatten = Pattern.compile("[A-Z ]")
        val lowerCasePatten = Pattern.compile("[a-z ]")
        val digitCasePatten = Pattern.compile("[0-9 ]")

        if (specialCharPatten.matcher(password).find()) {
            return false
        } else if (!upperCasePatten.matcher(password).find()) {
            return false
        } else if (!lowerCasePatten.matcher(password).find()) {
            return false
        } else if (!digitCasePatten.matcher(password).find()) {
            return false
        }

        return true
    }

    fun validThaiPhoneNumber(phoneNumber: String): String? {
        if (phoneNumber.isBlank()) {
            return context.getString(R.string.error_form_empty_data)
        }

        val prefixNumber = phoneNumber.substring(0, 2)
        if (isEmptyData(phoneNumber)) {
            return context.getString(R.string.error_form_empty_data)
        } else if (phoneNumber.length < MIN_PHONE_NUMBER) {
            return context.getString(R.string.error_form_phone_number_invalid)
        } else if (!(prefixNumber == "06" || prefixNumber == "08" || prefixNumber == "09")) {
            return context.getString(R.string.error_form_phone_number_invalid)
        }
        return null
    }

    private fun isEmptyData(data: String?): Boolean {
        return data == null || data.trim { it <= ' ' } == ""
    }

    fun validTax(taxId: String): String? {
        if (isEmptyData(taxId)) {
            return context.getString(R.string.error_form_empty_data)
        } else if (taxId.length < MIN_TAX_ID) {
            return context.getString(R.string.error_tax_invalid)
        }
        return null
    }

    fun validTheOne(theOne: String) : String?{
        return if (!isEmptyData(theOne)){
            if (theOne.length == THE_ONE_NUMBER_LENGTH){
                null
            } else {
                context.getString(R.string.the_1_error)
            }
        } else null
    }

    companion object {
        private const val MIN_PASSWORD_LENGTH = 9
        private const val MIN_PHONE_NUMBER = 10
        private const val MIN_TAX_ID = 13
        private const val MIN_PASSPORT_LENGTH = 5
        private const val THE_ONE_NUMBER_LENGTH = 10
        private const val LANGUAGE_THAI = "th"
        private const val LANGUAGE_ENGLISH = "en"

        @SuppressLint("StaticFieldLeak")
        private var mInstance: ValidationHelper? = null

        @Synchronized
        fun getInstance(context: Context): ValidationHelper {
            if (mInstance == null) {
                mInstance = ValidationHelper(context)
            }
            return mInstance as ValidationHelper
        }
    }

}
