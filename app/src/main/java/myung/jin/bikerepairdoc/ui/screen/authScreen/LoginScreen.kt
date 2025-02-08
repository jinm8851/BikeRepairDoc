package myung.jin.bikerepairdoc.ui.screen.authScreen


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import myung.jin.bikerepairdoc.ui.theme.BikeRepairDocTheme
import myung.jin.bikerepairdoc.R

@Composable
fun AuthenticatedContent(
    authEmail: String,
    logOutClick: () -> Unit,
    dataPutOnClick: () -> Unit,
    dataPullOnClick: () -> Unit,
    backWordOnClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(id = R.string.email_nice, authEmail),
            style = TextStyle(fontSize = 24.sp),
            textAlign = TextAlign.Center,
            color = Color(0xFF0B6380),
            modifier = Modifier.padding(16.dp)
        )
        ShowAuthButton(onClick = logOutClick, stringResource = R.string.logout) // 로그 아웃
        Spacer(Modifier.padding(16.dp))
        ShowAuthButton(onClick = dataPutOnClick, stringResource = R.string.data_put) // 데이터 전송
        Spacer(Modifier.padding(16.dp))
        ShowAuthButton(onClick = dataPullOnClick, stringResource = R.string.data_pull) // 데이터 수신
        Spacer(Modifier.padding(16.dp))
        ShowAuthButton(onClick = backWordOnClick, stringResource = R.string.back_words) // 뒤로 가기
        Spacer(Modifier.padding(16.dp))
        Text(
            stringResource(id = R.string.login_screen_info),
            style = TextStyle(fontSize = 24.sp),
            textAlign = TextAlign.Justify,
            color = Color(0xFF0B6380),
            modifier = Modifier.padding(16.dp)
        )

    }
}

@Preview
@Composable
private fun LogInContentPreview() {
    BikeRepairDocTheme {
        AuthenticatedContent(
            authEmail = "jinm8851@gmail.com",
            logOutClick = { },
            dataPutOnClick = { },
            dataPullOnClick = { },
            backWordOnClick = { })
    }
}