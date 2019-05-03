package cenergy.central.com.pwb_store.manager

object PowerBuy {
    object MEMBER {
        // getPWBCustomer()
        // rest/V1/headless/customers/:telephone

        // paths
        const val PATH_REST = "rest"
        const val PATH_V1 = "V1"
//        const val PATH_HEADLESS = "headless"
        const val PATH_CUSTOMERS = "customers"
        const val PATH_ADDRESSES = "addresses"
        const val PATH_SEARCH = "search"
        const val FULL_PATH = "$PATH_REST/$PATH_V1/$PATH_CUSTOMERS/$PATH_ADDRESSES/$PATH_SEARCH"

        // fields
        const val ID = "id"
        const val FIRST_NAME = "firstname"
        const val LAST_NAME = "lastname"
        const val EMAIL = "email"
        const val THE_1_CARD_NUMNER = "the_one_card_no"
        const val ADDRESSES = "addresses"
    }
}