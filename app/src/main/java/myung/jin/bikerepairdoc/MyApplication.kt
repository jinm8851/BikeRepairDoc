package myung.jin.bikerepairdoc

import androidx.multidex.MultiDexApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
//multidex 상속 어플리케이션 객체를 쉽게 이용하게 하기위해
class MyApplication: MultiDexApplication() {
    companion object {
        lateinit var auth: FirebaseAuth
        var email: String? = null  //유저가 입력한 이메일 값

        lateinit var db: FirebaseFirestore

        //인증이 된건지 체크하는 함수를 이쪽 저쪽에서 사용하기 위해서
        fun checkAuth(): Boolean {
            var currentUser = auth.currentUser
            return currentUser?.let {
                email = currentUser.email
                currentUser.isEmailVerified
            } ?: let {
                false
            }
        }



    }

    override fun onCreate() {
        super.onCreate()
        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()
    }
}