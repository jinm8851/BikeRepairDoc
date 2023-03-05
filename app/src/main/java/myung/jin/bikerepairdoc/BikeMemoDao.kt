package myung.jin.bikerepairdoc

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query

@Dao
interface BikeMemoDao {
    @Query("select * from bike_memo")
    fun getAll() : List<BikeMemo>

    @Insert(onConflict = REPLACE)
    fun insert(bikeMemo: BikeMemo)

    @Delete
    fun delete(bikeMemo: BikeMemo)

    @Query("select * from bike_memo WHERE date = :date")
    fun getDate(date : String) : List<BikeMemo>

    @Query("select * from bike_memo WHERE model = :model")
    fun getModel(model : String) : List<BikeMemo>



    @Query("select * from bike_memo WHERE year = :year")
    fun getYear(year : String) : List<BikeMemo>
}