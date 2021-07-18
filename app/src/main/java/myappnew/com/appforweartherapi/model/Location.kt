package myappnew.com.appforweartherapi.model


import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "location")
data class Location(
    val lat: Double,
    val lon: Double,
    val name: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
): Parcelable {
    override fun describeContents() : Int =0


    override fun writeToParcel(p0 : Parcel? , p1 : Int) {
        TODO("Not yet implemented")
    }
}
