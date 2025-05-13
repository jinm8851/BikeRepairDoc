package myung.jin.bikerepairdoc.ui.screen


import java.text.DecimalFormat
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import myung.jin.bikerepairdoc.InventoryTopAppBar
import myung.jin.bikerepairdoc.ui.theme.BikeRepairDocTheme
import myung.jin.bikerepairdoc.R
import myung.jin.bikerepairdoc.ui.AppViewModelProvider
import myung.jin.bikerepairdoc.ui.navigation.NavigationDestination.NavigationDestination
import myung.jin.bikerepairdoc.ui.room.BikeMemo
import myung.jin.bikerepairdoc.ui.theme.shapes



object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.mainScreen
}


// 개선된 천 단위 구분자 변환 함수
// VisualTransformation은 텍스트를 시각적으로 변환하는 데 사용되며, 실제 데이터는 변경하지 않고 화면에 표시되는 방식만 바꿉니다.
fun thousandSeparatorTransformation(): VisualTransformation {
    return VisualTransformation { text ->  // text는 TransformedText 타입으로, 입력된 텍스트에 대한 정보를 담고 있습니다.
        // 입력된 텍스트를 String 타입으로 가져옵니다.
        val originalText = text.text
        val intValue = originalText.toIntOrNull()

        // 입력된 텍스트를 정수로 변환하고, 콤마를 추가한 후 다시 문자열로 변환합니다.
        val formattedText = intValue?.let {
            DecimalFormat("#,###").format(it)
        } ?: originalText

        // OffsetMapping: 텍스트 변환 후 커서 위치를 올바르게 조정하는 데 필요한 인터페이스입니다.
        val offsetMapping = object : OffsetMapping{
            // originalToTransformed(offset: Int): 원래 텍스트에서의 커서 위치 (offset)를 변환된 텍스트에서의 위치로 매핑합니다.
            override fun originalToTransformed(offset: Int): Int {
                //offset < 0인 경우, 0을 반환하도록 하여 예외를 방지하고
                if (intValue == null || offset < 0) return 0
                // 입력된 offset까지의 원래 숫자를 추출합니다.
                val originalNumber = originalText.substring(0, offset).toIntOrNull() ?: return formattedText.length
                // 추출된 숫자를 DecimalFormat을 사용하여 포맷합니다.
                val formattedNumber = DecimalFormat("#,###").format(originalNumber)
                // 포맷된 숫자의 길이를 반환합니다. 이는 원래 offset이 변환된 텍스트에서 어디에 매핑되는지 정확하게 알려줍니다.
                return formattedNumber.length
            }
            //transformedToOriginal(offset: Int): 변환된 텍스트에서의 커서 위치 (offset)를 원래 텍스트에서의 위치로 매핑합니다.
            override fun transformedToOriginal(offset: Int): Int {
                if (intValue == null || offset < 0) return 0

                var formattedOffset = 0 // 변환된 텍스트에서의 현재 커서 위치
                var originalOffset = 0  // 변환된 텍스트에서의 현재 커서 위치
                // 변환된 텍스트의 각 문자(char)와 인덱스(index)를 순회합니다.
                formattedText.forEachIndexed { index, char ->
                    // 변환된 텍스트에서의 커서 위치(formattedOffset)가 입력된 offset(변환된 텍스트에서의 목표 커서 위치)보다 크거나 같으면 목표 위치에 도달했다는 의미입니다.
                    if (formattedOffset >= offset) {
                        return originalOffset  // 원래 텍스트에서의 커서 위치(originalOffset)를 반환합니다.
                    }
                    // 변환된 텍스트에서의 커서 위치를 한 칸 이동합니다.
                    formattedOffset++
                    // 현재 문자가 숫자이면, 원래 텍스트에서의 커서 위치도 한 칸 이동합니다.
                    // 쉼표와 같은 구분자는 원래 텍스트에 없으므로, 숫자인 경우에만 originalOffset을 증가시킵니다.
                    if (char.isDigit()) {
                        originalOffset++
                    }
                }
                // 순회를 마쳤지만 목표 offset에 도달하지 못한 경우, 현재 originalOffset을 반환합니다.
                // 이 경우는 offset이 formattedText의 길이보다 큰 경우에 발생합니다.
                return originalOffset

            }
        }
        // AnnotatedString은 텍스트에 스타일이나 메타데이터를 추가할 수 있도록 해줍니다.
        TransformedText(AnnotatedString(formattedText), offsetMapping)
    }
}


// 숫자만 허용하는 함수
fun String.filterNumbers(): String{
    return filter { it.isDigit() }
}

// 숫자에 콤마 넣기 확장함수
fun String.formatNumberWithCommas(): String {
    val decimalFormat = DecimalFormat("#,###")
    return try {
        decimalFormat.format(this.replace(",", "").toBigDecimal())
    } catch (e: NumberFormatException) {
        Log.e("String.formatNumberWithCommas", "NumberFormatException: ${e.message}")
        this // 숫자 형식이 아닌 경우 원래 문자열 반환
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navigateToUpdate: (Int) -> Unit,
    viewModel: MainScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
    pagerState: PagerState
) {


    // 저장버튼 누를때 키보드 내리기
    val focusManager = LocalFocusManager.current
    // 코루틴 스코프
    val coroutineScope = rememberCoroutineScope()
    // ui 상태
    val bikeUiState by viewModel.bikeUiState.collectAsState()
    // 메모 리스트
    val bikeMemoList by viewModel.bikeLazyUiState.collectAsState()
    // 현재 날짜
    val date = currentDateString()

    // 현재 날짜를 입력 및 수정
    // LaunchedEffect는 컴포저블 함수가 처음 실행될 때 또는 지정된 키 값이 변경될 때 코루틴을 실행합니다.
    //여기서는 bikeUiState.bikeDetails.km을 키 값으로 사용합니다. 즉, bikeUiState.bikeDetails.km 값이 변경될 때마다 LaunchedEffect 내부의 코드가 실행됩니다.
    //LaunchedEffect 내부에서는 driveKm.value를 실행합니다. 이는 driveKm의 현재 값을 가져오는 것을 의미합니다.
    //이 코드는 bikeUiState.bikeDetails.km 값이 변경될 때마다 driveKm의 값을 업데이트하는 데 사용됩니다.
    // LaunchedEffect를 사용해 bikeDetails.repairDate의 변경을 감지하고 displayedDate를 업데이트합니다.
    //사용자가 입력한 날짜는 displayedDate와 bikeDetails.repairDate에 모두 반영됩니다.
    val displayedDate = remember { mutableStateOf(date) }

    LaunchedEffect(bikeUiState.bikeDetails.repairDate) {
        bikeUiState.bikeDetails.repairDate = displayedDate.value
    }
 /*   // 주행 거리
    val driveKm = remember { mutableStateOf("0") }
    // 수리 금액
    val repairCost = remember { mutableStateOf("0") }*/

    val filteredBikeMemoList: List<BikeMemo> = bikeMemoList.bikeList.filter { bikeMemo ->
        bikeMemo.date.contains(displayedDate.value)
    }

    Scaffold(
        modifier = Modifier,
        topBar = {
            InventoryTopAppBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = false,
                canNavigateForward = true,
                onNavigateBack = { }, // canNavigateBack = false,
                onNavigateForward = {
                    coroutineScope.launch {
                        if (pagerState.currentPage < 2) {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        MainScreenContent(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(color = Color(0xFFFEF9CF)),
            filteredBikeList = filteredBikeMemoList,
            bikeUiState = bikeUiState,
            onBikeValueChange = viewModel::updateUiState,
            onSaveClick = {
                focusManager.clearFocus()
                coroutineScope.launch {
                    viewModel.saveBikeMemo()
                    bikeUiState.bikeDetails.amount = 0
                    bikeUiState.bikeDetails.etc = ""
                }
            },
            onDeleteBikeMemo = { bikeMemoId ->
                coroutineScope.launch {
                    viewModel.deleteBikeMemo(bikeMemoId)
                }
            },
            onItemClick = { bikeMemoId -> navigateToUpdate(bikeMemoId) },
            displayedDate = displayedDate,
        )


    }
}

@Composable
private fun MainScreenContent(
    modifier: Modifier,
    filteredBikeList: List<BikeMemo>,
    bikeUiState: BikeUiState,
    onBikeValueChange: (BikeDetails) -> Unit,
    onSaveClick: () -> Unit,
    onDeleteBikeMemo: (Int) -> Unit,
    onItemClick: (Int) -> Unit,
    displayedDate: MutableState<String>,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            BikeInputFormContent(
                modifier = Modifier,
                filteredBikeList = filteredBikeList,
                bikeDetails = bikeUiState.bikeDetails,
                onValueChange = onBikeValueChange,
                onSaveClick = onSaveClick,
                onDeleteBikeMemo = onDeleteBikeMemo,
                onItemClick = onItemClick,
                displayedDate = displayedDate,
            )
        }

    }
}

@Composable
fun BikeInputFormContent(
    modifier: Modifier,
    filteredBikeList: List<BikeMemo>,
    bikeDetails: BikeDetails,
    onValueChange: (BikeDetails) -> Unit,
    onSaveClick: () -> Unit,
    onDeleteBikeMemo: (Int) -> Unit,
    onItemClick: (Int) -> Unit,
    displayedDate: MutableState<String>,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color(0xFFFEF9CF))
            .padding(8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = modifier
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
                modifier = modifier
                    .weight(1f)
                    .heightIn(min = 56.dp)
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
                value = bikeDetails.startDate,
                onValueChange = {
                    onValueChange(bikeDetails.copy(startDate = it))
                },
                label = { Text(text = stringResource(id = R.string.purchase_date)) },
                placeholder = { Text(text = stringResource(id = R.string._2000_01_01)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF703BE1),
                    unfocusedBorderColor = Color(0xFF703BE1),
                    focusedLabelColor = Color(0xFF703BE1),
                    unfocusedLabelColor = Color(0xFF703BE1),
                ),
                modifier = modifier
                    .weight(1f)
                    .heightIn(min = 56.dp)
                    .background(color = Color(0x32B193E6)),
                textStyle = TextStyle(
                    fontSize = 20.sp,
                    color = Color(0xFF703BE1), textAlign = TextAlign.Center
                ),
                shape = shapes.small,
                singleLine = true,
            )
        }

        Box(modifier = modifier) {
            Row(
                modifier = modifier.padding(start = 8.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                //  수리 날짜
                OutlinedTextField(
                    value = displayedDate.value,
                    onValueChange = { newRepairDate ->
                        displayedDate.value = newRepairDate
                        onValueChange(bikeDetails.copy(repairDate = newRepairDate))
                    },
                    label = { Text(text = stringResource(id = R.string.repair_date)) },
                    placeholder = { Text(text = stringResource(R.string.auto_insert)) },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF703BE1),
                        unfocusedBorderColor = Color(0xFF703BE1),
                        focusedLabelColor = Color(0xFF703BE1),
                        unfocusedLabelColor = Color(0xFF703BE1),
                    ),
                    modifier = modifier
                        .weight(1f)
                        .heightIn(min = 56.dp)
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
                    modifier = modifier
                        .heightIn(min = 56.dp)
                        .weight(1f),
                    onValueChange = {
                        onValueChange(bikeDetails.copy(selectedOption = it))
                    }
                )
            }
        }
        Row(
            modifier = modifier
                .padding(start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            //  주행 거리
            OutlinedTextField(
                value = bikeDetails.km.toString(),
                onValueChange = { newKmString ->
                    onValueChange(bikeDetails.copy(km = newKmString.filterNumbers().toIntOrNull() ?: 0))
                },
                label = { Text(text = stringResource(id = R.string.mileage)) },
                placeholder = { Text(text = stringResource(R.string.example_number)) },
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
                modifier = modifier
                    .weight(1f)
                    .heightIn(min = 56.dp)
                    .background(color = Color(0x32B193E6)),
                textStyle = TextStyle(
                    fontSize = 20.sp,
                    color = Color(0xFF703BE1), textAlign = TextAlign.Center
                ),
                shape = shapes.small,
                singleLine = true,
                visualTransformation = thousandSeparatorTransformation()
            )

            Spacer(modifier = Modifier.size(16.dp))

            //  금액
            OutlinedTextField(
                value = bikeDetails.amount.toString(),
                onValueChange = { newAmountString ->
                    onValueChange(bikeDetails.copy(amount = newAmountString.filterNumbers().toIntOrNull() ?: 0))
                },
                label = { Text(text = stringResource(id = R.string.amount)) },
                placeholder = { Text(text = stringResource(id = R.string.example_number)) },
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
                modifier = modifier
                    .weight(1f)
                    .heightIn(min = 56.dp)
                    .background(color = Color(0x32B193E6)),
                textStyle = TextStyle(
                    fontSize = 20.sp,
                    color = Color(0xFF703BE1), textAlign = TextAlign.Center
                ),
                shape = shapes.small,
                singleLine = true,
                visualTransformation = thousandSeparatorTransformation()
            )
        }

        Row(
            modifier = modifier
                .padding(start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically

        ) {
            //  기타 내용
            OutlinedTextField(
                value = bikeDetails.etc,
                onValueChange = { onValueChange(bikeDetails.copy(etc = it)) },
                label = { Text(text = stringResource(id = R.string.other_details)) },
                placeholder = { Text(text = stringResource(R.string.other_details)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF703BE1),
                    unfocusedBorderColor = Color(0xFF703BE1),
                    focusedLabelColor = Color(0xFF703BE1),
                    unfocusedLabelColor = Color(0xFF703BE1),
                ),
                modifier = modifier
                    .weight(1f)
                    .heightIn(min = 56.dp)
                    .background(color = Color(0x32B193E6)),
                textStyle = TextStyle(
                    fontSize = 20.sp,
                    color = Color(0xFF703BE1), textAlign = TextAlign.Center
                ),
                shape = shapes.small,
                singleLine = true,
            )

            Spacer(modifier = Modifier.size(16.dp))

            SaveButton(
                modifier = modifier
                    .heightIn(min = 56.dp)
                    .weight(1f),
                stringResId = R.string.save,
                onClick = onSaveClick,
            )

        }

        Spacer(modifier = Modifier.size(8.dp))

        Log.d("MainScreen", "수리날짜: ${bikeDetails.repairDate}")

        // 리사이크러뷰
        BikeMemoList(
            modifier = Modifier
                .padding(8.dp)
                .height(180.dp),
            bikeMemoList = filteredBikeList,//bikeLazyState.bikeList,
            route = HomeDestination.route,
            onDeleteBikeMemo = onDeleteBikeMemo,
            onItemClick = onItemClick,
        )

        // 합계
        OutlinedTextField(
            modifier = Modifier
                .focusProperties { canFocus = false } // 포커스 안되게 하기
                .heightIn(min = 56.dp)
                .padding(8.dp)
                .background(color = Color(0x32B193E6)),
            label = {
                Text(
                    text = stringResource(id = R.string.total),
                    color = Color(0xFF703BE1)
                )
            },
            value = filteredBikeList.sumOf { it.amount }.toString().formatNumberWithCommas(),
            onValueChange = {},
            suffix = {
                Text(
                    text = stringResource(id = R.string.currency_unit),
                    color = Color(0xFF703BE1)
                )
            },
            keyboardOptions = KeyboardOptions.Default
                .copy(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF703BE1),
                unfocusedBorderColor = Color(0xFF703BE1),
                focusedLabelColor = Color(0xFF703BE1),
                unfocusedLabelColor = Color(0xFF703BE1),
            ),
            textStyle = TextStyle(
                fontSize = 20.sp,
                color = Color(0xFF703BE1), textAlign = TextAlign.Center
            ),
            shape = shapes.small,
            singleLine = true,
            enabled = true
        )
        Spacer(modifier = Modifier.size(8.dp))
        //참고
        DisplayInfoText(
            modifier = Modifier,
            stringResId = stringResource(id = selectsSet(bikeDetails)), //함수에서 바로 리소스 ID 반환
            textAlign = TextAlign.Center,
            fontSize = 24
        )
        // information
        DisplayInfoText(
            modifier = Modifier
                .padding(8.dp),
            stringResId = stringResource(id = contentsSet(bikeDetails)),
            textAlign = TextAlign.Justify,
            fontSize = 20
        )
    }
}

@Composable
fun SaveButton(
    modifier: Modifier,
    onClick: () -> Unit = {},
    @StringRes stringResId: Int,
) {
    OutlinedButton(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(Color(0x32B193E6)),
        shape = shapes.small,
        onClick = {
            onClick()
        },
        border = BorderStroke(1.dp, Color(0xFF703BE1)),
    ) {
        Text(
            text = stringResource(id = stringResId),
            fontSize = 20.sp,
            color = Color(0xFF703BE1)
        )
    }
}

@Composable
fun DisplayInfoText(
    modifier: Modifier,
    stringResId: String,
    textAlign: TextAlign,
    fontSize: Int,
) {
    Text(
        text = stringResId,
        modifier = modifier.background(color = Color(0xffFEF9CF)),
        color = Color(0xFF0B6380),
        textAlign = textAlign,
        fontSize = fontSize.sp,
    )
}



@Preview
@Composable
private fun SaveButtonPreview() {
    SaveButton(modifier = Modifier,
        stringResId = R.string.save,
        onClick = {}
    )
}


@Preview
@Composable
private fun StringTextPreview() {
    DisplayInfoText(
        stringResId = stringResource(id = R.string.nickname),
        modifier = Modifier,
        textAlign = TextAlign.Center,
        fontSize = 20
    )
}


@Preview
@Composable
private fun MainScreenPreview() {
    BikeRepairDocTheme {
    }
}
