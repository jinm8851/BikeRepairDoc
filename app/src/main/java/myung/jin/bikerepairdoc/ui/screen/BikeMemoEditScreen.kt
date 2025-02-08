package myung.jin.bikerepairdoc.ui.screen


import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import myung.jin.bikerepairdoc.InventoryTopAppBar
import myung.jin.bikerepairdoc.ui.navigation.NavigationDestination.NavigationDestination
import myung.jin.bikerepairdoc.R
import myung.jin.bikerepairdoc.ui.AppViewModelProvider
import myung.jin.bikerepairdoc.ui.theme.shapes


/**
 * bikeMemoIdArg: const val은 컴파일 시점에 값이 결정되는 상수를 정의하는 데 사용됩니다.
 * bikeMemoIdArg는 자전거 정비 기록의 ID를 전달하기 위한 네비게이션 인수의 키를 나타냅니다.
 * 자전거 정비 기록 수정 화면으로 이동할 때 어떤 기록을 수정해야 하는지를 나타내는 ID를 전달할 때 사용합니다.
 *
 * val routeWithArgs = "$route/{$bikeMemoIdArg}":
 * routeWithArgs: 이 속성은 네비게이션 경로에 인수를 포함하는 문자열을 만듭니다.
 * $route는 앞서 정의한 기본 경로("bikeMemoEdit")를 삽입합니다.
 * {$bikeMemoIdArg}는 중괄호로 감싸진 부분이 네비게이션 인수를 나타내는 자리 표시자임을 의미합니다. 여기서는 bikeMemoIdArg에 해당하는 키로 인수를 받겠다는 뜻입니다.
 * 결과적으로, 자전거 정비 기록 수정 화면으로 이동할 때 특정 ID 값을 전달할 수 있는 동적인 경로가 생성됩니다 (예: "bikeMemoEdit/123").
 */
object BikeMemoEditDestination : NavigationDestination {
    override val route = "bikeMemoEdit"
    override val titleRes = R.string.editScreen
    const val bikeMemoIdArg = "bikeMemoId"
    val routeWithArgs = "$route/{$bikeMemoIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BikeMemoEditScreen(
    navigateBack: () -> Unit,
    bikeMemoId: Int,
    viewModel: BikeMemoEditViewModel = viewModel(factory = AppViewModelProvider.Factory),

    ) {
    val coroutineScope = rememberCoroutineScope()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    // 데이트레이지컬럼에서 불러온 bikeMemoId를 뷰모델 viewModel.getBikeMemo(bikeMemoId) 을
    // 호출해서 뷰모델에 bikeUiState 바이크메모를 넣어줌
    LaunchedEffect(key1 = bikeMemoId) {
        if (bikeMemoId != 0) {
            viewModel.getBikeMemo(bikeMemoId)
        } else {
            Log.d("BikeMemoEditScreen", "BikeMemoEditScreen: $bikeMemoId")
        }
    }
    Scaffold(
        topBar = {
            InventoryTopAppBar(
                title = stringResource(BikeMemoEditDestination.titleRes),
                canNavigateBack = true,
                canNavigateForward = false,
                scrollBehavior = scrollBehavior,
                onNavigateBack = { navigateBack() },
                onNavigateForward = { },
            )
        }
    ) { innerPadding ->
        EditBikeMemoForm(
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding()
                ),
            bikeDetails = viewModel.bikeUiState.bikeDetails, // 불러온 바이크메모를 bikeDetails에 넣어줌
            onValueChange = { viewModel.updateUiState(it) },//viewModel::updateUiState, 앞에거나 이거나 같은것
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.updateBikeMemo()
                    navigateBack()
                }
            },
            onDeleteBikeMemo = {
                coroutineScope.launch {
                    viewModel.deleteBikeMemo(bikeMemoId)
                    navigateBack()
                }
            },
        )
    }
}

@Composable
fun EditBikeMemoForm(
    modifier: Modifier,
    bikeDetails: BikeDetails,
    onValueChange: (BikeDetails) -> Unit,
    onSaveClick: () -> Unit,
    onDeleteBikeMemo: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color(0xFFFEF9CF))
            .padding(8.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),

        ) {
        Row(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 애칭
            OutlinedTextField(
                value = bikeDetails.nickName,
                onValueChange = { newNickName ->
                    onValueChange(bikeDetails.copy(nickName = newNickName))
                },
                label = { Text(text = stringResource(id = R.string.nickname)) },
                placeholder = { Text(text = stringResource(id = R.string.bike_model)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF703BE1),
                    unfocusedBorderColor = Color(0xFF703BE1),
                    focusedLabelColor = Color(0xFF703BE1),
                    unfocusedLabelColor = Color(0xFF703BE1),

                    ),
                modifier = Modifier
                    .weight(0.5f)
                    .background(color = Color(0x32B193E6)),
                textStyle = TextStyle(
                    fontSize = 20.sp,
                    color = Color(0xFF703BE1), textAlign = TextAlign.Center
                ),
                shape = shapes.small,
                singleLine = true,
            )

            Spacer(
                modifier = Modifier.size(16.dp)
            )
            // 구입 날짜
            OutlinedTextField(
                label = { Text(text = stringResource(id = R.string.purchase_date)) },
                placeholder = { Text(text = stringResource(id = R.string._2000_01_01)) },
                value = bikeDetails.startDate,
                onValueChange = {
                    onValueChange(bikeDetails.copy(startDate = it))
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF703BE1),
                    unfocusedBorderColor = Color(0xFF703BE1),
                    focusedLabelColor = Color(0xFF703BE1),
                    unfocusedLabelColor = Color(0xFF703BE1),
                ),
                modifier = Modifier
                    .weight(0.5f)
                    .background(color = Color(0x32B193E6)),
                textStyle = TextStyle(
                    fontSize = 20.sp,
                    color = Color(0xFF703BE1), textAlign = TextAlign.Center
                ),
                shape = shapes.small,
                singleLine = true,
            )
        }

        Row(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            //  수리 날짜
            OutlinedTextField(
                label = { Text(text = stringResource(id = R.string.repair_date)) },
                placeholder = { Text(text = stringResource(R.string.auto_insert)) },
                value = bikeDetails.repairDate,
                onValueChange = {
                    onValueChange(bikeDetails.copy(repairDate = it))
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF703BE1),
                    unfocusedBorderColor = Color(0xFF703BE1),
                    focusedLabelColor = Color(0xFF703BE1),
                    unfocusedLabelColor = Color(0xFF703BE1)
                ),
                modifier = Modifier
                    .weight(0.5f)
                    .background(color = Color(0x32B193E6)),
                textStyle = TextStyle(
                    fontSize = 20.sp,
                    color = Color(0xFF703BE1), textAlign = TextAlign.Center
                ),
                shape = shapes.small,
                singleLine = true,
            )
            Spacer(
                modifier = Modifier.size(16.dp),
            )
            // 스피너(드롭 다운 메뉴)
            MyDropdownMenu(
                modifier = Modifier
                    .weight(0.5f),
                onValueChange = {
                    onValueChange(bikeDetails.copy(selectedOption = it))
                }
            )
        }

        Row(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            //  주행 거리
            OutlinedTextField(
                label = { Text(text = stringResource(id = R.string.mileage)) },
                placeholder = { Text(text = stringResource(R.string.example_number)) },
                value = bikeDetails.km.toString()
                    .formatNumberWithCommas(), //bikeDetails.km.toString() //
                onValueChange = { newKmString ->
                    val newKm = if (newKmString.isEmpty()) {
                        0
                    } else {
                        newKmString.replace(",", "").toIntOrNull() ?: bikeDetails.km
                    }
                    onValueChange(bikeDetails.copy(km = newKm))
                },
                suffix = {
                    Text(
                        text = stringResource(id = R.string.km),
                        color = Color(0xFF703BE1)
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF703BE1),
                    unfocusedBorderColor = Color(0xFF703BE1),
                    focusedLabelColor = Color(0xFF703BE1),
                    unfocusedLabelColor = Color(0xFF703BE1),
                ),
                modifier = Modifier
                    .weight(0.5f)
                    .background(color = Color(0x32B193E6)),
                textStyle = TextStyle(
                    fontSize = 20.sp,
                    color = Color(0xFF703BE1), textAlign = TextAlign.Center
                ),
                shape = shapes.small,
                singleLine = true,
            )

            Spacer(modifier = Modifier.size(16.dp))
            //  금액
            OutlinedTextField(
                label = { Text(text = stringResource(id = R.string.amount)) },
                placeholder = { Text(text = stringResource(id = R.string.example_number)) },
                value = bikeDetails.amount.toString().formatNumberWithCommas(),
                onValueChange = { newAmountString ->
                    val newAmount = if (newAmountString.isEmpty()) {
                        0
                    } else {
                        newAmountString.replace(",", "").toIntOrNull() ?: bikeDetails.amount
                    }
                    onValueChange(bikeDetails.copy(amount = newAmount))
                },
                suffix = {
                    Text(
                        text = stringResource(id = R.string.currency_unit),
                        color = Color(0xFF703BE1)
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF703BE1),
                    unfocusedBorderColor = Color(0xFF703BE1),
                    focusedLabelColor = Color(0xFF703BE1),
                    unfocusedLabelColor = Color(0xFF703BE1),
                ),
                modifier = Modifier
                    .weight(0.5f)
                    .background(color = Color(0x32B193E6)),
                textStyle = TextStyle(
                    fontSize = 20.sp,
                    color = Color(0xFF703BE1), textAlign = TextAlign.Center
                ),
                shape = shapes.small,
                singleLine = true,
            )
        }

        Row(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            //  기타 내용
            OutlinedTextField(
                label = { Text(text = stringResource(id = R.string.other_details)) },
                placeholder = { Text(text = stringResource(R.string.enter_other_content)) },
                value = bikeDetails.etc,
                onValueChange = { onValueChange(bikeDetails.copy(etc = it)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF703BE1),
                    unfocusedBorderColor = Color(0xFF703BE1),
                    focusedLabelColor = Color(0xFF703BE1),
                    unfocusedLabelColor = Color(0xFF703BE1),
                ),
                modifier = Modifier
                    .weight(0.5f)
                    .background(color = Color(0x32B193E6)),
                textStyle = TextStyle(
                    fontSize = 20.sp,
                    color = Color(0xFF703BE1), textAlign = TextAlign.Center
                ),
                shape = shapes.small,
                singleLine = true,
            )
        }

        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = { onSaveClick() },
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0x32B193E6),// 텍스트 색상
                disabledContentColor = Color(0xFF703BE1),// 비활성화상태에서 텍스트색상
                containerColor = Color(0x32B193E6),// 버튼배경색상
                disabledContainerColor = Color(0xFF703BE1) // 비 활성화상태에서 배경색상
            ),
            border = BorderStroke(1.dp, Color(0xFF703BE1)),
        ) {
            Text(
                text = stringResource(id = R.string.edit),
                style = TextStyle(fontSize = 25.sp),
                color = Color(0xFF703BE1)
            )
        }

        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = { onDeleteBikeMemo() },
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0x32B193E6),// 텍스트 색상
                disabledContentColor = Color(0xFF703BE1),// 비활성화상태에서 텍스트색상
                containerColor = Color(0x32B193E6),// 버튼배경색상
                disabledContainerColor = Color(0xFF703BE1) // 비 활성화상태에서 배경색상
            ),
            border = BorderStroke(1.dp, Color(0xFF703BE1)),
        ) {
            Text(
                text = stringResource(id = R.string.삭제),
                style = TextStyle(fontSize = 25.sp),
                color = Color(0xFF703BE1)
            )
        }
    }
}

@Preview
@Composable
private fun EditBikeMemoFormPreview() {
    EditBikeMemoForm(
        bikeDetails = BikeDetails(
            1, "버그만", "2000-01-01", "2000-01-01",
            10000, "선택", 100000, " ", "2000-10-10"
        ),
        onValueChange = {},
        onSaveClick = {},
        modifier = Modifier,
        onDeleteBikeMemo = {},
    )
}



