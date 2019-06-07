package cenergy.central.com.pwb_store.manager.api

class PwbMemberApi {

    companion object {
        // paths
        const val PATH_REST = "rest"
        const val PATH_V1 = "V1"
        const val PATH_CUSTOMERS = "customers"
        const val PATH_ADDRESSES = "addresses"
        const val PATH_SEARCH = "search"

        // fields
        const val ID = "id"
        const val FIRST_NAME = "firstname"
        const val LAST_NAME = "lastname"
        const val EMAIL = "email"
        const val THE_1_CARD_NUMNER = "the_one_card_no"
        const val ADDRESSES = "addresses"
    }

    /**
     * @param lang = {{store}}
     *
     * */
    fun getPath(lang: String): String {
        return "rest/$lang/V1/customers/addresses/search"
    }

}