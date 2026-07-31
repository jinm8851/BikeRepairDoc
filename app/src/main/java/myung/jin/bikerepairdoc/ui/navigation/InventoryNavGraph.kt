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
import myung.jin.bikerepairdoc.ui.screen.cashbook.CashbookScreen
import myung.jin.bikerepairdoc.ui.screen.cashbook.CashbookDestination
import myung.jin.bikerepairdoc.ui.screen.cashbook.CashbookSearch
import myung.jin.bikerepairdoc.ui.screen.cashbook.CashbookSearchDestination


import myung.jin.bikerepairdoc.ui.screen.StartDestination
import myung.jin.bikerepairdoc.ui.screen.StartScreen

@Composable
fun InventoryNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    Log.d("InventoryNavHost", "InventoryNavHost 함수 시작")

    NavHost(
        navController = navController,
        startDestination = StartDestination.route,
        modifier = modifier
    ) {
        composable(
            route = StartDestination.route
        ) {
            StartScreen(
                onNavigateToMain = {
                    navController.navigate(HomeDestination.route)
                },
                onNavigateToCashbook = {
                    navController.navigate(CashbookDestination.route)
                }
            )
            Log.d("InventoryNavHost", "StartDestination composable")
        }

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
        composable(
            route = CashbookDestination.route
        ){
            Log.d("InventoryNavHost","CashBookScreenDestination composable")
            CashbookScreen(
                navHostController = navController
            )
        }
        composable(
            route = CashbookSearchDestination.route
        ){
            Log.d("InventoryNavHost","CashBookSearchScreenDestination composable")
            CashbookSearch(
                navHostController = navController
            )
        }
    }
}

