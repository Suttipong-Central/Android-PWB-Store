package cenergy.central.com.pwb_store.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.NewCategoryAdapter

class SubHeaderProductActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_header_product)
        val subHeaderRecycler = findViewById<RecyclerView>(R.id.sub_header_recycler)
        val adapter = NewCategoryAdapter()
        val gridLayoutManager = GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false)
        subHeaderRecycler.layoutManager = gridLayoutManager
        subHeaderRecycler.adapter = adapter
    }
}
