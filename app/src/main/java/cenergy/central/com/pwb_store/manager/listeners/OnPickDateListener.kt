package cenergy.central.com.pwb_store.manager.listeners

import org.joda.time.DateTime

interface OnPickDateListener{
    fun onDatePickerListener(dateTime: DateTime)
}