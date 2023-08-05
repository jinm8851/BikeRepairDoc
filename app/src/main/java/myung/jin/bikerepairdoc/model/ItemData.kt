package myung.jin.bikerepairdoc.model

import myung.jin.bikerepairdoc.BikeMemo

class ItemData() {
    var docId: String? = null
    var email: String? = null
    var roomdata = mutableListOf<BikeMemo>()
}