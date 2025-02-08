package myung.jin.bikerepairdoc.ui.screen.authScreen


import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import myung.jin.bikerepairdoc.InventoryTopAppBar
import myung.jin.bikerepairdoc.ui.navigation.NavigationDestination.NavigationDestination
import myung.jin.bikerepairdoc.R
import myung.jin.bikerepairdoc.ui.AppViewModelProvider
import myung.jin.bikerepairdoc.ui.theme.shapes

object AuthDetailScreenDestination : NavigationDestination {
    override val route: String = "AuthDetail"
    override val titleRes: Int = R.string.authDetailScreen
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthDetailScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    authViewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    // 화면 이메일
    var email by remember { mutableStateOf("") }
    // 화면 패스워드
    var password by remember { mutableStateOf("") }
    // 로그인 상태
    var authState = authViewModel.authState.collectAsState()
    // 로그인한 이메일
    val authEmail by authViewModel.authEmail.collectAsState()
    // Toast 메세지
    val context = LocalContext.current
    val toastMessage by authViewModel.toastMessage.collectAsState()

    LaunchedEffect(toastMessage) { // 키가 변경되면 자동으로 실행 (key = toastMessage)
        toastMessage?.getContentIfNotHandled()?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier,
        topBar = {
            InventoryTopAppBar(
                title = stringResource(AuthDetailScreenDestination.titleRes),
                canNavigateBack = true,  // 뒤로가기 화살표
                canNavigateForward = false,  // 앞으로가기 화살표
                modifier = modifier,
                scrollBehavior = scrollBehavior,  // 스크로
                onNavigateBack = {
                    navHostController.navigate(AuthScreenDestination.route)
                }, // 뒤로가기 버튼
                onNavigateForward = { }, // 앞으로가기 버튼
            )
        }
    ) { innerPadding ->
        AuthDetailScreenContent(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(color = Color(0xffFEF9CF)),
            email = email, // 이메일 값 전달
            onEmailChange = { newValue -> email = newValue }, // 변경된 이메일 값 표시
            authEmail = authEmail.toString(), // fireStore 에서 받은 이메일 표시
            logOutOnClick = {         // 로그 아웃
                coroutineScope.launch {
                    authViewModel.signOut() // 파이어베이스 로그 아웃
                    navHostController.navigate(AuthScreenDestination.route) // 인증세부 화면 이동
                }
            },
            dataPutOnClick = {
                coroutineScope.launch {
                    authViewModel.fireStoreSave() // 화이어베이스에 데이터 전송
                }
            },
            dataPullOnClick = {
                coroutineScope.launch {
                    authViewModel.fireStoreGetData() // 화이어베이스에서 데이터 가져오기
                    navHostController.navigate(AuthScreenDestination.route)
                }
            },
            backWordOnClick = {
                coroutineScope.launch {
                    navHostController.navigate(AuthScreenDestination.route)
                }
            }, // 뒤로가기 버튼
            signUpScreenOnClick = {
                authViewModel.signUpScreen()  // 회원 가입 스크린으로 이동
            },
            password = password,
            onPasswordChange = { newPassword -> password = newPassword },
            logInOnClick = {
                coroutineScope.launch {
                    authViewModel.signIn(email, password) // 이메일 로그인 시도
                    email = ""  // 로그인실패시 빈 화면으로 변경
                    password = ""
                }
            },
            signUpOnClick = {
                coroutineScope.launch {
                    authViewModel.signUp(email, password) // 이메일 회원가입 시도
                    email = ""
                    password = ""
                }
            }, // 로그인실패시 빈 화면으로 변경
            authState = authState.value,
            authViewModel = authViewModel,
            googleAuthOnClick = {
                coroutineScope.launch {
                    authViewModel.googleSignIn(context) // 구글 로그인 시도
                }
            }
        )
    }
}

@Composable
fun AuthDetailScreenContent(
    modifier: Modifier,
    email: String,
    onEmailChange: (String) -> Unit,
    authEmail: String,
    logOutOnClick: () -> Unit,
    dataPutOnClick: () -> Unit,
    dataPullOnClick: () -> Unit,
    backWordOnClick: () -> Unit,
    signUpScreenOnClick: () -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    logInOnClick: () -> Unit,
    signUpOnClick: () -> Unit,
    authState: AuthState?,
    authViewModel: AuthViewModel,
    googleAuthOnClick: () -> Unit
) {


    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 로그인 성공 상태에 따라 화면 바뀜
        when (authState) {
            AuthState.Authenticated -> { // 로그인한 화면 로그인성공 상태
                AuthenticatedContent(
                    authEmail = authEmail, // authState.value.email, 로그인 상태
                    logOutClick = logOutOnClick,
                    dataPutOnClick = dataPutOnClick,
                    dataPullOnClick = dataPullOnClick,
                    backWordOnClick = backWordOnClick
                )
            }
            AuthState.Unauthenticated -> { // 로그아웃 화면 로그인 실패상태
                UnauthenticatedContent(
                    googleAuthOnClick = googleAuthOnClick,
                    signUpScreenOnClick = signUpScreenOnClick,
                    email = email, onEmailChange = onEmailChange,
                    password = password, onPasswordChange = onPasswordChange,
                    logInOnClick = logInOnClick,
                    onBackClick = backWordOnClick
                )
            }

            AuthState.SignInScreen -> { // 이메일 회원가입 화면
                SignInContent(
                    email = email, onEmailChange = onEmailChange,
                    password = password, onPasswordChange = onPasswordChange,
                    signUpOnClick = signUpOnClick,
                    modifier = Modifier

                )
            }

            AuthState.Loading -> {
                LoadingScreen(modifier = modifier)
            }

            is AuthState.Error -> authViewModel.showToast(authState.message)

            else -> {}
        }
    }
}


@Composable
fun ShowAuthButton(
    onClick: () -> Unit,
    @StringRes
    stringResource: Int
) {
    OutlinedButton(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0x32B193E6),// 텍스트 색상
            disabledContentColor = Color(0xFF703BE1),// 비활성화상태에서 텍스트색상
            containerColor = Color(0x32B193E6),// 버튼배경색상
            disabledContainerColor = Color(0xFF703BE1) // 비 활성화상태에서 배경색상
        ),
        border = BorderStroke(1.dp, Color(0xFF703BE1)),
    ) {
        Text(
            text = stringResource(stringResource),
            color = Color(0xFF703BE1),
            style = TextStyle(
                fontSize = 25.sp,
            ),
        )
    }
}

@Composable
fun ShowAuthText(
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    stringResource: Int,
    keyboardOptions: KeyboardOptions,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,//{ newValue -> onValueChange(newValue) },
        label = { Text(text = stringResource(id = stringResource)) },
        placeholder = { Text(text = stringResource(id = stringResource)) },
        keyboardOptions = keyboardOptions,// KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF703BE1),
            unfocusedBorderColor = Color(0xFF703BE1),
            focusedLabelColor = Color(0xFF703BE1),
            unfocusedLabelColor = Color(0xFF703BE1),
        ),
        modifier = modifier
            .background(color = Color(0x32B193E6)),
        textStyle = TextStyle(
            fontSize = 20.sp,
            color = Color(0xFF703BE1), textAlign = TextAlign.Center
        ),
        shape = shapes.small,
        singleLine = true,
        visualTransformation = visualTransformation
    )
}

