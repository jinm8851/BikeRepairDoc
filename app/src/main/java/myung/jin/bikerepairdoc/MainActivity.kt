package myung.jin.bikerepairdoc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import myung.jin.bikerepairdoc.ui.theme.BikeRepairDocTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            BikeRepairDocTheme {
                InventoryApp()
            }
        }
    }
}