package cenergy.central.com.pwb_store.model

/**
 * Created by Anuphap Suwannamas on 17/9/2018 AD.
 * Email: Anupharpae@gmail.com
 */

enum class DeliveryType(val key: String) {
    EXPRESS("express") {
        override fun toString(): String = "express"
    },
    STANDARD("standard") {
        override fun toString(): String = "standard"
    },
    STORE_PICK_UP("storepickup") {
        override fun toString(): String = "storepickup"
    },
    HOME("homedelivery") {
        override fun toString(): String = "homedelivery"
    }
}
