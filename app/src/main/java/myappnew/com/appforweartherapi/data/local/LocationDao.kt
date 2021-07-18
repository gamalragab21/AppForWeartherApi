package myappnew.com.appforweartherapi.data.local

import androidx.room.*
import io.reactivex.Single
import myappnew.com.appforweartherapi.model.Location

@Dao
interface LocationDao {

    @Query("select * from location order by id desc")
    suspend fun getAllLocations(): List<Location>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(note : Location):Long

    @Delete
    suspend fun deleteLocation(note : Location):Int

    @Query("SELECT * FROM location WHERE name LIKE :keyword OR lat LIKE :keyword OR lon LIKE :keyword")
    suspend fun searchLocations(keyword :String): List<Location>



}