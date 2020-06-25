package cenergy.central.com.pwb_store.fragment

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.ProductDetailListener
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.DeliveryInfo
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.utils.DialogUtils
import kotlinx.android.synthetic.main.fragment_product_shipping_option.*

class ProductShippingOptionFragment : Fragment() {

    private var productDetailListener: ProductDetailListener? = null
    private var product: Product? = null
    private var deliveryList: List<DeliveryInfo> = arrayListOf()
    private var progressDialog: ProgressDialog? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        productDetailListener = context as ProductDetailListener
        product = productDetailListener?.getProduct()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.deliveryList = productDetailListener?.getDeliveryInfoList() ?: arrayListOf()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_product_shipping_option, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isAdded) {
            setupView()
            if (deliveryList.isEmpty()) {
                product?.let { retrieveDeliveryInfo(it) }
            } else {
                updateDeliveryList()
            }
        }
    }

    private fun retrieveDeliveryInfo(product: Product) {
        context ?: return
        showProgressDialog()
        HttpManagerMagento.getInstance(context!!).getDeliveryInformation(product.sku,
                object : ApiResponseCallback<List<DeliveryInfo>> {
                    override fun success(response: List<DeliveryInfo>?) {
                        response?.let {
                            deliveryList = it
                            productDetailListener?.setDeliveryInfoList(it)
                            Toast.makeText(context, "de ${response.size}", Toast.LENGTH_SHORT).show()
                        }
                        dismissProgressDialog()
                    }

                    override fun failure(error: APIError) {
                        dismissProgressDialog()
                    }
                })
    }

    private fun setupView() {
        rvDeliveryOption?.apply {
            layoutManager = LinearLayoutManager(context)

        }
    }

    private fun updateDeliveryList() {

    }

    private fun showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = DialogUtils.createProgressDialog(context)
            progressDialog?.show()
        } else {
            progressDialog?.show()
        }
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }
}