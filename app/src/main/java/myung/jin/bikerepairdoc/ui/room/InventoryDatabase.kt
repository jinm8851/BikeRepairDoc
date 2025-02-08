package myung.jin.bikerepairdoc.ui.room


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [BikeMemo::class],
    version = 1,
    exportSchema = false)
abstract class InventoryDatabase : RoomDatabase(){

    abstract fun bikeMemoDao(): BikeMemoDao

    companion object{
        @Volatile
        private var Instance: InventoryDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2){
           override fun migrate(database: SupportSQLiteDatabase) {
                // 버전 1에서 버전 2로 마이그레이션할 때 필요한 작업 수행
                // 현재는 데이터베이스 스키마가 변경되지 않았으므로 아무 작업도 하지 않음
                // 나중에 스키마가 변경되면 여기에 마이그레이션 로직을 추가해야 함
            }
        }
        // "bike_memo" 이거 틀려서 데이터 다 날라감
        fun getDatabase(context: Context): InventoryDatabase{
            return Instance ?: synchronized(this){
                Room.databaseBuilder(context, InventoryDatabase::class.java, "bike_memo")
                   // .fallbackToDestructiveMigration()  // 기존 데이터 삭제후 저장
                    .addMigrations(MIGRATION_1_2) // 기존 데이터 삭제 하지 않고 저장
                    .build()
                    .also { Instance = it }
            }
        }

    }

}