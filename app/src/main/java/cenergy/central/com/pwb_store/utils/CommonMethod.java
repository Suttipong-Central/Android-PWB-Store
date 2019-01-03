package cenergy.central.com.pwb_store.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CommonMethod {
	private static final String TAG = CommonMethod.class.getSimpleName();

	public static String convertWeekDays(String date) {
		Log.d(TAG, " Date: " + date);
		String formattedDate = null;
		try {
			SimpleDateFormat originalFormat = new SimpleDateFormat(
					"yyyy-MM-dd", Locale.ENGLISH);
			SimpleDateFormat targetFormat = new SimpleDateFormat("dd");
			Date date12 = originalFormat.parse(date);
			formattedDate = targetFormat.format(date12);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return formattedDate;

	}

	
	
	public static String convertWeekDaysMouth(String date, Locale locale)
	{
		String formattedDate = null;
		try
			{
				SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd" , locale);
				SimpleDateFormat targetFormat = new SimpleDateFormat("MMM yyyy", locale);
				Date date12 = originalFormat.parse(date);
				formattedDate = targetFormat.format(date12);
			} catch (Exception e)
			{
				e.printStackTrace();
			}

		return formattedDate;

	}

	public static String convertMonth(String date) {
		Log.d(TAG, " Date: " + date);
		String formattedDate = null;
		try {
			SimpleDateFormat originalFormat = new SimpleDateFormat(
					"yyyy-MM-dd", Locale.ENGLISH);
			SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM");
			Date date12 = originalFormat.parse(date);
			formattedDate = targetFormat.format(date12);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return formattedDate;

	}

}
