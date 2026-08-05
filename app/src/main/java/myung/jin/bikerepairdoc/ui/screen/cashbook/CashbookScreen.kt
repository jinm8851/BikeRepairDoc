package myung.jin.bikerepairdoc.ui.screen.cashbook

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import myung.jin.bikerepairdoc.InventoryTopAppBar
import myung.jin.bikerepairdoc.R
import myung.jin.bikerepairdoc.ui.AppViewModelProvider
import myung.jin.bikerepairdoc.ui.components.CashDropDownMenu
import myung.jin.bikerepairdoc.ui.components.DatePickerField
import myung.jin.bikerepairdoc.ui.navigation.NavigationDestination
import myung.jin.bikerepairdoc.ui.room.CashBook
import myung.jin.bikerepairdoc.ui.screen.CashBookDetail
import myung.jin.bikerepairdoc.ui.screen.DisplayInfoText
import myung.jin.bikerepairdoc.ui.screen.GenericDateList
import myung.jin.bikerepairdoc.ui.screen.StartDestination
import myung.jin.bikerepairdoc.ui.screen.formatNumberWithCommas
import myung.jin.bikerepairdoc.ui.theme.shapes

object CashbookDestination : NavigationDestination {
    override val route = "cashbook"
    override val titleRes = R.string.cashbookScreen
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashbookScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    viewModel: CashbookViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val uiState by viewModel.uiState.collectAsState()
    val cashBookList by viewModel.cashBookList.collectAsState()
    val totalAmount by viewModel.totalAmount.collectAsState()

    Scaffold(
        modifier = Modifier,
        topBar = {
            InventoryTopAppBar(
                title = stringResource(CashbookDestination.titleRes),
                canNavigateBack = true,
                canNavigateForward = true,
                onNavigateBack = {
                    navHostController.popBackStack(StartDestination.route, inclusive = false)
                },
                onNavigateForward = {
                    navHostController.navigate(CashbookSearchDestination.route)
                },
            )
        }
    ) { innerPadding ->
        CashbookScreenContent(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            uiState = uiState,
            cashBookList = cashBookList,
            totalAmount = totalAmount,
            onValueChange = { viewModel.updateSelectedText(it) },
            onSaveClick = { viewModel.saveCashBook() },
            onDeleteClick = { viewModel.deleteCashBook(it) },
            viewModel = viewModel
        )

    }
}

@Composable
fun CashbookScreenContent(
    modifier: Modifier = Modifier,
    uiState: CashbookUiState,
    cashBookList: List<CashBook>,
    totalAmount: Long,
    onValueChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: (Long) -> Unit,
    viewModel: CashbookViewModel
) {

    var newContentInput by remember { mutableStateOf("") }
    val names by viewModel.contentNames.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상단 입력 영역 (스크롤 가능)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false) // 입력 영역이 너무 커지면 목록을 위해 공간을 양보하도록 설정
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //날짜
            DatePickerField(
                label = { Text(stringResource(R.string.current_date)) },
                selectedDate = uiState.cashBookDetails.date,
                onDateSelected = { viewModel.updateDate(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(56.dp)
                    .padding(vertical = 4.dp)
            )

            // 내용 입력 창
            OutlinedTextField(
                value = newContentInput,
                onValueChange = { newContentInput = it },
                label = { Text(stringResource(R.string.inputContent)) },
                placeholder = { Text(stringResource(R.string.inputText)) },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            viewModel.addContentName(newContentInput)
                            newContentInput = ""
                        },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_add_box_24),
                            contentDescription = null
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = colorScheme.surface,
                    unfocusedContainerColor = colorScheme.surface,
                    focusedBorderColor = colorScheme.primary,
                    unfocusedBorderColor = colorScheme.outline,
                ),
                textStyle = TextStyle(
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = shapes.small,
                singleLine = true
            )

            // 드롭다운 메뉴
            CashDropDownMenu(
                modifier = Modifier
                    .heightIn(min = 56.dp)
                    .padding(vertical = 4.dp),
                selectedOption = uiState.selectedText, // 현재 선택된 텍스트
                onValueChange = onValueChange,
                onDeleteName = { nameDelete -> viewModel.deleteContentName(nameDelete) },
                selectedNames = names // 저장되있는 컨텐츠 목록
            )

            // 수입 금액
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.income),
                    modifier = Modifier.weight(0.3f),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = colorScheme.primary
                )

                OutlinedTextField(
                    value = if (uiState.cashBookDetails.income == 0L) "" else uiState.cashBookDetails.income.toString(),
                    onValueChange = { viewModel.updateIncome(it) },
                    label = { Text(stringResource(R.string.income), color = colorScheme.primary) },
                    placeholder = {
                        Text(
                            stringResource(R.string.inputAmount),
                            color = colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = onSaveClick,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.outline_add_box_24),
                                contentDescription = null,
                                tint = colorScheme.primary
                            )
                        }
                    },
                    textStyle = TextStyle(
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        color = colorScheme.primary
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colorScheme.surface,
                        unfocusedContainerColor = colorScheme.surface,
                        focusedBorderColor = colorScheme.primary,
                        unfocusedBorderColor = colorScheme.outline,
                        focusedLabelColor = colorScheme.primary,
                        unfocusedLabelColor = colorScheme.onSurfaceVariant,
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.7f),
                    shape = shapes.small,
                    singleLine = true
                )
            }

            // 지출금액
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.expenditure),
                    modifier = Modifier.weight(0.3f),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = colorScheme.error
                )

                OutlinedTextField(
                    value = if (uiState.cashBookDetails.expense == 0L) "" else uiState.cashBookDetails.expense.toString(),
                    onValueChange = { viewModel.updateExpense(it) },
                    label = {
                        Text(
                            stringResource(R.string.expenditure),
                            color = colorScheme.error
                        )
                    },
                    placeholder = {
                        Text(
                            stringResource(R.string.inputAmount),
                            color = colorScheme.error
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = onSaveClick,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.outline_add_box_24),
                                contentDescription = null,
                                tint = colorScheme.error
                            )
                        }
                    },
                    textStyle = TextStyle(
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        color = colorScheme.error
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colorScheme.surface,
                        unfocusedContainerColor = colorScheme.surface,
                        focusedBorderColor = colorScheme.error,
                        unfocusedBorderColor = colorScheme.outline,
                        focusedLabelColor = colorScheme.error,
                        unfocusedLabelColor = colorScheme.onSurfaceVariant,
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.7f),
                    shape = shapes.small,
                    singleLine = true
                )
            }
        }

        // 레이지컬럼 (남은 공간 모두 차지)
        GenericDateList(
            modifier = Modifier.weight(0.5f),
            itemList = cashBookList,
            getDate = { it.date }
        ) { cashBook, color ->
            CashBookDetail(
                cashBook = cashBook,
                route = CashbookDestination.route,
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

