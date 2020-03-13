package cenergy.central.com.pwb_store.fragment

import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.MainActivity
import cenergy.central.com.pwb_store.view.PowerBuyTextView

class PaymentCreatedOrder : Fragment() {

    private lateinit var textOrderCreated: PowerBuyTextView
    private lateinit var finishButton: Button

    companion object {
        fun newInstance(): PaymentCreatedOrder {
            val fragment = PaymentCreatedOrder()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = LayoutInflater.from(context).inflate(R.layout.fragment_payment_created_order, container, false)
        setupView(rootView)
        return rootView
    }

    private fun setupView(rootView: View) {
        textOrderCreated = rootView.findViewById(R.id.textOrderCreated)
        finishButton = rootView.findViewById(R.id.buttonFinished)
        finishButton.setOnClickListener { finishThisPage() }
    }

    private fun finishThisPage() {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context?.let {
            ActivityCompat.startActivity(it, intent, ActivityOptionsCompat
                    .makeScaleUpAnimation(finishButton, 0, 0, finishButton.width, finishButton.height)
                    .toBundle())
        }
    }

    fun updateView() {
        textOrderCreated.text = getString(R.string.created_order)
        finishButton.text = getString(R.string.finished)
    }
}