package dev.berlinbruno.pecki.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable object Onboarding : Screen
    @Serializable object Home : Screen
    @Serializable object Transactions : Screen
    @Serializable object Budgets : Screen
    @Serializable object Investments : Screen
    @Serializable object Settings : Screen
    @Serializable object ApproveTransactions : Screen
    @Serializable object AddTransaction : Screen
    @Serializable object TransactionDetail : Screen
    @Serializable object BudgetDetail : Screen
    @Serializable object InvestmentDetail : Screen
    @Serializable object Reports : Screen
    @Serializable object Search : Screen
    @Serializable object Notifications : Screen
    @Serializable object Profile : Screen
}
