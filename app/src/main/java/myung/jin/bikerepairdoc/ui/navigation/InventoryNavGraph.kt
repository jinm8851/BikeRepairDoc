package myung.jin.bikerepairdoc.ui.navigation


import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import myung.jin.bikerepairdoc.CashbookPagerScreen
import myung.jin.bikerepairdoc.RepairPagerScreen
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

object PagerDestinations{
    const val REPAIR_PAGER = "repair_pager"
    const val CASHBOOK_PAGER = "cashbook_pager"
}

@Composable
fun InventoryNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
 //   Log.d("InventoryNavHost", "InventoryNavHost 함수 시작")

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
                    navController.navigate(PagerDestinations.REPAIR_PAGER)
                },
                onNavigateToCashbook = {
                    navController.navigate(PagerDestinations.CASHBOOK_PAGER)
                }
            )
         //   Log.d("InventoryNavHost", "StartDestination composable")
        }

        // 수리 내역 페이저 흐름
        composable(route = PagerDestinations.REPAIR_PAGER) {
            RepairPagerScreen(navController = navController)
        }

        // 금전출납부 페이저 흐름
        composable(route = PagerDestinations.CASHBOOK_PAGER) {
            CashbookPagerScreen(navController = navController)
        }

        composable(
            route = BikeMemoEditDestination.routeWithArgs,
            arguments = listOf(navArgument(BikeMemoEditDestination.bikeMemoIdArg) {
                type = NavType.IntType
            })
        ) {
          //  Log.d("InventoryNavHost", "BikeMemoEditDestination composable")
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
          //  Log.d("InventoryNavHost", "AuthDetailScreenDestination composable")
            AuthDetailScreen(
                navHostController = navController,
            )
        }
    }
}

