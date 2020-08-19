package cenergy.central.com.pwb_store.model

sealed class InstallmentPlanView {

    data class Installment(val bankId: Int, val installments: List<InstallmentPlan> = listOf()) : InstallmentPlanView()
}