package dev.berlinbruno.pecki.ui.transactions.models

import dev.berlinbruno.pecki.domain.transactions.models.TransactionType

data class TransactionFilters(
    val title: String = "",
    val type: TransactionType? = null,
    val categoryId: String? = null,
    val dateRange: DateRange = DateRange.ALL,
    val fromDate: Long? = null,
    val toDate: Long? = null,
    val sortBy: SortBy = SortBy.DATE_DESC
)

enum class DateRange {
    ALL,
    TODAY,
    YESTERDAY,
    THIS_WEEK,
    THIS_MONTH,
    CUSTOM
}

enum class SortBy {
    DATE_DESC,
    DATE_ASC,
    AMOUNT_DESC,
    AMOUNT_ASC
}
