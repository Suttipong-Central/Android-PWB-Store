package cenergy.central.com.pwb_store.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.view.PowerBuyEditTextBorder

class PaymentDescriptionFragment : Fragment() {

    private lateinit var recycler: RecyclerView
    private lateinit var firstNameEdt: PowerBuyEditTextBorder
    private lateinit var lastNameEdt: PowerBuyEditTextBorder
    private lateinit var contactNumberEdt: PowerBuyEditTextBorder
    private lateinit var emailEdt: PowerBuyEditTextBorder
    private lateinit var houseNoEdt: PowerBuyEditTextBorder
    private lateinit var placeOrBuildingEdit: PowerBuyEditTextBorder
    private lateinit var soiEdt: PowerBuyEditTextBorder
    private lateinit var streetEdt: PowerBuyEditTextBorder
    private lateinit var provinceEdt: PowerBuyEditTextBorder
    private lateinit var districtEdt: PowerBuyEditTextBorder
    private lateinit var subDistrictEdt: PowerBuyEditTextBorder
    private lateinit var postCodeEdt: PowerBuyEditTextBorder
    private lateinit var tellEdt: PowerBuyEditTextBorder
    private lateinit var paymentBtn: CardView

    companion object {

        private const val contactNumber = "CONTACT_NUMBER"

        private var contactNo: String? = null

        fun newInstance(contactNo: String): ProductDetailFragment {
            val fragment = ProductDetailFragment()
            val args = Bundle()
            args.putString(contactNumber, contactNo)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactNo = arguments?.getString(contactNumber)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_payment_description, container, false)
        setupView(rootView)
        return rootView
    }

    private fun setupView(rootView: View) {
        recycler = rootView.findViewById(R.id.recycler_product_list_payment)
        firstNameEdt = rootView.findViewById(R.id.first_name_payment)
        lastNameEdt = rootView.findViewById(R.id.last_name_payment)
        contactNumberEdt = rootView.findViewById(R.id.contact_number_payment)
        emailEdt = rootView.findViewById(R.id.email_payment)
        houseNoEdt = rootView.findViewById(R.id.house_no_payment)
        placeOrBuildingEdit = rootView.findViewById(R.id.place_or_building_payment)
        soiEdt = rootView.findViewById(R.id.soi_payment)
        streetEdt = rootView.findViewById(R.id.street_payment)
        provinceEdt = rootView.findViewById(R.id.province_payment)
        districtEdt = rootView.findViewById(R.id.district_payment)
        subDistrictEdt = rootView.findViewById(R.id.sub_district_payment)
        postCodeEdt = rootView.findViewById(R.id.post_code_payment)
        tellEdt = rootView.findViewById(R.id.tell_payment)
        paymentBtn = rootView.findViewById(R.id.payment_button_payment)

        contactNo?.let { contactNumberEdt.setText(it) }
    }
}