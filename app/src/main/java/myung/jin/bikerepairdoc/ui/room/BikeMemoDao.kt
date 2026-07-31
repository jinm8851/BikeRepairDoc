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

    @Query("SELECT IFNULL(SUM(amount), 0) FROM bike_memo WHERE date = :date")
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

    @Query("SELECT * FROM bike_memo ORDER BY date DESC LIMIT 1")
    fun getLastBikeMemo(): Flow<BikeMemo?>

    @Query("DELETE FROM bike_memo WHERE `no` = :bikeMemoId")
    suspend fun deleteBikeMemoById(bikeMemoId: Int)

}


//@Insert(onConflict = OnConflictStrategy.REPLACE)는 데이터베이스에 데이터를 저장할 때 충돌이 발생할 경우
// 어떻게 처리할지를 정의하는 설정입니다.
//주로 신규 저장과 수정(업데이트)을 동시에 처리하고 싶을 때 사용합니다.
/*
충돌 감지: 저장하려는 데이터의 **기본키(Primary Key, 여기서는 id)**가 이미 데이터베이스에 존재하는지 확인합니다.
•교체(REPLACE): 만약 같은 id를 가진 데이터가 이미 있다면, 기존 데이터를 지우고 새로운 데이터로 덮어씁니다.
•신규 삽입: 같은 id가 없다면, 그냥 새로운 데이터를 추가합니다.
1.새로 만들기: id가 0(또는 null)인 데이터를 넣으면 새 항목이 생깁니다.
2.수정하기: 기존 데이터의 id를 그대로 유지한 채 값을 바꿔서 넣으면 데이터가 수정됩니다.
*/
@Dao
interface CashBookDao {
    // CashBook 내용 금액 저장
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCashBook(cashBook: CashBook)

    @Query("SELECT * FROM cash_book ORDER BY date DESC")
    fun getAll(): Flow<List<CashBook>>

    @Query("DELETE FROM cash_book WHERE id = :id")
    suspend fun  deleteById(id: Long)

    // CashBook 드롭다운메뉴 저장
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertContentName(contentName: ContentName)

    @Query("SELECT * FROM content_name ORDER BY name ASC")
    fun getAllContentNames(): Flow<List<ContentName>>

    @Query("DELETE FROM content_name WHERE id = :id")
    suspend fun deleteContentName(id: Long)
}