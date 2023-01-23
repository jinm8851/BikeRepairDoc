package myung.jin.bikerepairdoc

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(BikeMemo::class), version = 1, exportSchema = false)
abstract class RoomHelper : RoomDatabase() {
    abstract fun bikeMemoDao(): BikeMemoDao
}