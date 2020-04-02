package cenergy.central.com.pwb_store.model.body

class ProductListBody(
        var size: Int = 0,
        var currentPage: Int = 0,
        var filterGroups: ArrayList<FilterGroups> = arrayListOf(),
        var sortOrders: ArrayList<SortOrder> = arrayListOf()) {
    companion object {
        fun createBody(size: Int, currentPage: Int, filterGroups: ArrayList<FilterGroups>,
                       sortOrders: ArrayList<SortOrder>): ProductListBody {
            return ProductListBody(size = size, currentPage = currentPage,
                    filterGroups = filterGroups, sortOrders = sortOrders)
        }

        fun createBody(filterGroups: ArrayList<FilterGroups>): ProductListBody {
            return ProductListBody(filterGroups = filterGroups)
        }
    }
}

class FilterGroups(
        var filters: ArrayList<Filter> = arrayListOf()
){
    companion object {
        fun createFilterGroups(field: String, value: String, conditionType: String): FilterGroups {
            val filter = Filter.createFilter(field = field, value = value, conditionType = conditionType)
            val filterList = arrayListOf<Filter>()
            filterList.add(filter)
            return FilterGroups(filterList)
        }

        fun createFilterGroups(field: String, conditionType: String): FilterGroups {
            val filter = Filter.createFilter(field = field, conditionType = conditionType)
            val filterList = arrayListOf<Filter>()
            filterList.add(filter)
            return FilterGroups(filterList)
        }
    }
}

class Filter(
        var field: String = "",
        var value: String? = "",
        var conditionType: String? = null
) {
    companion object {
        fun createFilter(field: String, value: String, conditionType: String? = null): Filter {
            return Filter(field = field, value = value, conditionType = conditionType)
        }

        fun createFilter(field: String, conditionType: String? = null): Filter {
            return Filter(field = field, conditionType = conditionType)
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