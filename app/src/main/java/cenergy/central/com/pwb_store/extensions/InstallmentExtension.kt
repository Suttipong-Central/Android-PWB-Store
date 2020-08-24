package cenergy.central.com.pwb_store.extensions

import cenergy.central.com.pwb_store.model.Product

fun List<Product>.isAllProductEmptyInstallment(): Boolean {
    return this.firstOrNull { !it.extension?.installmentPlans.isNullOrEmpty() } != null
}

fun List<Product>.isAllProductInstallmentEqual(): Boolean {
    val productsHave = this.filter { !it.extension?.installmentPlans.isNullOrEmpty() }

    return productsHave.size == this.size
}

