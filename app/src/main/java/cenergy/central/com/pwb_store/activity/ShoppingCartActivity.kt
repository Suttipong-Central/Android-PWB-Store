package cenergy.central.com.pwb_store.activity

import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.ShoppingCartAdapter

class ShoppingCartActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var paymentButton: CardView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_cart)

        initView()

        recycler.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false)
        recycler.adapter = ShoppingCartAdapter()

        paymentButton.setOnClickListener {
            val intent = PaymentActivity.intent(this)
            ActivityCompat.startActivity(this, intent, ActivityOptionsCompat
                    .makeScaleUpAnimation(paymentButton, 0, 0,paymentButton.width, paymentButton.height)
                    .toBundle())
        }
    }

    private fun initView() {
        recycler = findViewById(R.id.recycler_view_shopping_cart)
        paymentButton = findViewById(R.id.payment)
    }
}