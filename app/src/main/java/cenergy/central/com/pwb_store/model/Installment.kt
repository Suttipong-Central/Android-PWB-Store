package cenergy.central.com.pwb_store.model

class Installment(
        val bankId: Int,
        val installments: List<InstallmentPlan> = listOf()
)