package cenergy.central.com.pwb_store.view

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.ShadeSelectAdapter
import cenergy.central.com.pwb_store.adapter.interfaces.ShadeClickListener
import cenergy.central.com.pwb_store.model.ProductValue

class PowerBuyShadeSelectView: ConstraintLayout {

    private lateinit var header: PowerBuyTextView
    private lateinit var shadeName: PowerBuyTextView
    private lateinit var shadeRecycler: RecyclerView

    var title = ""
    var name = ""
    var listener: ShadeClickListener? = null

    constructor(context: Context) : super(context) {
        prepareView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val typedArray = context.theme.obtainStyledAttributes(
                attrs, R.styleable.PowerBuyShadeSelectView, 0, 0)

        //Get attribute values
        title = typedArray.getString(R.styleable.PowerBuyShadeSelectView_shade_header) ?: ""
        name = typedArray.getString(R.styleable.PowerBuyShadeSelectView_shade_name) ?: ""

        typedArray.recycle()

        prepareView()

        notifyAttributeChanged()
    }

    private fun prepareView() {
        val view = View.inflate(context, R.layout.view_shade_select, this)
        header = view.findViewById(R.id.titleShade)
        shadeName = view.findViewById(R.id.txtTitleShade)
        shadeRecycler = view.findViewById(R.id.shadeRecycler)
    }

    private fun notifyAttributeChanged() {
        header.text = title
        shadeName.text = name
        shadeRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    fun setShadeName(name: String){
        this.name = name
        notifyAttributeChanged()
    }

    fun setAdapter(adapter: ShadeSelectAdapter){
        this.shadeRecycler.adapter = adapter
    }
}