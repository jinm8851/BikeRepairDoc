package myung.jin.bikerepairdoc.ui.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BikeMemoDao {
    @Query("SELECT * FROM bike_memo ORDER BY date DESC")
    fun getAll(): Flow<List<BikeMemo>>

    //충돌이 발생하면 해당 데이터는 데이터베이스에 삽입되지 않고, 아무런 오류도 발생하지 않습니다.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(bikeMemo: BikeMemo)

    @Delete
    suspend fun delete(bikeMemo: BikeMemo)

    @Query("select * from bike_memo where date = :date")
    fun getDate(date: String): Flow<List<BikeMemo>>

    @Query("SELECT SUM(amount) FROM bike_memo WHERE date = :date")
    suspend fun getTotalAmountByDate(date: String): Int

    @Query("select * from bike_memo where model = :model")
    fun getModel(model: String): Flow<List<BikeMemo>>

    @Query("select * from bike_memo where year = :year")
    fun getYear(year: String): Flow<List<BikeMemo>>
    // 추가
    @Update
    suspend fun update(bikeMemo: BikeMemo)

    @Query("SELECT * FROM bike_memo WHERE `no` = :id")
    fun getBikeMemoById(id: Int): Flow<BikeMemo>

    @Query("SELECT * FROM bike_memo ORDER BY date DESC")
    fun getLastBikeMemo(): Flow<BikeMemo?>

    @Query("DELETE FROM bike_memo WHERE `no` = :bikeMemoId")
    suspend fun deleteBikeMemoById(bikeMemoId: Int)
}