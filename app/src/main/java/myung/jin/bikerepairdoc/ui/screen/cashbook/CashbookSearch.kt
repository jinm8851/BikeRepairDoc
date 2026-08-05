package myung.jin.bikerepairdoc.ui.screen.cashbook

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import myung.jin.bikerepairdoc.InventoryTopAppBar
import myung.jin.bikerepairdoc.R
import myung.jin.bikerepairdoc.ui.AppViewModelProvider
import myung.jin.bikerepairdoc.ui.navigation.NavigationDestination
import myung.jin.bikerepairdoc.ui.room.CashBook
import myung.jin.bikerepairdoc.ui.screen.CashBookDetail
import myung.jin.bikerepairdoc.ui.screen.DisplayInfoText
import myung.jin.bikerepairdoc.ui.screen.GenericDateList
import myung.jin.bikerepairdoc.ui.screen.formatNumberWithCommas

object CashbookSearchDestination : NavigationDestination {
    override val route = "cashbook_search"
    override val titleRes = R.string.cashbook_search_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashbookSearch(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    viewModel: CashbookSearchViewmodel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredList by viewModel.filteredCashBookList.collectAsState()
    val totalAmount by viewModel.filteredTotalAmount.collectAsState()

    Scaffold(
        topBar = {
            InventoryTopAppBar(
                title = stringResource(CashbookSearchDestination.titleRes),
                canNavigateBack = true,
                canNavigateForward = false,
                onNavigateBack = { navHostController.popBackStack() },
                onNavigateForward = { }
            )
        }
    ) { innerPadding ->
        CashbookSearchScreen(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            searchQuery = searchQuery,
            cashBookList = filteredList,
            totalAmount = totalAmount,
            onSearchQueryChange = { viewModel.updateSearchQuery(it) },
            onDeleteClick = { viewModel.deleteCashBook(it) }
        )
    }
}

@Composable
fun CashbookSearchScreen(
    modifier: Modifier,
    searchQuery: String,
    cashBookList: List<CashBook>,
    totalAmount: Long,
    onSearchQueryChange: (String) -> Unit,
    onDeleteClick: (Long) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text(stringResource(R.string.search)) },
            placeholder = { Text(stringResource(R.string.search_hint_cashbook)) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = colorScheme.surface,
                unfocusedContainerColor = colorScheme.surface,
                focusedBorderColor = colorScheme.primary,
                unfocusedBorderColor = colorScheme.outline,
            )
        )

        // 레이지컬럼 (남은 공간 모두 차지)
        GenericDateList(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 16.dp),
            itemList = cashBookList,
            getDate = { it.date }
        ) { cashBook, color ->
            CashBookDetail(
                cashBook = cashBook,
                route = CashbookSearchDestination.route,
                onDeleteClick = { onDeleteClick(cashBook.id) },
                backgroundColor = color
            )
        }

        //합계 (항상 하단에 고정)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = colorScheme.surfaceContainer,
            tonalElevation = 4.dp
        ) {
            DisplayInfoText(
                modifier = Modifier.padding(16.dp),
                stringResId = stringResource(
                    id = R.string.total_amount,
                    totalAmount.toString().formatNumberWithCommas()
                ),
                textAlign = TextAlign.Center,
                fontSize = 24,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
