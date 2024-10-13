package tm.read.text.test.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

@SuppressLint("StaticFieldLeak")
object SharedPreference{
    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        this.context = context
        sharedPreferences = context.getSharedPreferences("INDEX", Context.MODE_PRIVATE)
    }


    internal fun getLineIndex(): Int {
        return sharedPreferences.getInt("index", 0)
    }

    fun setLineIndex(index: Int) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putInt("index", index)
        editor.apply()
    }
}
