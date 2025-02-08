package myung.jin.bikerepairdoc.ui.room


import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.text.insert

class OfflineBikeMemoRepository(private val bikeMemoDao: BikeMemoDao) : BikeMemoRepository {

    override fun getAllBikeMemoStream(): Flow<List<BikeMemo>> = bikeMemoDao.getAll()

    override fun getBikeMemoStream(id: Int): Flow<BikeMemo?> = bikeMemoDao.getBikeMemoById(id)

    override fun getLastBikeMemoStream(): Flow<BikeMemo?> = bikeMemoDao.getLastBikeMemo()

    //getTotalAmountByDate 함수에서 flow 빌더를 사용하여 Flow<Int>를 생성하고, bikeMemoDao.getTotalAmountByDate(date)를 호출하여 결과를 emit합니다. 추가 설명:
    //flow 빌더는 코루틴을 사용하여 비동기적으로 데이터를 생성하는 Flow를 생성합니다.
    //emit 함수는 Flow에 데이터를 방출합니다.
    //bikeMemoDao.getTotalAmountByDate(date)는 지정된 날짜에 해당하는 금액의 합계를 반환하는 함수입니다.
    override fun getTotalAmountByDate(date: String): Flow<Int> = flow {
        emit(bikeMemoDao.getTotalAmountByDate(date))
    }

    override fun getDateBikeMemoStream(date: String): Flow<List<BikeMemo>> =
        bikeMemoDao.getDate(date)


    override suspend fun insertBikeMemo(bikeMemo: BikeMemo) = bikeMemoDao.insert(bikeMemo)

    override suspend fun deleteBikeMemo(bikeMemo: BikeMemo) = bikeMemoDao.delete(bikeMemo)

    override suspend fun updateBikeMemo(bikeMemo: BikeMemo) = bikeMemoDao.update(bikeMemo)

    override suspend fun deleteBikeMemoId(bikeMemoId: Int) =
        bikeMemoDao.deleteBikeMemoById(bikeMemoId)

}