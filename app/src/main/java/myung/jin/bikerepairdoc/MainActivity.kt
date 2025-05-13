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
        enableEdgeToEdge()
        setContent {

            BikeRepairDocTheme {
                // 속성을 false로 설정하면 사실상 3버튼 탐색 배경을 투명으로 설정하는 것입니다.
                // 3버튼 탐색에만 영향을 미치며 동작 탐색에는 영향을 미치지 않습니다.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    window.isNavigationBarContrastEnforced = false
                }
                Scaffold(Modifier.windowInsetsPadding(WindowInsets.safeDrawing)) { innerPadding ->

                    InventoryApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}