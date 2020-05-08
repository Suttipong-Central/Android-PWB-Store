package cenergy.central.com.pwb_store.fragment

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.CategoryAdapter
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.Category
import cenergy.central.com.pwb_store.utils.Analytics
import cenergy.central.com.pwb_store.utils.Screen
import kotlinx.android.synthetic.main.activity_sub_header_product.view.*

class SubHeaderProductFragment : Fragment() {

    private val analytics by lazy { context?.let { Analytics(it) } }
    private var category: Category? = null
    private lateinit var adapter: CategoryAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        adapter = CategoryAdapter(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = arguments?.getParcelable(ARG_CATEGORY_HEADER)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.activity_sub_header_product, container, false)
        setupView(rootView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        category?.id?.let { loadCategories(it) }
    }

    override fun onResume() {
        super.onResume()
        analytics?.trackScreen(Screen.CATEGORY_LV2)
    }

    private fun setupView(rootView: View) {
        val subHeaderRecycler = rootView.sub_header_recycler
        val gridLayoutManager = GridLayoutManager(rootView.context, 3, LinearLayoutManager.VERTICAL, false)
        gridLayoutManager.spanSizeLookup = adapter.spanSize
        subHeaderRecycler.layoutManager = gridLayoutManager
        subHeaderRecycler.adapter = adapter
        adapter.setCategoryHeader(category?.departmentName, arrayListOf()) // default
    }

    fun foreRefresh(category: Category) {
        this.category = category
        loadCategories(category.id)
    }

    private fun loadCategories(parentId: String) {
        activity?.let {
            HttpManagerMagento.getInstance(it).retrieveCategory(categoryId = parentId,
                    includeInMenu =  true, callback = object : ApiResponseCallback<List<Category>> {
                        override fun success(response: List<Category>?) {
                            it.runOnUiThread {
                                adapter.setCategoryHeader(category?.departmentName, response)
                            }
                        }

                        override fun failure(error: APIError) {
                            it.runOnUiThread {
                                showAlertDialog(error.errorUserMessage?: getString(R.string.some_thing_wrong))
                                Log.e(TAG, "onFailure: " + error.errorUserMessage)
                            }
                        }
                    })
        }
    }

    private fun showAlertDialog(message: String) {
        if (context != null) {
            val builder = AlertDialog.Builder(context!!, R.style.AlertDialogTheme)
                    .setMessage(message)
                    .setPositiveButton(getString(R.string.ok)) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
            builder.show()
        }
    }

    companion object {
        private const val ARG_CATEGORY_HEADER = "category_header"
        private const val TAG = "CategoryLv2Fragment"

        fun newInstance(category: Category): SubHeaderProductFragment {
            val fragment = SubHeaderProductFragment()
            val args = Bundle()
            args.putParcelable(ARG_CATEGORY_HEADER, category)
            fragment.arguments = args
            return fragment
        }
    }
}
