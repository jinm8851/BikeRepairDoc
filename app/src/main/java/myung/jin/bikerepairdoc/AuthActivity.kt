package myung.jin.bikerepairdoc


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import myung.jin.bikerepairdoc.databinding.ActivityAuthBinding
import myung.jin.bikerepairdoc.model.ItemData


class AuthActivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthBinding
    private lateinit var helper: RoomHelper
    private lateinit var bikeMemoDao: BikeMemoDao
    private var bikeList3 = mutableListOf<BikeMemo>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        helper = Room.databaseBuilder(this, RoomHelper::class.java, "bike_memo")
            .fallbackToDestructiveMigration().build()
        bikeMemoDao = helper.bikeMemoDao()

        //룸을 코루틴으로 돌리지 않으면 오류남
        CoroutineScope(Dispatchers.IO).launch {
            val filteredBikes = bikeMemoDao.getAll()
            withContext(Dispatchers.Main) {
                bikeList3.addAll(filteredBikes)
            }
        }

        if (MyApplication.checkAuth()) {
            changeVisibility("login")
        } else {
            changeVisibility("logout")
        }

        //구글 로그인 결과 처리 구글에 인증처리된 결과를 받음

        val requestLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                val account = task.getResult(ApiException::class.java)!!
                //인증서 구글에서 얻기
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                //인증서가 유효한지 판단
                MyApplication.auth.signInWithCredential(credential)
                    .addOnCompleteListener(this) { resultTask -> //통신 완료가 된 후 무슨일을 할지
                        if (resultTask.isSuccessful) {
                            MyApplication.email = account.email
                            changeVisibility("login")
                        } else {
                            changeVisibility("logout")
                        }
                    }
            } catch (e: ApiException) {
                changeVisibility("logout")
            }
        }




        binding.logoutBtn.setOnClickListener {
            //로그아웃...........
            MyApplication.auth.signOut()
            MyApplication.email = null
            changeVisibility("logout")

        }
        //로그인 화면으로 전환
        binding.goSignInBtn.setOnClickListener {
            changeVisibility("signin")
        }


        binding.googleLoginBtn.setOnClickListener {
            //구글 로그인.................... 위에준비한 런처를 실행시킨다
            val gso = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) //기본 로그인 방식 사용
                .requestIdToken(getString(R.string.default_web_client_id))
                //requestIdToken :필수사항이다. 사용자의 식별값(token)을 사용하겠다.
                //(App이 구글에게 요청)
                .requestEmail()
                // 사용자의 이메일을 사용하겠다.(App이 구글에게 요청)
                .build()  //정보 정도
            //구글기본앱 내부에 보이지 않는앱 구글인증을포괄적으로 처리해주는앱
            val signInIntent = GoogleSignIn.getClient(
                this@AuthActivity,
                gso
            ).signInIntent // 내 앱에서 구글의 계정을 가져다 쓸거니 알고 있어라!
            requestLauncher.launch(signInIntent)  //requestLauncher.launch()는 ActivityResultLauncher를 사용하여 startActivityForResult()를 대체하는 방법 중 하나입니다¹.
        }



        binding.signBtn.setOnClickListener {
            //이메일,비밀번호 회원가입........................
            val email = binding.authEmailEditView.text.toString()
            val password = binding.authPasswordEditView.text.toString()
            //구글에 등록이 됬고
            MyApplication.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task -> //통신 완료가 된 후 무슨일을 할지
                    binding.authEmailEditView.text.clear()
                    binding.authPasswordEditView.text.clear()
                    //회원가입이 성공했으면 인증메일보내기 작업
                    if (task.isSuccessful) {
                        MyApplication.auth.currentUser?.sendEmailVerification() //인증메일보내기
                            ?.addOnCompleteListener { sendTask ->
                                if (sendTask.isSuccessful) {
                                    Toast.makeText(baseContext, R.string.authsuc, Toast.LENGTH_LONG)
                                        .show()
                                    changeVisibility("logout")
                                } else {
                                    Toast.makeText(
                                        baseContext,
                                        R.string.emailfail,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    } else {
                        Toast.makeText(baseContext, R.string.authfail, Toast.LENGTH_LONG).show()
                        changeVisibility("logout")
                    }
                }

        }

        binding.loginBtn.setOnClickListener {
            //이메일, 비밀번호 로그인.......................
            val email = binding.authEmailEditView.text.toString()
            val password = binding.authPasswordEditView.text.toString()
            Log.d("kkang", "email:$email, password:$password")
            MyApplication.auth.signInWithEmailAndPassword(email, password) //firebase와 연동
                .addOnCompleteListener(this) { task ->
                    binding.authEmailEditView.text.clear()
                    binding.authPasswordEditView.text.clear()
                    if (task.isSuccessful) {
                        if (MyApplication.checkAuth()) {
                            MyApplication.email = email
                            changeVisibility("login")
                        } else {
                            Toast.makeText(baseContext, R.string.eauthfail, Toast.LENGTH_LONG)
                                .show()
                        }
                    } else {
                        Toast.makeText(baseContext, R.string.logfail, Toast.LENGTH_LONG).show()
                    }
                }

        }

        binding.dataput.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                if (MyApplication.checkAuth()) {
                    saveStore()
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, R.string.authrun, Toast.LENGTH_LONG)
                            .show()
                    }
                }

            }
        }
        binding.datapull.setOnClickListener {
            //룸을 코루틴으로 돌리지 않으면 오류남
            CoroutineScope(Dispatchers.IO).launch {
                receiveStore()
                withContext(Dispatchers.Main) {
                }
            }
        }
        // 뒤로가기 버튼
        binding.backwords.setOnClickListener {
            // 현재 프래그먼트 스택이 비어있지 않으면 이전 프래그먼트로 이동
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
            } else {
                // 프래그먼트 스택이 비어있을 경우, 이전 화면(액티비티)로 돌아가도록 할 수 있습니다.
                finish()
            }
        }

    }

    /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         super.onActivityResult(requestCode, resultCode, data)
         //구글 로그인 결과처리

     }*/
    fun changeVisibility(mode: String) {
        val myemail = MyApplication.email.toString()
        if (mode === "login") {
            binding.run {
                authMainTextView.text = getString(R.string.emailnice, myemail)
                logoutBtn.visibility = View.VISIBLE
                goSignInBtn.visibility = View.GONE
                googleLoginBtn.visibility = View.GONE
                authEmailEditView.visibility = View.GONE
                authPasswordEditView.visibility = View.GONE
                signBtn.visibility = View.GONE
                loginBtn.visibility = View.GONE
                dataput.visibility = View.VISIBLE
                datapull.visibility = View.VISIBLE
            }

        } else if (mode === "logout") {
            binding.run {
                authMainTextView.text = getString(R.string.authlogin)
                logoutBtn.visibility = View.GONE
                goSignInBtn.visibility = View.VISIBLE
                googleLoginBtn.visibility = View.VISIBLE
                authEmailEditView.visibility = View.VISIBLE
                authPasswordEditView.visibility = View.VISIBLE
                signBtn.visibility = View.GONE
                loginBtn.visibility = View.VISIBLE
                datapull.visibility = View.GONE
                dataput.visibility = View.GONE
            }
        } else if (mode === "signin") {
            binding.run {
                logoutBtn.visibility = View.GONE
                goSignInBtn.visibility = View.GONE
                googleLoginBtn.visibility = View.GONE
                authEmailEditView.visibility = View.VISIBLE
                authPasswordEditView.visibility = View.VISIBLE
                signBtn.visibility = View.VISIBLE
                loginBtn.visibility = View.GONE
                dataput.visibility = View.GONE
                datapull.visibility = View.GONE
            }
        }
    }

    //데이터 저장 컬렉션을 사용자이메일로 변경
    private fun saveStore() {

        val data = mapOf(
            "email" to MyApplication.email,
            "roomdata" to bikeList3
        )

        MyApplication.db.collection("${MyApplication.email}")
            .add(data)
            .addOnSuccessListener {
                Toast.makeText(this, R.string.datacom, Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, R.string.datafail, Toast.LENGTH_LONG).show()
            }
    }

    //데이터 수신 메서드 사용자 이메일로 받음 삭제함
    private fun receiveStore() {

        MyApplication.db.collection("${MyApplication.email}")
            .get()
            .addOnSuccessListener { result ->
                bikeList3.clear()

                    for (document in result) {
                        val item = document.toObject(ItemData::class.java)
                        item.docId = document.id
                        val email = item.email
                        if (MyApplication.email == email) {
                            bikeList3 = item.roomdata
                            for (room in bikeList3) {
                                val bikeName = room.model
                                val startDate = room.purchaseDate
                                val repairDate = room.date
                                val km = room.km
                                val content = room.refer
                                val amount = room.amount
                                val note = room.note
                                val year = room.year
                                val memo = BikeMemo(
                                    bikeName,
                                    startDate,
                                    repairDate,
                                    km,
                                    content,
                                    amount,
                                    note,
                                    year
                                )
                                insertBikeMemo(memo)
                                Log.d("bikename", bikeName)
                                Log.d("email", "$email")
                            }

                            Toast.makeText(
                                baseContext,
                                "ID $email  ${getString(R.string.sent)}",
                                Toast.LENGTH_LONG
                            ).show()
                            deleted()
                        } else {
                            Toast.makeText(baseContext, R.string.emailnm, Toast.LENGTH_LONG).show()
                        }

                    }

            }
            .addOnFailureListener {
                Toast.makeText(this, R.string.fdata, Toast.LENGTH_LONG).show()
                Log.d("doc", "doc...error")
            }
    }

    //삭제 메서드 사용자 이메일로 삭제함
    private fun deleted() {

        MyApplication.db.collection("${MyApplication.email}")
            .get()
            .addOnSuccessListener {
                //이메일로된 컬렉션의 모든 다규먼트아이디를 삭제함
                for (document in it) {
                    MyApplication.db.collection("${MyApplication.email}").document(document.id)
                        .delete().addOnSuccessListener {
                            Log.d("delete", "deleteSuccess")
                        }
                }

            }

    }

    //바이크 메모를 룸에 입력
    private fun insertBikeMemo(memo: BikeMemo) {
        CoroutineScope(Dispatchers.IO).launch {
            bikeMemoDao.insert(memo)
        }
    }

}