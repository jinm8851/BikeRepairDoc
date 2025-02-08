package myung.jin.bikerepairdoc.ui.screen.authScreen


import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import myung.jin.bikerepairdoc.InventoryTopAppBar
import myung.jin.bikerepairdoc.ui.navigation.NavigationDestination.NavigationDestination
import myung.jin.bikerepairdoc.R

object AuthScreenDestination : NavigationDestination {
    override val route: String = "Auth"
    override val titleRes: Int = R.string.authScreen
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    navController: NavHostController,
) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier,
        topBar = {
            InventoryTopAppBar(
                title = stringResource(AuthScreenDestination.titleRes),
                canNavigateBack = true,
                canNavigateForward = false,
                modifier = modifier,
                scrollBehavior = scrollBehavior,
                onNavigateBack = {
                    coroutineScope.launch {
                        if (pagerState.currentPage > 0) {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }
                },
                onNavigateForward = { },
            )
        }
    ) { innerPadding ->
        AuthScreenContent(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(color = Color(0xffFEF9CF)),
            navController = navController,

            )
    }
}

@Composable
fun AuthScreenContent(
    modifier: Modifier,
    navController: NavHostController,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        Text(
            text = stringResource(R.string.authChange),
            modifier = Modifier.background(color = Color(0xffFEF9CF)),
            style = TextStyle(
                fontSize = 25.sp,
                textAlign = TextAlign.Center,
                color = Color(0xFF0B6380),
            )
        )

        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onClick = {
                Log.d("AuthScreenContent", "OutlinedButton 클릭됨") // 로그 추가
                navController.navigate(AuthDetailScreenDestination.route)
            },
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0x32B193E6),// 텍스트 색상
                disabledContentColor = Color(0xFF703BE1),// 비활성화상태에서 텍스트색상
                containerColor = Color(0x32B193E6),// 버튼배경색상
                disabledContainerColor = Color(0xFF703BE1) // 비 활성화상태에서 배경색상
            ),
            border = BorderStroke(1.dp, Color(0xFF703BE1)),
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = Color(0xFF703BE1),
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 18.dp)
            )
            Text(
                text = stringResource(id = R.string.tran),
                color = Color(0xFF703BE1),
                style = TextStyle(
                    fontSize = 25.sp,
                ),
            )
        }
        Text(
            text = stringResource(R.string.login_info),
            modifier = Modifier.background(color = Color(0xffFEF9CF)),
            style = TextStyle(
                fontSize = 25.sp,
                textAlign = TextAlign.Justify,
                color = Color(0xFF0B6380),
                /*   lineHeightStyle = LineHeightStyle(
                       alignment = LineHeightStyle.Alignment.Center,  // 텍스트 라인정렬
                       trim = LineHeightStyle.Trim.None  // 공백 제거 안 함
                   ),
                   lineHeight = 30.sp, // 텍스트 라인높이*/
            )
        )
    }
}
