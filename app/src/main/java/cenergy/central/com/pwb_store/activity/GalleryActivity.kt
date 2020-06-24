package cenergy.central.com.pwb_store.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.viewpager2.widget.ViewPager2
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.ProductDetailActivity.Companion.ARG_UPDATE_IMAGE_SELECTED
import cenergy.central.com.pwb_store.activity.ProductDetailActivity.Companion.REQUEST_UPDATE_IMAGE_SELECTED
import cenergy.central.com.pwb_store.adapter.GalleryImageAdapter
import cenergy.central.com.pwb_store.adapter.GalleryIndicatorAdapter
import cenergy.central.com.pwb_store.adapter.interfaces.SelectedImageListener
import cenergy.central.com.pwb_store.model.ProductDetailImageItem
import kotlinx.android.synthetic.main.activity_gallery.*
import java.util.*

class GalleryActivity : AppCompatActivity(), SelectedImageListener {

    private var imageSelectedIndex: Int = 0
    private var productImageList: ArrayList<ProductDetailImageItem> = arrayListOf()
    private val galleryImageAdapter = GalleryImageAdapter()
    private val galleryIndicatorAdapter = GalleryIndicatorAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        imageSelectedIndex = intent?.extras?.getInt(ARG_IMAGE_SELECTED) ?: 0
        productImageList = intent?.extras?.getParcelableArrayList(ARG_PRODUCT_IMAGE_LIST) ?: arrayListOf()
        galleryImageAdapter.productImageList = productImageList
        galleryIndicatorAdapter.productImageList = productImageList
        galleryIndicatorAdapter.setSelectedImageListener(this)
        galleryIndicatorRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        galleryIndicatorRecycler.adapter = galleryIndicatorAdapter
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(galleryIndicatorRecycler)
        galleryImageViewPager.adapter = galleryImageAdapter
        galleryImageViewPager.currentItem = imageSelectedIndex
        galleryImageViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                this@GalleryActivity.imageSelectedIndex = position
            }
        })
        finishedActivityBt.setOnClickListener { finishActivity() }
    }

    override fun onClickImageListener(index: Int) {
        this.imageSelectedIndex = index
        galleryImageViewPager.currentItem = this.imageSelectedIndex
    }

    override fun onBackPressed() {
        finishActivity()
    }

    private fun finishActivity() {
        val intent = Intent()
        intent.putExtra(ARG_UPDATE_IMAGE_SELECTED, this.imageSelectedIndex)
        setResult(RESULT_IMAGE_SELECTED, intent)
        this.finish()
    }

    companion object {
        private const val ARG_IMAGE_SELECTED = "ARG_IMAGE_SELECTED"
        private const val ARG_PRODUCT_IMAGE_LIST = "ARG_PRODUCT_IMAGE_LIST"
        const val RESULT_IMAGE_SELECTED = 4021

        fun startActivity(context: Context, index: Int, productImageList: ArrayList<ProductDetailImageItem>) {
            val intent = Intent(context, GalleryActivity::class.java)
            intent.putExtra(ARG_IMAGE_SELECTED, index)
            intent.putParcelableArrayListExtra(ARG_PRODUCT_IMAGE_LIST, productImageList)
            (context as Activity).startActivityForResult(intent, REQUEST_UPDATE_IMAGE_SELECTED)
        }
    }
}