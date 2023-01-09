package myung.jin.bikerepairdoc

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bike_memo")
class BikeMemo {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    var no: Int = 0

    @ColumnInfo
    var model: String = "기종"

    @ColumnInfo
    var purchaseDate: String = "구입날짜"

    @ColumnInfo(name = "date")
    var date: String = "수리날짜"

    @ColumnInfo
    var km: Int = 0

    @ColumnInfo
    var refer: String = "수리내역"

    @ColumnInfo
    var amount: Int = 0

    @ColumnInfo
    var note : String = "비고"


    constructor(model:String,purchaseDate:String,date:String,km:Int,refer:String,amount:Int,note:String){
        this.model = model
        this.purchaseDate = purchaseDate
        this.date = date
        this.km = km
        this.refer = refer
        this.amount = amount
        this.note = note
    }
}