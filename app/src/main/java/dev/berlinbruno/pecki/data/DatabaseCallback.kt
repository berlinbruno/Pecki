package dev.berlinbruno.pecki.data

import android.content.ContentValues
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.berlinbruno.pecki.domain.transactions.models.TransactionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class DatabaseCallback @Inject constructor(
    private val scope: CoroutineScope
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        // Use raw SQL to avoid deadlock with DAO injection during database creation
        scope.launch(Dispatchers.IO) {
            populateModes(db)
            populateCategories(db)
        }
    }

    private fun populateModes(db: SupportSQLiteDatabase) {
        val modes = listOf(
            "UPI", "NEFT", "IMPS", "CARD", "CASH", "ATM", "NETBANKING", "OTHER"
        )
        modes.forEach { name ->
            val values = ContentValues().apply {
                put("id", name.lowercase())
                put("name", name)
                put("isSystem", 1)
            }
            db.insert("modes", 0, values)
        }
    }

    private fun populateCategories(db: SupportSQLiteDatabase) {
        val debitCategories = listOf(
            "Food" to "#FF6B6B",
            "Shopping" to "#6BCB77",
            "Transport" to "#4D96FF",
            "Bills" to "#FFD43B",
            "Health" to "#FF6B9D",
            "Entertainment" to "#A8E6CF",
            "Education" to "#4ECDC4"
        )
        
        debitCategories.forEach { (name, color) ->
            db.insert("categories", 0, ContentValues().apply {
                put("id", "debit_${name.lowercase()}")
                put("name", name)
                put("type", TransactionType.DEBIT.name)
                put("color", android.graphics.Color.parseColor(color))
                put("keywords", "")
                put("isSystem", 1)
            })
        }

        val creditCategories = listOf(
            "Salary" to "#00E676",
            "Gift" to "#FFEEAD",
            "Refund" to "#00B0FF",
            "Investment" to "#69F0AE",
            "Interest" to "#7C4DFF"
        )
        
        creditCategories.forEach { (name, color) ->
            db.insert("categories", 0, ContentValues().apply {
                put("id", "credit_${name.lowercase()}")
                put("name", name)
                put("type", TransactionType.CREDIT.name)
                put("color", android.graphics.Color.parseColor(color))
                put("keywords", "")
                put("isSystem", 1)
            })
        }

        // Add Others
        listOf(TransactionType.DEBIT, TransactionType.CREDIT).forEach { type ->
            db.insert("categories", 0, ContentValues().apply {
                val prefix = type.name.lowercase()
                put("id", "${prefix}_other")
                put("name", "Other")
                put("type", type.name)
                put("color", android.graphics.Color.parseColor("#868E96"))
                put("keywords", "")
                put("isSystem", 1)
            })
        }
    }
}
