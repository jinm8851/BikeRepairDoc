package myung.jin.bikerepairdoc.ui.model

import myung.jin.bikerepairdoc.ui.room.BikeMemo

class ItemData() {
    var docId: String? = null
    var email: String? = null
    var roomdata = mutableListOf<BikeMemo>()
}