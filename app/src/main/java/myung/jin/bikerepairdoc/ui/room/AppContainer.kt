package myung.jin.bikerepairdoc.ui.room

import android.content.Context

interface AppContainer {
    val bikeMemoRepository: BikeMemoRepository
    val cashBookRepository: CashBookRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val bikeMemoRepository: BikeMemoRepository by lazy {
        OfflineBikeMemoRepository(InventoryDatabase.getDatabase(context).bikeMemoDao())
    }
    // 2. 가계부 리포지토리 구현 추가
    override val cashBookRepository: CashBookRepository by lazy {
        OfflineCashBookRepository(InventoryDatabase.getDatabase(context).cashBookDao())
    }
}