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

interface HeaderListener{
    fun onActivityChanged(activity: String)

}
class Header : FrameLayout {

    private var headerColor: String = "@color/light_blue_400"
    private var  listener: HeaderListener? = null


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

    public fun setHeaderListener(listener: HeaderListener)
    {
        this.listener = listener
    }
    public fun notifyActivityChanged(activity: String)
    {
        when(activity)
        {
            "ChatActivity" -> setChat()
            else -> setOther()
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
        var startColor: Int
        try{
            startColor = Color.parseColor(headerColor)
            setBackgroundColor(startColor)
        }catch (e: IllegalArgumentException)
        {
            println("color not found")
            setBackgroundColor(Color.parseColor("#2196F3"))
            startColor = Color.parseColor("#2196F3")
        }

        val endColor: Int = Color.TRANSPARENT
        val gradient = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(startColor, endColor)
        )

        val chatHeader = findViewById<View>(R.id.chatHeader)
        chatHeader?.background = gradient



    }

    public fun changeColor(newColor: String)
    {
        headerColor = newColor
        applyNewColor()
    }


    private fun init(attrs: AttributeSet?, defStyle: Int) {
        LayoutInflater.from(context).inflate(R.layout.sample_header_layout, this, true)

        setBackgroundColor(Color.parseColor("#2196F3"))


    }



}