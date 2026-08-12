package myung.jin.bikerepairdoc.ui.model

import androidx.annotation.Keep
import myung.jin.bikerepairdoc.ui.room.BikeMemo
import myung.jin.bikerepairdoc.ui.room.CashBook
import myung.jin.bikerepairdoc.ui.room.ContentName

@Keep // @Keep 어노테이션을 붙여 난독화 대상에서 제외해 주세요. 역직렬화 오류 다운받을때 이름이 변경되 오류 나는것을 방지
class ItemData() {
    var email: String? = null
    var uid: String? = null
    var roomdata = mutableListOf<BikeMemo>()
    var cashBookData = mutableListOf<CashBook>()
    var contentNameData = mutableListOf<ContentName>()
}