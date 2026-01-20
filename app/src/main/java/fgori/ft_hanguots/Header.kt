package fgori.ft_hanguots

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.view.View
import androidx.core.graphics.toColorInt
import android.graphics.drawable.GradientDrawable
import androidx.core.view.isVisible
import android.content.SharedPreferences
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import java.util.concurrent.TimeUnit
import androidx.core.graphics.ColorUtils
import java.text.SimpleDateFormat
import java.util.Locale

interface HeaderListener{
    fun onActivityChanged(activity: String)

}
class Header : FrameLayout {

    private var headerColor: String = "@color/light_blue_400"
    private var  listener: HeaderListener? = null
    private var toastTime: Long = 0

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }
    public fun onPause(){
        toastTime = System.currentTimeMillis()
    }

    public fun onResume() {
        if (toastTime > 0) {
            val durationMillis = System.currentTimeMillis() - toastTime
            if (durationMillis >= 1000) {
                val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val formattedTime = format.format(toastTime)
                val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis)

                val text =
                    context.getString(R.string.backText) + " " +formattedTime + " " + context.getString(R.string.backText2) +" "+ totalSeconds +" " + context.getString(
                        R.string.backText3
                    )
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
            }
            toastTime = 0
        }
    }

    public fun setHeaderListener(listener: HeaderListener)
    {
        this.listener = listener
    }
    public fun notifyActivityChanged(activity: String)
    {
        post {
            when (activity) {
                "ChatActivity" -> setChat()
                else -> setOther()
            }
        }
    }

    public fun populateChatHeader(contact: Contact)
    {
        Log.d("HeaderDebug", "Populating header. Name: ${contact.getValue("name")}, Img: ${contact.getValue("img")}")

        val contactImg = findViewById<ImageView>(R.id.contactImg)
        val contactName = findViewById<TextView>(R.id.contactName)
        val contactPhone = findViewById<TextView>(R.id.contactPhone)

        contactName?.text = contact.getValue("name") + " " + contact.getValue("surname")
        contactPhone?.text = contact.getValue("phone")
        val imgUriString = contact.getValue("img")
        if (imgUriString.isNotEmpty()) {
            try {
                contactImg?.setImageURI(android.net.Uri.parse(imgUriString))
            } catch (e: Exception) {
                contactImg?.setImageResource(R.drawable.ic_launcher_foreground) // Default se l'URI fallisce
            }
        } else {
            contactImg?.setImageResource(R.drawable.ic_launcher_foreground)
        }
    }
    private fun setChat() {
        val chatHeader = findViewById<View>(R.id.chatHeader)
        chatHeader?.visibility = View.VISIBLE
    }

    private fun setOther() {
        val chatHeader = findViewById<View>(R.id.chatHeader)
        if (chatHeader?.isVisible == true)
            chatHeader.visibility = View.INVISIBLE
    }



    private fun applyNewColor()
    {
        Log.d("HeaderDebug", "applyNewColor called with color: $headerColor")
        var startColor: Int
        val topBand = findViewById<View>(R.id.top_band)
        try{
            startColor = Color.parseColor(headerColor)
        }catch (e: IllegalArgumentException)
        {
            println("color not found")
            startColor = Color.parseColor("#2196F3")
        }
        topBand?.setBackgroundColor(startColor)
        val endColor: Int = Color.TRANSPARENT
        this.setBackgroundColor(Color.TRANSPARENT)
        val gradient = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(startColor, endColor)
        )

        val chatHeader = findViewById<View>(R.id.chatHeader)
        chatHeader?.background = gradient



    }

    public fun changeColor(newColor: String)
    {
        val sharedPrefs by lazy { context.getSharedPreferences("UserProfile", Context.MODE_PRIVATE) }
        val editor = sharedPrefs.edit()
        editor.putString("USER_COLOR", newColor)
        editor.apply()
        headerColor = newColor
        val headerText = findViewById<TextView>(R.id.header_text)
        if (ColorUtils.calculateLuminance(Color.parseColor(newColor)) < 0.6)
            headerText.setTextColor(Color.WHITE)
        else
            headerText.setTextColor(Color.BLACK)
        applyNewColor()
    }


    private fun init(attrs: AttributeSet?, defStyle: Int) {
        Log.d("HeaderDebug", "init called")

        LayoutInflater.from(context).inflate(R.layout.sample_header_layout, this, true)
        val sharedPrefs by lazy { context.getSharedPreferences("UserProfile", Context.MODE_PRIVATE) }
        val color = sharedPrefs.getString("USER_COLOR", "@color/light_blue_400")
        headerColor = color ?: "@color/light_blue_400"
        applyNewColor()


    }

    public fun getHeaderColor() : String
    {
        return headerColor
    }


}