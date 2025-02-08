package myung.jin.bikerepairdoc.ui.navigation


import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import myung.jin.bikerepairdoc.ui.screen.BikeMemoEditDestination
import myung.jin.bikerepairdoc.ui.screen.BikeMemoEditScreen
import myung.jin.bikerepairdoc.ui.screen.HomeDestination
import myung.jin.bikerepairdoc.ui.screen.TotalScreenDestination
import myung.jin.bikerepairdoc.ui.screen.authScreen.AuthDetailScreen
import myung.jin.bikerepairdoc.ui.screen.authScreen.AuthDetailScreenDestination
import myung.jin.bikerepairdoc.ui.screen.authScreen.AuthScreenDestination


@Composable
fun InventoryNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    Log.d("InventoryNavHost", "InventoryNavHost 함수 시작")

    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,//"main_flow", // 임의의 시작 지점 설정
        modifier = modifier
    ) {
        composable(
            route = HomeDestination.route,
        ) {
            Log.d("InventoryNavHost", "HomeDestination composable")
        }

        composable(
            route = TotalScreenDestination.route
        ) {
            Log.d("InventoryNavHost", "TotalScreenDestination composable")
        }

        composable(
            route = AuthScreenDestination.route
        ) {
            Log.d("InventoryNavHost", "AuthScreenDestination composable")
        }


        composable(
            route = BikeMemoEditDestination.routeWithArgs,
            arguments = listOf(navArgument(BikeMemoEditDestination.bikeMemoIdArg) {
                type = NavType.IntType
            })
        ) {
            Log.d("InventoryNavHost", "BikeMemoEditDestination composable")
            val bikeMemoId = it.arguments?.getInt(BikeMemoEditDestination.bikeMemoIdArg) ?: 0
            BikeMemoEditScreen(
                bikeMemoId = bikeMemoId,
                navigateBack = {
                    navController.popBackStack()
                },
            )
        }
        composable(
            route = AuthDetailScreenDestination.route
        ) {
            Log.d("InventoryNavHost", "AuthDetailScreenDestination composable")
            AuthDetailScreen(
                navHostController = navController,
            )
        }
    }
}

