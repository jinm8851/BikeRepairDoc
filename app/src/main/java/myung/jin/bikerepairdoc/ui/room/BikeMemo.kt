package myung.jin.bikerepairdoc.ui.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bike_memo")
data class BikeMemo(
    @PrimaryKey(autoGenerate = true)
    var no: Int = 0,

    @ColumnInfo(name = "model")
    var model: String = "기종",

    @ColumnInfo(name = "purchaseDate")
    var purchaseDate: String = "구입날짜",

    @ColumnInfo(name = "date")
    var date: String = "수리날짜",

    @ColumnInfo(name = "km")
    var km: Int = 0,

    @ColumnInfo(name = "refer")
    var refer: String = "수리내역",

    @ColumnInfo(name = "amount")
    var amount: Int = 0,

    @ColumnInfo(name = "note")
    var note: String = "비고",

    @ColumnInfo(name = "year")
    var year: String = "년도"
)
