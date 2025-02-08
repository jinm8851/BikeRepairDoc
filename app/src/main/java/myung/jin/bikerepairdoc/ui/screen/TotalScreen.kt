package myung.jin.bikerepairdoc.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import myung.jin.bikerepairdoc.InventoryTopAppBar
import myung.jin.bikerepairdoc.ui.AppViewModelProvider
import myung.jin.bikerepairdoc.ui.navigation.NavigationDestination.NavigationDestination
import myung.jin.bikerepairdoc.R
import myung.jin.bikerepairdoc.ui.room.BikeMemo


object TotalScreenDestination : NavigationDestination {
    override val route = "total"
    override val titleRes = R.string.totalScreen
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TotalScreen(
    modifier: Modifier = Modifier,
    navigateToUpdate: (Int) -> Unit,
    viewModel: TotalScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
    pagerState: PagerState
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior() // 탑앱바 스크롤
    val totalUiState by viewModel.totalUiState.collectAsState() // 바이크메모 리스트
    var search by remember { mutableStateOf("") } // 검색
    val focusManager =
        LocalFocusManager.current // 버튼을 클릭하면 키보드가 숨겨지고, 텍스트 필드와 같은 UI 요소에서 포커스가 해제됩니다.
    val coroutineScope = rememberCoroutineScope()

    // 검색어를 기반으로 필터링된 리스트 생성
    //contains() 함수는 문자열이 특정 문자열을 포함하는지 확인하는 함수입니다.
    //ignoreCase = true 옵션을 사용하면 대소문자를 구분하지 않고 검색합니다.
    val filteredBikeList = totalUiState.bikeList.filter { bikeMemo ->
        bikeMemo.model.contains(search, ignoreCase = true) ||
                bikeMemo.date.contains(search, ignoreCase = true) ||
                bikeMemo.year.contains(search, ignoreCase = true) ||
                bikeMemo.refer.contains(search, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            InventoryTopAppBar(
                title = stringResource(TotalScreenDestination.titleRes),
                canNavigateBack = true,
                canNavigateForward = true,
                scrollBehavior = scrollBehavior,
                onNavigateBack = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                },
                onNavigateForward = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                },
            )
        },
    ) { innerPadding ->
        TotalScreenColum(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
            filteredBikeList = filteredBikeList,
            search = search,
            onValueChange = { search = it },
            onclick = { focusManager.clearFocus() },
            onItemClick = { bikeMemoId -> navigateToUpdate(bikeMemoId) },
            onDeleteBikeMemo = { bikeMemoId ->
                coroutineScope.launch {
                    viewModel.deleteBikeMemo(bikeMemoId)
                }
            },
        )
    }
}

@Composable
fun TotalScreenColum(
    modifier: Modifier = Modifier,
    filteredBikeList: List<BikeMemo>,
    search: String,
    onValueChange: (String) -> Unit,
    onclick: () -> Unit,
    onItemClick: (Int) -> Unit,
    onDeleteBikeMemo: (Int) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFEF9CF))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        DisplayInfoText(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally),
            stringResId = stringResource(id = R.string.search_hint),
            textAlign = TextAlign.Center,
            fontSize = 24
        )
        Row(
            modifier = Modifier
                .padding(8.dp),
            //   .height(60.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(0.7f),
                value = search,
                onValueChange = onValueChange,
                label = { Text(text = stringResource(id = R.string.search)) },
                placeholder = { Text(text = stringResource(id = R.string.search_description)) },
                keyboardOptions = KeyboardOptions.Default,
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    color = Color(0xFF703BE1),
                    fontSize = 24.sp
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF703BE1),
                    unfocusedBorderColor = Color(0xFF703BE1),
                    focusedLabelColor = Color(0xFF703BE1),
                    unfocusedLabelColor = Color(0xFF703BE1),

                    ),
            )
            SaveButton(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(0.3f),
                stringResId = R.string.search,
                onClick = onclick
            )
        }


        BikeMemoList(
            modifier = Modifier
                .weight(1f),
            bikeMemoList = filteredBikeList,
            route = TotalScreenDestination.route,
            onDeleteBikeMemo = onDeleteBikeMemo,
            onItemClick = onItemClick,
        )

        DisplayInfoText(
            modifier = Modifier.padding(16.dp),
            stringResId = stringResource(
                id = R.string.total_amount,
                filteredBikeList.sumOf { it.amount }.toString().formatNumberWithCommas()
            ),
            textAlign = TextAlign.Center,
            fontSize = 30
        )
    }
}

