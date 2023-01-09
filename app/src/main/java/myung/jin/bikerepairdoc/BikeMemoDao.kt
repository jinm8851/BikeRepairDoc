package myung.jin.bikerepairdoc

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
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
}