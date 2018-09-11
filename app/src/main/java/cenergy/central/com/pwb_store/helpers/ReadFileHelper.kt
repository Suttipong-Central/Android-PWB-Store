package cenergy.central.com.pwb_store.helpers

import android.content.Context
import android.support.annotation.RawRes
import cenergy.central.com.pwb_store.Constants
import com.google.gson.GsonBuilder
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.reflect.Type

/**
 * Created by Anuphap Suwannamas on 11/9/2018 AD.
 * Email: Anupharpae@gmail.com
 */

class ReadFileHelper<T> {

    fun readRawFile(context: Context, @RawRes rawRes: Int): String {
        val inputStream = context.resources.openRawResource(rawRes)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val result = StringBuilder()
        try {
            var line: String? = null
            while ({ line = bufferedReader.readLine(); line }() != null) {
                result.append(line)
            }
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return result.toString()
    }

    fun parseRawJson(context: Context, @RawRes rawJsonRes: Int, type: Type, typeAdapter: T?): T? {
        val json = readRawFile(context, rawJsonRes)
        val gsonBuilder = GsonBuilder()
        if (typeAdapter != null) {
            gsonBuilder.registerTypeAdapter(type, typeAdapter)
        }
        gsonBuilder.setDateFormat(Constants.DATE_FORMATTER_VALUE.toPattern())
        val gson = gsonBuilder.create()
        return gson.fromJson(json, type)
    }
}