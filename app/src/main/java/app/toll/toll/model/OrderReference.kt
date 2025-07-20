package app.toll.toll.model
import android.os.Parcelable
import kotlinx.parcelize.Parcelize



@Parcelize
data class OrderReference(val active: String? = null, val pending: String? = null, val recents: String? = null): Parcelable