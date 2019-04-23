package cenergy.central.com.pwb_store.model.body

class ProductListBody(
        var size: Int = 0,
        var currentPage: Int = 0,
        var filterGroups: ArrayList<FilterGroups> = arrayListOf(),
        var sortOrders: ArrayList<SortOrder> = arrayListOf()
) {
    companion object {
        fun createBody(size: Int, currentPage: Int, filterGroups: ArrayList<FilterGroups>,
                       sortOrders: ArrayList<SortOrder>): ProductListBody {
            return ProductListBody(size = size, currentPage = currentPage,
                    filterGroups = filterGroups, sortOrders = sortOrders)
        }
    }
}

class FilterGroups(
        var filters: ArrayList<Filter> = arrayListOf()
)

class Filter(
        var field: String = "",
        var value: String = "",
        var conditionType: String = ""
) {
    companion object {
        fun createFilter(field: String, value: String, conditionType: String): Filter {
            return Filter(field = field, value = value, conditionType = conditionType)
        }
    }
}

class SortOrder(
        var field: String = "",
        var direction: String = ""
) {
    companion object {
        fun createSortOrder(field: String, direction: String): SortOrder {
            return SortOrder(field = field, direction = direction)
        }
    }
}