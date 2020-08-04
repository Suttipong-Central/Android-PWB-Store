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
){
    companion object{
        // field
        const val FIELD_STATUS = "status"
        const val FIELD_VISIBILITY = "visibility"
        const val FIELD_PRICE = "price"
        const val FIELD_MARKETPLACE_SELLER = "marketplace_seller"

        // condition type
        const val CONDITION_EQUAL = "eq"
        const val CONDITION_GET_THAN = "gt"
        const val CONDITION_NULL = "null"

        fun createFilterGroups(field: String, value: String, conditionType: String): FilterGroups{
            val filter = Filter.createFilter(field = field,value =  value,conditionType =  conditionType)
            val filterList = arrayListOf<Filter>()
            filterList.add(filter)
            return FilterGroups(filterList)
        }

        fun createFilterGroups(field: String, conditionType: String): FilterGroups{
            val filter = Filter.createFilter(field = field,conditionType =  conditionType)
            val filterList = arrayListOf<Filter>()
            filterList.add(filter)
            return FilterGroups(filterList)
        }

        fun getDefaultFilterGroup(): ArrayList<FilterGroups>{
            val filterGroupsList: ArrayList<FilterGroups> = arrayListOf()
            filterGroupsList.add(createFilterGroups(FIELD_STATUS, "1", CONDITION_EQUAL))
            filterGroupsList.add(createFilterGroups(FIELD_VISIBILITY, "4", CONDITION_EQUAL))
            filterGroupsList.add(createFilterGroups(FIELD_PRICE, "0", CONDITION_GET_THAN))

            // TODO We have to do not display market place product
            filterGroupsList.add(createFilterGroups(FIELD_MARKETPLACE_SELLER, CONDITION_NULL))
            return  filterGroupsList
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