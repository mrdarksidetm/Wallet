package com.darkside.wallet.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector

object AppIcons {
    fun getIcon(name: String?): ImageVector {
        val cleanName = name?.lowercase()?.replace("mdi:", "")?.replace("fa:", "")?.replace("mi:", "") ?: ""
        
        return when (cleanName) {
            "home" -> Icons.Default.Home
            "accounts", "account_balance_wallet", "wallet" -> Icons.Default.AccountBalanceWallet
            "reports", "pie_chart", "chart-pie" -> Icons.Default.PieChart
            "search" -> Icons.Default.Search
            "settings", "settings_rounded", "gear" -> Icons.Default.Settings
            "add" -> Icons.Default.Add
            "add_card" -> Icons.Default.AddCard
            "filter_alt" -> Icons.Default.FilterAlt
            "category" -> Icons.Default.Category
            "person_add", "user" -> Icons.Default.PersonAdd
            "flag" -> Icons.Default.Flag
            "event_repeat" -> Icons.Default.EventRepeat
            "trending_up" -> Icons.AutoMirrored.Filled.TrendingUp
            "trending_down" -> Icons.AutoMirrored.Filled.TrendingDown
            "calendar_today", "event_note" -> Icons.Default.CalendarToday
            "chevron_right" -> Icons.Default.ChevronRight
            "shopping_bag", "shopping-bag" -> Icons.Default.ShoppingBag
            "credit_card", "credit-card" -> Icons.Default.CreditCard
            "account_balance", "bank", "landmark" -> Icons.Default.AccountBalance
            "share" -> Icons.Default.Share
            "currency_exchange", "money-bill", "money-check-dollar" -> Icons.Default.CurrencyExchange
            "star" -> Icons.Default.Star
            "refresh" -> Icons.Default.Refresh
            "group" -> Icons.Default.Group
            "visibility" -> Icons.Default.Visibility
            "visibility_off" -> Icons.Default.VisibilityOff
            "info" -> Icons.Default.Info
            "edit" -> Icons.Default.Edit
            "archive" -> Icons.Default.Archive
            "unarchive" -> Icons.Default.Unarchive
            "delete" -> Icons.Default.Delete
            "car" -> Icons.Default.DirectionsCar
            "house" -> Icons.Default.Home
            "burger", "utensils", "pizza-slice" -> Icons.Default.Restaurant
            "gift" -> Icons.Default.CardGiftcard
            "heart" -> Icons.Default.Favorite
            "bell" -> Icons.Default.Notifications
            "camera" -> Icons.Default.PhotoCamera
            "envelope" -> Icons.Default.Email
            "phone" -> Icons.Default.Phone
            "location-dot" -> Icons.Default.LocationOn
            "bicycle" -> Icons.Default.DirectionsBike
            "bus" -> Icons.Default.DirectionsBus
            "plane" -> Icons.Default.Flight
            "gas-pump" -> Icons.Default.LocalGasStation
            "laptop" -> Icons.Default.Laptop
            "mobile" -> Icons.Default.Smartphone
            "tv" -> Icons.Default.Tv
            "gamepad" -> Icons.Default.Gamepad
            "music" -> Icons.Default.MusicNote
            "film" -> Icons.Default.Movie
            "ticket" -> Icons.Default.LocalActivity
            "shopping-cart" -> Icons.Default.ShoppingCart
            "tag" -> Icons.Default.Tag
            "coffee", "mug-hot" -> Icons.Default.Coffee
            "wine-glass" -> Icons.Default.WineBar
            "ice-cream" -> Icons.Default.Icecream
            "shirt" -> Icons.Default.Checkroom
            "graduation-cap" -> Icons.Default.School
            "briefcase" -> Icons.Default.Work
            "tools", "wrench", "hammer" -> Icons.Default.Build
            "medkit", "stethoscope", "pills" -> Icons.Default.MedicalServices
            "dumbbell" -> Icons.Default.FitnessCenter
            "soccer-ball", "basketball" -> Icons.Default.SportsBasketball
            "trophy" -> Icons.Default.EmojiEvents
            "coins", "dollar-sign", "euro-sign", "bitcoin" -> Icons.Default.MonetizationOn
            "receipt" -> Icons.Default.Receipt
            "piggy-bank" -> Icons.Default.Savings
            else -> Icons.Default.Category
        }
    }
}
