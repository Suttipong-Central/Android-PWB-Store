package cenergy.central.com.pwb_store.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.model.AddressInformation
import cenergy.central.com.pwb_store.model.UserInformation
import cenergy.central.com.pwb_store.realm.RealmController

class DeliveryStorePickUpFragment : Fragment() {

    private val database = RealmController.with(context)
    private var userInformation: UserInformation? = null
    private var stores: ArrayList<AddressInformation> = arrayListOf()

    companion object {
        fun newInstance(): DeliveryStorePickUpFragment {
            val fragment = DeliveryStorePickUpFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userInformation = database.userInformation

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = LayoutInflater.from(context).inflate(R.layout.fragment_delivery_stores, container, false)
        setupView()
        return rootView
    }

    private fun setupView() {
        val fragmentTransaction = childFragmentManager.beginTransaction()
        val storesFragment = StoresFragment.newInstance()
        fragmentTransaction.replace(R.id.stores_fragment, storesFragment)
                .commit()
        val storeDetailFragment = StoresFragment.newInstance()
        fragmentTransaction.replace(R.id.store_detail_fragment, storeDetailFragment)
                .commit()

        userInformation?.let { userInformation ->
            if (userInformation.user != null && userInformation.stores != null && userInformation.stores!!.size > 0) {
                val staff = userInformation.user
                val store = userInformation.stores!![0]
                //Check this if change to use PWB login
                val province = database.getProvinceByNameEn(store?.province ?: "")
                val district = database.getDistrictByNameEn(store?.district ?: "")
                val subDistrict = database.getSubDistrictByNameEn(store?.subDistrict ?: "")
                val storeAddress = AddressInformation.createAddress(
                        firstName = staff?.name ?: "", lastName = staff?.name ?: "", email = staff?.email ?: "",
                        contactNo = store?.number ?: "", homeNo = store?.storeName ?: "", homeBuilding = store?.building ?: "",
                        homeSoi = store?.soi ?: "", homeRoad = store?.road ?: "", homePostalCode = store?.postalCode ?: "",
                        homePhone = store?.number ?: "", provinceId = province?.provinceId?.toString() ?: "",
                        provinceCode = province?.code ?: "", countryId = province?.countryId ?: "",
                        districtId = district?.districtId?.toString() ?: "", subDistrictId = subDistrict?.subDistrictId?.toString() ?: "",
                        postcodeId = store?.postalCode ?: "", homeCity = province?.nameTh ?: "",
                        homeDistrict = district?.nameTh ?: "", homeSubDistrict = subDistrict?.nameTh ?: "")
                stores.add(storeAddress)
            }
        }
    }
}
