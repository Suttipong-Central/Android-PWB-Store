package cenergy.central.com.pwb_store.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R

/**
 * Created by Anuphap Suwannamas on 24/9/2018 AD.
 * Email: Anupharpae@gmail.com
 */

class ProductFreeItemFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_product_free_item, container, false)
    }
}