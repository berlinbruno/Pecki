package dev.berlinbruno.pecki.data.transactions.defaults

import dev.berlinbruno.pecki.domain.transactions.models.Category
import dev.berlinbruno.pecki.domain.transactions.models.Mode
import dev.berlinbruno.pecki.domain.transactions.models.TransactionType

object DefaultData {
    val modes = listOf(
        "UPI", "NEFT", "IMPS", "CARD", "CASH", "ATM", "NETBANKING", "OTHER"
    ).map { name ->
        Mode(id = name.lowercase(), name = name, icon = null, isSystem = true)
    }

    private fun color(hex: String) = android.graphics.Color.parseColor(hex)

    val categories = listOf(
        // Debit
        Category(
            id = "debit_food",
            name = "FOOD",
            type = TransactionType.DEBIT,
            icon = null,
            color = color("#FF6B6B"),
            keywords = listOf("swiggy", "zomato", "food", "restaurant", "cafe", "bakery", "pizza", "burger", "kfc", "mcdonald", "starbucks", "subway", "domino", "pizzahut", "dine", "kitchen", "eat", "breakfast", "lunch", "dinner", "snack", "tea", "coffee", "bar", "pub"),
            isSystem = true
        ),
        Category(
            id = "debit_grocery",
            name = "GROCERY",
            type = TransactionType.DEBIT,
            icon = null,
            color = color("#FFA94D"),
            keywords = listOf("bigbasket", "blinkit", "zepto", "instamart", "grocery", "supermart", "dmart", "reliance fresh", "more megastore", "spencer", "star bazaar", "local store", "dairy", "milk", "vegetable", "fruit", "meat", "fish", "provision", "mart", "store", "shop", "market", "pantry"),
            isSystem = true
        ),
        Category(
            id = "debit_bills",
            name = "BILLS",
            type = TransactionType.DEBIT,
            icon = null,
            color = color("#FFD43B"),
            keywords = listOf("airtel", "jio", "vi ", "bill", "electricity", "water", "gas", "broadband", "landline", "recharge", "mobile", "postpaid", "prepaid", "dth", "tata play", "dish tv", "bsnl", "vodafone", "idea", "act fibernet", "utility"),
            isSystem = true
        ),
        Category(
            id = "debit_shopping",
            name = "SHOPPING",
            type = TransactionType.DEBIT,
            icon = null,
            color = color("#6BCB77"),
            keywords = listOf("amazon", "flipkart", "shopping", "myntra", "ajio", "nykaa", "zara", "h&m", "meesho", "decathlon", "lifestyle", "max", "pantaloons", "westside", "titan", "lenskart", "clothes", "shoes", "fashion", "electronics", "gadget", "retail", "mall", "order"),
            isSystem = true
        ),
        Category(
            id = "debit_travel",
            name = "TRAVEL",
            type = TransactionType.DEBIT,
            icon = null,
            color = color("#4D96FF"),
            keywords = listOf("uber", "ola", "travel", "irctc", "metro", "makemytrip", "goibibo", "indigo", "rapido", "redbus", "air india", "spicejet", "akasa", "train", "flight", "bus", "cab", "taxi", "auto", "hotel", "stay", "booking", "trip", "journey", "ticket"),
            isSystem = true
        ),
        Category(
            id = "debit_fuel",
            name = "FUEL",
            type = TransactionType.DEBIT,
            icon = null,
            color = color("#845EC2"),
            keywords = listOf("shell", "hpcl", "bpcl", "iocl", "fuel", "petrol", "diesel", "cng", "gas station", "filling station", "petroleum", "oil", "lubricants", "speed", "xppremium", "power", "auto gas"),
            isSystem = true
        ),
        Category(
            id = "debit_rent",
            name = "RENT",
            type = TransactionType.DEBIT,
            icon = null,
            color = color("#FF9671"),
            keywords = listOf("rent", "house rent", "flat rent", "pg rent", "lease", "maintenance", "deposit", "society", "accommodation"),
            isSystem = true
        ),
        Category(
            id = "debit_healthcare",
            name = "HEALTHCARE",
            type = TransactionType.DEBIT,
            icon = null,
            color = color("#FF6B9D"),
            keywords = listOf("pharmacy", "hospital", "doctor", "clinic", "medical", "lab ", "pathology", "apollo", "pharmeasy", "medicine", "health", "dentist", "surgeon", "therapy", "wellness", "yoga", "gym", "1mg", "netmeds", "tata 1mg"),
            isSystem = true
        ),
        Category(
            id = "debit_education",
            name = "EDUCATION",
            type = TransactionType.DEBIT,
            icon = null,
            color = color("#4ECDC4"),
            keywords = listOf("school", "college", "university", "fees", "tuition", "academy", "learning", "udemy", "coursera", "edx", "khan academy", "byju", "unacademy", "physics wallah", "books", "stationary", "exam", "admission", "coaching", "course", "diploma", "degree"),
            isSystem = true
        ),
        Category(
            id = "debit_entertainment",
            name = "ENTERTAINMENT",
            type = TransactionType.DEBIT,
            icon = null,
            color = color("#A8E6CF"),
            keywords = listOf("netflix", "prime video", "hotstar", "disney", "spotify", "youtube", "theatre", "cinema", "movie", "pvr", "inox", "bookmyshow", "gaming", "playstation", "xbox", "steam", "epic games", "concert", "event", "show", "amusement", "park", "club"),
            isSystem = true
        ),
        Category(
            id = "debit_utilities",
            name = "UTILITIES",
            type = TransactionType.DEBIT,
            icon = null,
            color = color("#20C997"),
            keywords = listOf("electricity", "water", "gas", "sewage", "waste", "broadband", "wifi", "internet", "utility"),
            isSystem = true
        ),
        Category(
            id = "debit_insurance",
            name = "INSURANCE",
            type = TransactionType.DEBIT,
            icon = null,
            color = color("#15AABF"),
            keywords = listOf("lic", "insurance", "premium", "hdfc life", "sbi life", "icici pru", "max life", "policy", "medical insurance", "term insurance", "life insurance"),
            isSystem = true
        ),
        Category(
            id = "debit_fitness",
            name = "FITNESS",
            type = TransactionType.DEBIT,
            icon = null,
            color = color("#F783AC"),
            keywords = listOf("gym", "fitness", "yoga", "workout", "crossfit", "cult fit", "protein", "supplement", "health club"),
            isSystem = true
        ),
        Category(
            id = "debit_beauty",
            name = "BEAUTY",
            type = TransactionType.DEBIT,
            icon = null,
            color = color("#32CD32"),
            keywords = listOf("salon", "spa", "beauty", "parlor", "cosmetics", "haircut", "makeup", "nykaa", "purplle", "skin care", "trim"),
            isSystem = true
        ),
        Category(
            id = "debit_transport",
            name = "TRANSPORT",
            type = TransactionType.DEBIT,
            icon = null,
            color = color("#40E0D0"),
            keywords = listOf("transport", "transit", "logistics", "shipping", "delivery"),
            isSystem = true
        ),
        Category(
            id = "debit_subscription",
            name = "SUBSCRIPTION",
            type = TransactionType.DEBIT,
            icon = null,
            color = color("#DA70D6"),
            keywords = listOf("netflix", "spotify", "youtube premium", "amazon prime", "hotstar", "icloud", "google one", "microsoft 365", "adobe", "subscription"),
            isSystem = true
        ),
        Category(
            id = "debit_donation",
            name = "DONATION",
            type = TransactionType.DEBIT,
            icon = null,
            color = color("#FFA500"),
            keywords = listOf("donation", "charity", "ngo", "pm cares", "relief fund", "temple", "church", "mosque", "give", "help"),
            isSystem = true
        ),
        Category(
            id = "debit_maintenance",
            name = "MAINTENANCE",
            type = TransactionType.DEBIT,
            icon = null,
            color = color("#9370DB"),
            keywords = listOf("society", "repair", "plumber", "electrician", "mechanic", "car service", "bike service", "maintenance"),
            isSystem = true
        ),
        Category(
            id = "debit_other",
            name = "OTHER",
            type = TransactionType.DEBIT,
            icon = null,
            color = color("#868E96"),
            keywords = emptyList(),
            isSystem = true
        ),

        // Credit
        Category(
            id = "credit_salary",
            name = "SALARY",
            type = TransactionType.CREDIT,
            icon = null,
            color = color("#00E676"),
            keywords = listOf("salary", "stipend", "payroll", "credited by", "employer", "hike"),
            isSystem = true
        ),
        Category(
            id = "credit_investments",
            name = "INVESTMENTS",
            type = TransactionType.CREDIT,
            icon = null,
            color = color("#45B7D1"),
            keywords = listOf("zerodha", "groww", "upstox", "angel one", "5paisa", "icici direct", "mutual fund", "sip ", "stock", "share", "demat", "nifty", "sensex", "etf", "gold", "silver", "property", "real estate", "coin dcx", "wazirx", "crypto", "bitcoin", "fd", "deposit", "insurance"),
            isSystem = true
        ),
        Category(
            id = "credit_refund",
            name = "REFUND",
            type = TransactionType.CREDIT,
            icon = null,
            color = color("#00B0FF"),
            keywords = listOf("refund", "reversed", "returned", "money back"),
            isSystem = true
        ),
        Category(
            id = "credit_bonus",
            name = "BONUS",
            type = TransactionType.CREDIT,
            icon = null,
            color = color("#69F0AE"),
            keywords = listOf("bonus", "incentive", "reward"),
            isSystem = true
        ),
        Category(
            id = "credit_freelance",
            name = "FREELANCE",
            type = TransactionType.CREDIT,
            icon = null,
            color = color("#96CEB4"),
            keywords = listOf("upwork", "fiverr", "freelance", "gig", "consulting"),
            isSystem = true
        ),
        Category(
            id = "credit_gift",
            name = "GIFT",
            type = TransactionType.CREDIT,
            icon = null,
            color = color("#FFEEAD"),
            keywords = listOf("gift", "present", "shagun"),
            isSystem = true
        ),
        Category(
            id = "credit_cashback",
            name = "CASHBACK",
            type = TransactionType.CREDIT,
            icon = null,
            color = color("#D4A5A5"),
            keywords = listOf("cashback", "reward", "scratch card", "points", "cash back"),
            isSystem = true
        ),
        Category(
            id = "credit_dividend",
            name = "DIVIDEND",
            type = TransactionType.CREDIT,
            icon = null,
            color = color("#9B59B6"),
            keywords = listOf("dividend", "div ", "payout"),
            isSystem = true
        ),
        Category(
            id = "credit_interest",
            name = "INTEREST",
            type = TransactionType.CREDIT,
            icon = null,
            color = color("#34495E"),
            keywords = listOf("interest", "int.paid", "savings interest", "fixed deposit interest"),
            isSystem = true
        ),
        Category(
            id = "credit_commission",
            name = "COMMISSION",
            type = TransactionType.CREDIT,
            icon = null,
            color = color("#16A085"),
            keywords = listOf("commission", "brokerage", "referral"),
            isSystem = true
        ),
        Category(
            id = "credit_rental",
            name = "RENTAL",
            type = TransactionType.CREDIT,
            icon = null,
            color = color("#27AE60"),
            keywords = listOf("rent received", "rental income", "tenant"),
            isSystem = true
        ),
        Category(
            id = "credit_other",
            name = "OTHER",
            type = TransactionType.CREDIT,
            icon = null,
            color = color("#868E96"),
            keywords = emptyList(),
            isSystem = true
        )
    )
}
