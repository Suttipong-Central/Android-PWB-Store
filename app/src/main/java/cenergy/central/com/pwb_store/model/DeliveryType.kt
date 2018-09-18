package cenergy.central.com.pwb_store.model

/**
 * Created by Anuphap Suwannamas on 17/9/2018 AD.
 * Email: Anupharpae@gmail.com
 */

enum class DeliveryType(val key: String) {
    EXPRESS("express") {
        override fun toString(): String = "ส่งด่วน"
    },
    STANDARD("standard") {
        override fun toString(): String = "จัดส่งแบบปกติ"
    },
    STORE_PICK_UP("storepickup") {
        override fun toString(): String = "รับที่สาขา"
    },
    HOME("homedelivery") {
        override fun toString(): String = "กำหนดวันจัดส่ง"
    };
    companion object {
        private val map = DeliveryType.values().associateBy(DeliveryType::key)
        fun fromString(value: String) = map[value]
    }
}
