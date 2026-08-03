package myung.jin.bikerepairdoc


import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import myung.jin.bikerepairdoc.ui.AppViewModelProvider
import myung.jin.bikerepairdoc.ui.navigation.InventoryNavHost
import myung.jin.bikerepairdoc.ui.screen.BikeMemoEditDestination
import myung.jin.bikerepairdoc.ui.screen.BikeMemoEditViewModel
import myung.jin.bikerepairdoc.ui.screen.MainScreen
import myung.jin.bikerepairdoc.ui.screen.TotalScreen
import myung.jin.bikerepairdoc.ui.screen.authScreen.AuthScreen


object InventoryScreen {
    const val HOME = 0
    const val TOTAL = 1
    const val AUTH = 2
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryApp(
    modifier: Modifier = Modifier,
    bikeMemoEditViewModel: BikeMemoEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val navController: NavHostController = rememberNavController()
    val pagerState = rememberPagerState(
        initialPage = InventoryScreen.HOME,
        pageCount = { 3 }) // 람다 함수를 사용하여 페이지 수 반환

    Box(modifier = modifier.fillMaxSize()) {
        // 메인 화면용 HorizontalPager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = true
        ) { page ->
            when (page) {
                InventoryScreen.HOME -> MainScreen(
                    navigateToUpdate = { bikeMemoId ->
                        Log.d("InventoryApp", "${BikeMemoEditDestination.route}/$bikeMemoId 으로 이동")
                        bikeMemoEditViewModel.getBikeMemo(bikeMemoId)
                        navController.navigate("${BikeMemoEditDestination.route}/$bikeMemoId")
                    },
                    pagerState = pagerState,
                    navController = navController
                )

                InventoryScreen.TOTAL -> TotalScreen(
                    navigateToUpdate = { bikeMemoId ->
                        bikeMemoEditViewModel.getBikeMemo(bikeMemoId)
                        navController.navigate("${BikeMemoEditDestination.route}/$bikeMemoId")
                    },
                    pagerState = pagerState,
                    navController = navController
                )

                InventoryScreen.AUTH -> AuthScreen(
                    navController = navController,
                    pagerState = pagerState
                )
            }
        }

        // 세부 화면용 NavHost
        InventoryNavHost(
            navController = navController,
            modifier = Modifier.fillMaxSize(),

            )
    }
}

/**
 * 인벤토리 앱을 위한 맞춤형 상단 앱 바.
 *
 * 이 컴포지트 파일은 제목을 표시하고 앞뒤 내비게이션 아이콘을 조건부로 표시합니다.
 *
 * @param title 상단 앱 바에 표시될 제목.
 * @param canNavigateBack Boolean은 뒤로 이동 아이콘을 표시해야 하는지 여부를 나타냅니다.
 * @param canNavigateForward Boolean은 순방향 탐색 아이콘을 표시해야 하는지 여부를 나타냅니다.
 * @param onNavigateForward 다음화면으로 이동
 * @param scrollBehavior 옵션으로 상단 앱 바를 스크롤할 수 있습니다.
 * @param onNavigateBack 이전화면으로 이동
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    canNavigateForward: Boolean,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavigateBack: () -> Unit,
    onNavigateForward: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    // 탑앱바 컬러적용
    val topAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = colorScheme.primaryContainer,
        scrolledContainerColor = colorScheme.surface,
        navigationIconContentColor = colorScheme.onPrimaryContainer,
        titleContentColor = colorScheme.onPrimaryContainer,
        actionIconContentColor = colorScheme.onPrimaryContainer
    )

    //CenterAlignedTopAppBar는 제목을 가운데에 정렬하고 탐색 아이콘과 작업 아이콘을 양쪽에 배치하는 데 사용됩니다.
    //navigationIcon 매개변수는 왼쪽에 표시될 탐색 아이콘을 지정합니다.
    //actions 매개변수는 오른쪽에 표시될 작업 아이콘 목록을 지정합니다.
    //아이콘을 변경하려면 imageVector 속성을 다른 아이콘으로 변경하면 됩니다. 이렇게 수정하면 가운데는 글자가 들어가고 양 옆으로 아이콘이 들어가는 TopAppBar가 생성됩니다. 😊
    CenterAlignedTopAppBar(
        title = { Text(text = title) },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        colors = topAppBarColors,
        navigationIcon = {
            if (canNavigateBack) {
                NavigationIcon(
                    onNavigateBack = onNavigateBack,
                )
            }
        },
        actions = {
            if (canNavigateForward) {
                ForwardActonIcon(
                    onNavigateForward = onNavigateForward,
                )
            }
        }
    )
}

@Composable
fun NavigationIcon(
    onNavigateBack: () -> Unit,
) {
    IconButton(
        onClick = {
            onNavigateBack()
            /*val destination =
                when (currentRoute) {
                    TotalScreenDestination.route -> HomeDestination.route
                    BikeMemoEditDestination.route -> TotalScreenDestination.route
                    AuthDestination.route -> TotalScreenDestination.route
                    AuthDetailScreenDestination.route -> AuthDestination.route
                    else -> null
                }
            //destination이 null이 아닐 때만 뒤에 오는 코드를 실행하고, null이면 아무것도 실행하지 않고 넘어갑니다.
            destination?.let {  //예상치 못한 경로에 대한 null 검사를 추가했습니다.
                navController.navigate(it) // it에 해당하는 경로로 이동하라는 명령입니다. 즉, destination이 null이 아니라면, destination에 설정된 경로로 화면을 이동합니다
            }*/
        }
    ) {
        Icon(
            painter = painterResource(R.drawable.outline_arrow_back_24),
            contentDescription = stringResource(id = R.string.back_button)
        )
    }
}

@Composable
fun ForwardActonIcon(
    onNavigateForward: () -> Unit,
) {
    IconButton(
        onClick = {
            onNavigateForward()
        }
    ) {
        Icon(
            painter = painterResource(R.drawable.outline_arrow_forward_24),
            contentDescription = stringResource(id = R.string.forward_button)
        )

    }

}

