package myung.jin.bikerepairdoc.ui.model

import myung.jin.bikerepairdoc.ui.room.BikeMemo
import myung.jin.bikerepairdoc.ui.room.CashBook
import myung.jin.bikerepairdoc.ui.room.ContentName

class ItemData() {
    var docId: String? = null
    var email: String? = null
    var uid: String? = null
    var roomdata = mutableListOf<BikeMemo>()
    var cashBookData = mutableListOf<CashBook>()
    var contentNameData = mutableListOf<ContentName>()
}