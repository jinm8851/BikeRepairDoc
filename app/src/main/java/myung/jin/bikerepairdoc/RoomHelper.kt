package myung.jin.bikerepairdoc

import androidx.room.Database

@Database(entities = arrayOf(BikeMemo::class), version = 1, exportSchema = false)
abstract class RoomHelper {
    abstract fun bikeMemoDao(): BikeMemoDao
}