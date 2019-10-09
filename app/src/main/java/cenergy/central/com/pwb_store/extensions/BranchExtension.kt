package cenergy.central.com.pwb_store.extensions

import cenergy.central.com.pwb_store.model.Branch

fun List<List<Branch>>.getDiff(): List<Branch> {
    val sum = arrayListOf<Branch>()
    this.forEach {
        sum += it
    }
    return sum.groupBy { it.storeId }
            .filter { it.value.size == this.size }
            .flatMap { it.value }
}