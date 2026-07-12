package dev.berlinbruno.pecki.domain.transactions.models

enum class TransactionType {
    DEBIT,
    CREDIT
}

enum class TransactionStatus {
    DRAFT,
    PENDING_APPROVAL,
    APPROVED
}

enum class TransactionSource {
    MANUAL,
    SMS,
    IMPORT
}
