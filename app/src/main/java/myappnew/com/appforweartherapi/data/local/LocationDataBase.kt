package myappnew.com.conserve.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import myappnew.com.appforweartherapi.data.local.LocationDao
import myappnew.com.appforweartherapi.model.Location

@Database(entities = [Location::class] , version = 1, exportSchema = false)
public  abstract class LocationDataBase : RoomDatabase() {
    abstract fun noteDao(): LocationDao
    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: LocationDataBase? = null

//        fun getDatabase(context: Context): LocationDataBase {
//            // if the INSTANCE is not null, then return it,
//            // if it is, then create the database
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    LocationDataBase::class.java,
//                    "Location_Database"
//                ).build()
//                INSTANCE = instance
//                // return instance
//                instance
//            }
//        }
    }

}