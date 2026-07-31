package myung.jin.bikerepairdoc.ui.screen.authScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 5.dp
        )
        // 추가적으로 로딩 메시지를 표시하고 싶을 경우 사용
        // Text(text = "Loading...",style = TextStyle(fontSize = 25.sp, color = Color(0xFF703BE1)), modifier = Modifier.padding(top = 50.dp) )
    }
}
