package myung.jin.bikerepairdoc.ui.screen.authScreen


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import myung.jin.bikerepairdoc.R



@Composable
fun SignInContent(
    modifier: Modifier,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    signUpOnClick: () -> Unit,
) {

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ShowAuthText(
            modifier = Modifier,
            value = email, onValueChange = onEmailChange,
            stringResource = R.string.email,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.padding(4.dp))
        ShowAuthText(
            modifier = Modifier,
            value = password,
            onValueChange = onPasswordChange,
            stringResource = R.string._6,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation() // 비밀번호 가리기
        )
        Spacer(modifier = Modifier.padding(4.dp))
        ShowAuthButton(
            onClick = signUpOnClick,
            stringResource = R.string.sign_up_button
        )
    }

}