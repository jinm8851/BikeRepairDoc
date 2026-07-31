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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import myung.jin.bikerepairdoc.InventoryTopAppBar
import myung.jin.bikerepairdoc.R
import myung.jin.bikerepairdoc.ui.navigation.NavigationDestination
import myung.jin.bikerepairdoc.ui.screen.StartDestination

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
                canNavigateForward = true,
                modifier = modifier,
                scrollBehavior = scrollBehavior,
                onNavigateBack = {
                    coroutineScope.launch {
                        if (pagerState.currentPage > 0) {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        } else {
                            navController.popBackStack(StartDestination.route, inclusive = false)
                        }
                    }
                },
                onNavigateForward = {
                    navController.popBackStack(StartDestination.route, inclusive = false)
                },
            )
        }
    ) { innerPadding ->
        AuthScreenContent(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
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
            modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
            style = MaterialTheme.typography.titleLarge.copy(
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
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
                contentColor = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 18.dp)
            )
            Text(
                text = stringResource(id = R.string.tran),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge
            )
        }
        Text(
            text = stringResource(R.string.login_info),
            modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
            style = MaterialTheme.typography.bodyLarge.copy(
                textAlign = TextAlign.Justify,
                color = MaterialTheme.colorScheme.onSurface,
            )
        )
    }
}
