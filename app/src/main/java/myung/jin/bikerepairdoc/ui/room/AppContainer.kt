package myung.jin.bikerepairdoc.ui.room

import android.content.Context

interface AppContainer {
    val bikeMemoRepository: BikeMemoRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val bikeMemoRepository: BikeMemoRepository by lazy {
        OfflineBikeMemoRepository(InventoryDatabase.getDatabase(context).bikeMemoDao())
    }
}