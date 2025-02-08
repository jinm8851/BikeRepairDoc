package myung.jin.bikerepairdoc.ui.screen.authScreen


import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import myung.jin.bikerepairdoc.ui.model.ItemData
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import myung.jin.bikerepairdoc.ui.room.BikeMemo
import myung.jin.bikerepairdoc.ui.room.BikeMemoRepository

class AuthViewModel(
    private val bikeMemoRepository: BikeMemoRepository
) : ViewModel() {
    companion object {
        private const val TAG = "구글 인증"
    }

    //화이어베이스에서 인증정보를 가져오는 변수
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // 화이어베이스에서 데이터를 가져오는 변수
    private val firestoreDb: FirebaseFirestore = FirebaseFirestore.getInstance()

    //인증상태를 저장하는 변수
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    //인증된 이메일을 저장하는 변수
    private val _authEmail = MutableStateFlow<String?>(null)
    val authEmail: StateFlow<String?> = _authEmail.asStateFlow()

    // 토스트메세지가 변경되면 메세지실행
    private val _toastMessage = MutableStateFlow<Event<String>?>(null)
    val toastMessage: StateFlow<Event<String>?> = _toastMessage.asStateFlow()

    // 바이크메모 리스트 초기화
    private val bikeList: MutableList<BikeMemo> = mutableListOf<BikeMemo>()

    //데이터를 가져오는 코루틴을 viewModelScope.launch로 관리하고, 뷰모델이 살아있는 동안 데이터를 관찰하고 업데이트합니다.
    //데이터 저장 및 삭제와 같은 코루틴은 별도의 스코프(CoroutineScope(Dispatchers.IO))를 사용하여 뷰모델 생명주기와 분리합니다.
    private val ioScope = CoroutineScope(Dispatchers.IO)

    //뷰모델 초기화시  인증상태확인
    init {
        // 로그인 상태 체크 밎 성공시 이메일 업데이트
        checkAuthStatus()
        // 바이크메모 넣기
        fetchBikeMemos()
    }

    // 바이크메모를 불러와서 리스트에 Flow를 컬렉션으로 List로 변경해 넣기
    private fun fetchBikeMemos() {
        viewModelScope.launch {
            bikeMemoRepository.getAllBikeMemoStream().collect { bikeMemos ->
                bikeList.clear()
                bikeList.addAll(bikeMemos)
            }
        }
    }

    //앱이 시작될 때 또는 로그인 상태가 변경될 때 호출되어 현재 로그인 상태를 확인하고
    // _authState와 _authEmail을 업데이트합니다.
    private fun checkAuthStatus() {
        auth.currentUser?.let { user ->  // currentUser 가 널 이아니면
            _authState.value = AuthState.Authenticated
            _authEmail.value = user.email
        } ?: run {                       // null 실행
            _authState.value = AuthState.Unauthenticated
            _authEmail.value = null
        }
    }

    //이메일과 비밀번호로 로그인을 시도합니다. 성공하면 updateUI()를 호출하여 UI를 업데이트합니다.
    fun signIn(email: String, password: String) {
        authenticateUser(email, password, isSignUp = false)
    }

    //이메일과 비밀번호로 회원가입을 시도합니다. 성공하면 updateUI()를 호출하여 UI를 업데이트합니다.
    fun signUp(email: String, password: String) {
        authenticateUser(email, password, isSignUp = true)
    }

    // 이메일과 페스워드를 받아 회원가입 또는  로그인
    private fun authenticateUser(email: String, password: String, isSignUp: Boolean) {
        if (!validateEmailAndPassword(email, password)) return
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val task = if (isSignUp) { // 회원가입 여부
                    auth.createUserWithEmailAndPassword(email, password) // 회원가입 시도
                } else {
                    auth.signInWithEmailAndPassword(email, password)  // 회원 로그인 시도
                }.await()

                val user = task.user
                if (user != null) {
                    if (isSignUp) {
                        user.sendEmailVerification().await() // 검증이메일 보내기
                        showToast("회원가입 성공, 전송된 메일을 확인해주세요.")
                    }
                    updateUI(user)  // 화면변경
                    showToast("$email 님 ${if (isSignUp) "회원가입" else "로그인"} 성공")
                } else {
                    _authState.value = AuthState.Error("로그인 실패")
                    showToast("로그인 실패")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "인증 실패")
                Log.e(TAG, "인증 실패", e)
                showToast(if (isSignUp) "회원가입 실패" else "로그인 실패")
            }
        }
    }

    //이메일 코드 유효성 검사
    private fun validateEmailAndPassword(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("이메일과 비밀번호를 입력하세요")
            showToast("이메일과 비밀번호를 입력하세요")
            return false
        }
        return true
    }

    // 구글 로그인
    fun googleSignIn(context: Context) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                // 구글 자격증명 받아오기
                val googleCredential = getCredential(context).credential
                // 구글 자격증명을 넘겨줘서 로그인함
                handleGoogleSignIn(googleCredential)
            } catch (e: Exception) {
                _authState.value = AuthState.Error("구글 로그인 실패 : ${e.message}")
                Log.d(TAG, "구글 로그인 실패 : ${e.message}")
            }
        }
    }

    // context 를 넘겨받아 자격증명을 가져옴
    private suspend fun getCredential(context: Context): GetCredentialResponse {
        //자격증명 관리자 객체
        val credentialManager = CredentialManager.create(context)
        val request = buildCredentialRequest() // 자격증명 가져오기

        return try {
            // 내 요청을 만족하는 자격증명을 credentialManager 입력
            credentialManager.getCredential(context = context, request = request)
        } catch (e: GetCredentialException) { //GetCredentialException은 자격 증명 검색 과정에서 오류가 발생했을 때 발생합니다.
            // 예외 처리
            credentialManager.clearCredentialState(ClearCredentialStateRequest()) // 자격증명 상태 지우기
            _authState.value = AuthState.Error("구글 로그인 실패 : ${e.message}")
            Log.d(TAG, "구글 로그인 실패 : ${e.message}")
            throw e
        } catch (e: GetCredentialCancellationException) { // GetCredentialCancellationException은 사용자가 작업을 취소했을 때 발생합니다.
            // 예외 처리
            credentialManager.clearCredentialState(ClearCredentialStateRequest()) // 자격증명 상태 지우기
            _authState.value = AuthState.Error("구글 로그인 실패 : ${e.message}")
            Log.d(TAG, "구글 로그인 실패 : ${e.message}")
            throw e
        } catch (e: CancellationException) { //CancellationException은 사용자가 작업을 취소했을 때 발생합니다.
            // 예외 처리
            credentialManager.clearCredentialState(ClearCredentialStateRequest()) // 자격증명 상태 지우기
            _authState.value = AuthState.Error("구글 로그인 실패 : ${e.message}")
            Log.d(TAG, "구글 로그인 실패 : ${e.message}")
            throw e
        }


    }

    // 자격 증명을 받아 오기
    private fun buildCredentialRequest(): GetCredentialRequest {
        return GetCredentialRequest.Builder()
            .addCredentialOption(
                GetGoogleIdOption.Builder() // 옵션설정
                    .setFilterByAuthorizedAccounts(false) //권한이 있는계정만 표지(true) 모든계정표시(false)
                    .setServerClientId( // firebase에서 얻오옴
                        "1014785864624-fkrf144vkqpkpeumcglteks7egbqhngv.apps.googleusercontent.com" // web client id 나중에 자기 앱으로 변경해야함
                    )
                    .setAutoSelectEnabled(false) // 자동선택(false) 사용자가 원하는 계정선택
                    .build()
            )
            .build()
    }

    // googleSignIn에서 자격 증명을 받아서 여기로 전달
    private suspend fun handleGoogleSignIn(credential: Credential) {
        if (
            credential is CustomCredential &&  // 받아온 자격증명이 사용자 지정 자격증명이고
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL  // 타입이 이것과 같으면
        ) {
            try {
                // 구글자격증명을 받았지만 아직 로그인하지못함
                val tokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                // 구글에서 만든 작격증명을 화이어베이스에 넘겨 로그인함
                val authCredential = GoogleAuthProvider.getCredential(tokenCredential.idToken, null)
                // 자격증명과 idToken을 넘겨줌
                val authResult = auth.signInWithCredential(authCredential).await()
                if (authResult.user != null) {
                    _authEmail.value = tokenCredential.id
                    showToast("${tokenCredential.id} 구글 인증 성공.")
                    updateUI(authResult.user)
                } else {
                    _authState.value = AuthState.Error("구글 로그인 실패")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("구글 로그인 실패 : ${e.message}")
                Log.d(TAG, "구글 로그인 실패 : ${e.message}")
            }
        } else {
            _authState.value = AuthState.Error("잘못된 구글 자격증명")
            Log.d(TAG, "credential is not GoogleIdTokenCredential")
        }
    }

    //로그 아웃
    fun signOut() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
        _authEmail.value = null
        //  credentialManager.clearCredentialState(ClearCredentialStateRequest()) // 자격증명 상태 지우기
    }

    //회원가입스크린
    fun signUpScreen() {
        _authState.value = AuthState.SignInScreen
    }

    // updateUI(user: FirebaseUser?): 로그인 성공 또는 실패 시 호출되어 _authState와 _authEmail을 업데이트합니다.
    // user가 null이 아니면 로그인 성공으로 간주하고, user.email을 _authEmail에 저장합니다.
    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            _authState.value = AuthState.Authenticated
            _authEmail.value = user.email
            showToast("${_authEmail.value}님 반갑습니다")
        } else {
            _authState.value = AuthState.Unauthenticated //
            _authEmail.value = null
        }
    }

    // 토스트 메세지전달
    fun showToast(message: String) {
        _toastMessage.value = Event(message)
    }

    // fireStore 저장
    fun fireStoreSave() {
        ioScope.launch {
            try {
                val data = mapOf(
                    "email" to _authEmail.value.toString(), // 키와 값으로 data 에 저장
                    "roomdata" to bikeList // roomData 필드에 BikeMemo에서 가져온 bikeList를 저장
                )
                firestoreDb.collection(_authEmail.value.toString()) // 컬렉션 이름
                    .document() // 문서이름 지정으로 하나의 문서만 저장되게함document(_authEmail.value.toString()) 을 뺌 이메일로 저정되는데 바굼 오류
                    .set(data) // set으로 중복 허용안함
                    .await()
                showToast("데이터 전송을 완료했습니다.")
            } catch (e: Exception) {
                showToast("데이터 전송에 실패했습니다.")
                Log.e(TAG, "데이터 전송 실패", e)
            }
        }
    }

    fun fireStoreGetData() {
        ioScope.launch {
            try {
                val result = firestoreDb.collection(_authEmail.value.toString()).get().await()
                var dataReceived = false // 데이터 수신여부 확인 플래그
                for (document in result) {
                    val itemData = document.toObject(ItemData::class.java) // 데이터를 ItemData 객체로 변환
                    itemData.docId = document.id // 문서 ID를 설정
                    Log.d("firebase", "itemData.docId: ${itemData.docId}")
                   // itemData.email = document.email // 이메일로 아이디 저장
                    Log.d("firebase", "itemData.email: ${itemData.email}")
                    if (_authEmail.value.toString() == itemData.email) {
                        dataReceived = true // 데이터 수신 확인
                        itemData.roomdata.forEach { room ->
                            val memo = BikeMemo(
                                no = room.no,
                                model = room.model,
                                purchaseDate = room.purchaseDate,
                                date = room.date,
                                km = room.km,
                                refer = room.refer,
                                amount = room.amount,
                                note = room.note,
                                year = room.year
                            )
                            Log.d("firebase", "memo: $memo.id")
                            bikeMemoRepository.insertBikeMemo(memo)
                        }
                        showToast("${itemData.email} 님 데이터를 전송 받았습니다.")
                    }
                }
                if (!dataReceived) {
                    showToast("${_authEmail.value} 님 저장한 데이터를 전송 받지 못했습니다.")
                }
            } catch (e: Exception) {
                showToast("${_authEmail.value} 님 저장한 데이터를 전송 받지 못했습니다.")
                Log.d(TAG, "${_authEmail.value} 님 저장한 데이터를 전송 받지 못했습니다.", e)
            } finally {
                fireStoreDelete()
            }
        }
    }

    // 모든 문서 삭제
    fun fireStoreDelete() {
        ioScope.launch{
            try {
                val result = firestoreDb.collection(_authEmail.value.toString()).get()
                result.addOnSuccessListener{
                    for (document in it) {
                        document.reference.delete()
                    }
                }
            }catch (e: Exception) {
                Log.d("firebase", "화이어베이스 데이터 삭제 실패", e)
            }
        }
       /* email document 로 저장했을때 ( 특정 문서 삭제)
       ioScope.launch {
            try {
                firestoreDb.collection(_authEmail.value.toString()) // 이메일로된 컬렉션
                    .document(_authEmail.value.toString()) 을 변경
                    .delete()
                    .await()  // 비동기 작업이 완료될 때까지 기다립니다.
                Log.d(TAG, "화이어베이스 데이터 삭제")
            } catch (e: Exception) {
                Log.d(TAG, "화이어베이스 데이터 삭제 실패", e)
            }

        } */
    }

    override fun onCleared() {
        super.onCleared()
        ioScope.cancel()
    }
}


// 로그인 상태를 나타내는 sealed 클래스입니다.
sealed class AuthState {
    object Authenticated : AuthState() // 로그인 됨
    object Unauthenticated : AuthState() // 로그인 안됨
    object Loading : AuthState()  // 로그인 중
    object SignInScreen : AuthState() // 회원가입 화면으로 이동
    data class Error(val message: String) : AuthState() // 로그인 실패 메세지 전달
}

// Event 클래스: Toast 메시지와 같은 이벤트를 한 번만 처리하도록 보장하는 래퍼 클래스입니다.
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // 외부 읽기는 허용하지만 쓰기는 허용하지 않음

    // 콘텐츠를 반환하고 다시 사용하지 못하도록 합니다.
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    //  이미 처리된 내용이라도 반환합니다.
    fun peekContent(): T = content
}