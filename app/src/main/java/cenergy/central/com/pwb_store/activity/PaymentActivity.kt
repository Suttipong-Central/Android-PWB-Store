package cenergy.central.com.pwb_store.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.fragment.PaymentCheckOutFragment
import cenergy.central.com.pwb_store.fragment.PaymentDescriptionFragment
import cenergy.central.com.pwb_store.fragment.PaymentSuccessFragment
import cenergy.central.com.pwb_store.manager.listeners.CheckOutClickListener
import cenergy.central.com.pwb_store.manager.listeners.PaymentClickLintener

class PaymentActivity : AppCompatActivity(), CheckOutClickListener , PaymentClickLintener{

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, PaymentActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
                .replace(R.id.container, PaymentCheckOutFragment.newInstance())
                .commit()
    }

    override fun onCheckOutListener(contactNo: String) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
                .replace(R.id.container, PaymentDescriptionFragment.newInstance(contactNo))
                .commit()
    }

    override fun onPaymentClickListener() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
                .replace(R.id.container, PaymentSuccessFragment.newInstance())
                .commit()
    }
}