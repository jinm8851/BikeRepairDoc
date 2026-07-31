package myung.jin.bikerepairdoc.ui.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// 업데이트 할때 var를 val로 변경
@Entity(tableName = "bike_memo")
data class BikeMemo(
    @PrimaryKey(autoGenerate = true)
    val no: Int = 0,

    @ColumnInfo(name = "model")
    val model: String = "",

    @ColumnInfo(name = "purchaseDate")
    val purchaseDate: String = "",

    @ColumnInfo(name = "date")
    val date: String = "",

    @ColumnInfo(name = "km")
    val km: Int = 0,

    @ColumnInfo(name = "refer")
    val refer: String = "",

    @ColumnInfo(name = "amount")
    val amount: Int = 0,

    @ColumnInfo(name = "note")
    val note: String = "",

    @ColumnInfo(name = "year")
    val year: String = ""
)

// 사용자가 수출입 내용을 저장하는 테이블
@Entity(tableName = "content_name")
data class ContentName(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String = ""
)

// 수입 지출 기록 저장
@Entity(tableName = "cash_book")
data class CashBook(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String = "",
    val content: String = "", // 내용(드롭다운 선택 항목)
    val income: Long = 0, // 수입
    val expense: Long = 0 // 지축
)