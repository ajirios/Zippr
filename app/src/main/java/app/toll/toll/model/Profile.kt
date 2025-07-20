package app.toll.toll.model
import app.toll.toll.Country
import app.toll.toll.Place



data class Profile(
    val userId: String? = null,
    val firstName: String? = null,
    val middleName: String? = null,
    val lastName: String? = null,
    val aliasName: String? = null,
    val email: String? = null,
    val timeZone: String? = null,
    val country: Country? = null,
    val address: Place? = null,
    val balance: Double? = null,
    val paymentOptions: List<String>? = null
)
