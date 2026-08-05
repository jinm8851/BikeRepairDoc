package myung.jin.bikerepairdoc

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import myung.jin.bikerepairdoc.ui.theme.BikeRepairDocTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 전체 화면 모드 활성화 (Android 15 필수 사항)
        enableEdgeToEdge()
        setContent {
            BikeRepairDocTheme {
                // 루트 수준의 Scaffold와 패딩을 제거하여 
                // 시스템 바 영역까지 앱이 그려지도록 합니다.
                InventoryApp()
            }
        }
    }
}
