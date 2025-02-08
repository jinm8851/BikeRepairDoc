package myung.jin.bikerepairdoc.ui.room

import kotlinx.coroutines.flow.Flow

/**
 * 주어진 데이터 소스에서 [바이크메모]의 삽입, 업데이트, 삭제 및 검색을 제공하는 저장소입니다.
 */
interface BikeMemoRepository {

    // 지정된 데이터 소스에서 모든 항목을 검색합니다.
    fun getAllBikeMemoStream(): Flow<List<BikeMemo>>
    // 주어진 데이터 소스에서 [id]와 일치하는 항목을 검색합니다.
    fun getBikeMemoStream(id: Int): Flow<BikeMemo?>

    // 가장 마지막 날 저장한 데이터 1개를 가지고 옴
    fun getLastBikeMemoStream(): Flow<BikeMemo?>
    // 지정된 날짜에 해당하는 모든 바이크메모를 검색합니다.
    fun getTotalAmountByDate(date: String): Flow<Int>
    // 지정된 날짜에 해당하는 모든 바이크메모를 검색합니다.
    fun getDateBikeMemoStream(date: String): Flow<List<BikeMemo>>

    suspend fun insertBikeMemo(bikeMemo: BikeMemo)

    suspend fun deleteBikeMemo(bikeMemo: BikeMemo)

    suspend fun updateBikeMemo(bikeMemo: BikeMemo)

    suspend fun deleteBikeMemoId(bikeMemoId: Int)
}