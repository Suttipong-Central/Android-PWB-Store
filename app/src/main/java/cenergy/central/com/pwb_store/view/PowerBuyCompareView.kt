package cenergy.central.com.pwb_store.view

import android.animation.Animator
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.annotation.AttrRes
import androidx.annotation.RequiresApi
import androidx.annotation.StyleRes
import cenergy.central.com.pwb_store.R
import com.robinhood.ticker.TickerUtils
import com.robinhood.ticker.TickerView

/**
 * Created by napabhat on 7/19/2017 AD.
 */

class PowerBuyCompareView : FrameLayout {
    //View Members
    private lateinit var mLayoutCart: RelativeLayout
    lateinit var mLayoutBadge: ViewGroup
    lateinit var mBadgeCart: TickerView

    //Data Members
    private var mCompareItemCount: Int = 0
    private var mListener: OnClickListener? = null

    constructor(context: Context) : super(context) {
        initInflate()
        initInstance()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initInflate()
        initInstance()
    }

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initInflate()
        initInstance()
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int, @StyleRes defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initInflate()
        initInstance()
    }

    private fun initInflate() {
        //Inflate Layout
        val view = View.inflate(context, R.layout.view_button_compare, this)

        // bind view
        mLayoutBadge = view.findViewById(R.id.layout_badge)
        mLayoutCart = view.findViewById(R.id.layout_cart)
        mBadgeCart = view.findViewById(R.id.txt_view_badge_compare)
        view.findViewById<View>(R.id.image_view_compare).setOnClickListener { v ->
            if (mListener != null) {
                mListener!!.onCompareClickListener(v)
            }
        }
    }

    private fun initInstance() {
        mBadgeCart.setCharacterList(TickerUtils.getDefaultNumberList())
    }

    private fun setBadgeCart(count: Int) {
        this.mCompareItemCount = count
        setInfo()
    }

    private fun setInfo() {
        mBadgeCart.setText(mCompareItemCount.toString())
        if (mCompareItemCount == 0) {
            mLayoutBadge.animate()
                    .alpha(0.0f)
                    .setDuration(300)
                    .setListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animator: Animator) {

                        }

                        override fun onAnimationEnd(animator: Animator) {
                            mBadgeCart.visibility = View.INVISIBLE
                            mLayoutBadge.visibility = View.INVISIBLE
                        }

                        override fun onAnimationCancel(animator: Animator) {

                        }

                        override fun onAnimationRepeat(animator: Animator) {

                        }
                    })
            mLayoutBadge.visibility = View.INVISIBLE
            mBadgeCart.visibility = View.INVISIBLE
        } else {
            mLayoutBadge.animate().alpha(1.0f)
                    .setDuration(300)
                    .setListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animator: Animator) {
                            mBadgeCart.visibility = View.VISIBLE
                            mLayoutBadge.visibility = View.VISIBLE
                        }

                        override fun onAnimationEnd(animator: Animator) {
                            mLayoutBadge.clearAnimation()
                        }

                        override fun onAnimationCancel(animator: Animator) {

                        }

                        override fun onAnimationRepeat(animator: Animator) {

                        }
                    })
            mLayoutBadge.visibility = View.VISIBLE
            mBadgeCart.visibility = View.VISIBLE
        }
    }

    fun updateCartCount(count: Int) {
        setBadgeCart(count)
    }

    fun setListener(listener: OnClickListener) {
        this.mListener = listener
    }

    interface OnClickListener {
        fun onCompareClickListener(view: View)
    }
}
