package myung.jin.bikerepairdoc.ui.room

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// 업데이트 할때 var를 val로 변경
@Keep // @Keep 어노테이션을 붙여 난독화 대상에서 제외해 주세요. 역직렬화 오류 다운받을때 이름이 변경되 오류 나는것을 방지
@Entity(tableName = "bike_memo")
data class BikeMemo(
    @PrimaryKey(autoGenerate = true)
    var no: Int = 0,

    @ColumnInfo(name = "model")
    var model: String = "",

    @ColumnInfo(name = "purchaseDate")
    var purchaseDate: String = "",

    @ColumnInfo(name = "date")
    var date: String = "",

    @ColumnInfo(name = "km")
    var km: Int = 0,

    @ColumnInfo(name = "refer")
    var refer: String = "",

    @ColumnInfo(name = "amount")
    var amount: Int = 0,

    @ColumnInfo(name = "note")
    var note: String = "",

    @ColumnInfo(name = "year")
    var year: String = ""
)

// 사용자가 수출입 내용을 저장하는 테이블
@Keep
@Entity(tableName = "content_name")
data class ContentName(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var name: String = ""
)

// 수입 지출 기록 저장
@Keep
@Entity(tableName = "cash_book")
data class CashBook(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var date: String = "",
    var content: String = "", // 내용(드롭다운 선택 항목)
    var income: Long = 0, // 수입
    var expense: Long = 0 // 지축
)