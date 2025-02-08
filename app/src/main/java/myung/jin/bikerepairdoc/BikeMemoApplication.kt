package myung.jin.bikerepairdoc

import android.app.Application
import myung.jin.bikerepairdoc.ui.room.AppContainer
import myung.jin.bikerepairdoc.ui.room.AppDataContainer


class BikeMemoApplication : Application() {

    // 시작할때 앱콘테이너를 통해 룸에 접근함
    lateinit var appContainer: AppContainer
        private set // Ai 추가
    // FirebaseAuth 의 인스텀스를 선언합니다.
    // lateinit var auth: FirebaseAuth

    override fun onCreate() {
        super.onCreate()
        appContainer = AppDataContainer(this)
        //  auth = Firebase.auth
    }


}