package cenergy.central.com.pwb_store.dialogs.interfaces

import org.joda.time.DateTime

interface PickDateListener{
    fun onDatePickerListener(dateTime: DateTime)
}