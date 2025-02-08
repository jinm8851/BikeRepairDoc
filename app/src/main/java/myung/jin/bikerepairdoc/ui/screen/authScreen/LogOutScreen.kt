package myung.jin.bikerepairdoc.ui.screen.authScreen


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import myung.jin.bikerepairdoc.R


@Composable
fun UnauthenticatedContent(
    signUpScreenOnClick: () -> Unit,
    googleAuthOnClick: () -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    logInOnClick: () -> Unit,
    onBackClick: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(id = R.string.auth_login),
            style = TextStyle(fontSize = 24.sp),
            textAlign = TextAlign.Justify,
            color = Color(0xFF0B6380),
            modifier = Modifier.padding(16.dp)
        )

        ShowAuthButton(onClick = googleAuthOnClick, stringResource = R.string.sign_in_google) // 구글인증
        Spacer(Modifier.padding(16.dp))
        ShowAuthButton(onClick = signUpScreenOnClick, stringResource = R.string.sign_in_email) // 회원가입
        Spacer(Modifier.padding(16.dp))
        ShowAuthText(
            modifier = Modifier,
            value = email, onValueChange = onEmailChange, stringResource = R.string.email,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
        )
        ShowAuthText(
            modifier = Modifier,
            value = password, onValueChange = onPasswordChange, stringResource = R.string._6,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation() // 비밀번호 가리기
        )
        ShowAuthButton(onClick = logInOnClick, stringResource = R.string.login)
        Spacer(Modifier.padding(16.dp))
        ShowAuthButton(onClick = onBackClick, stringResource = R.string.back_words)
        Spacer(Modifier.padding(16.dp))
        Text(
            stringResource(id = R.string.logout_screen_info),
            style = TextStyle(fontSize = 24.sp),
            textAlign = TextAlign.Center,
            color = Color(0xFF0B6380),
            modifier = Modifier.padding(16.dp)
        )
    }

}

