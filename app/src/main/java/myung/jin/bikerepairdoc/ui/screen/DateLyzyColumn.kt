package myung.jin.bikerepairdoc.ui.screen


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import myung.jin.bikerepairdoc.R
import myung.jin.bikerepairdoc.ui.room.BikeMemo
import myung.jin.bikerepairdoc.ui.room.CashBook

//랜덤으로 색상 설정 (Material 3 컨테이너 색상 계열로 변경)
object ColorGenerator {
    private val colorPalette = listOf(
        Color(0xFFEADDFF), // Primary Container light
        Color(0xFFD0BCFF), // Primary light
        Color(0xFFE8DEF8), // Secondary Container light
        Color(0xFFF2B8B5), // Error Container light
        Color(0xFFC1ECD6), // Tertiary Container light
        Color(0xFFECE68D), // Primary Container alternative
        Color(0xFFFFDAD6), // Error light
        Color(0xFFD7E3FF), // Custom blue-ish
        Color(0xFFF3E8FF), // Custom purple-ish
        Color(0xFFE8F5E9)  // Custom green-ish
    )

    private var lastColor: Color? = null

    fun generateRandomColor(): Color {
        val newColor = colorPalette.filter { it != lastColor }.random()
        lastColor = newColor
        return newColor.copy(alpha = 0.4f) // 배경으로 쓰기 좋게 투명도 조절
    }
}


@Composable
fun <T> GenericDateList(
    modifier: Modifier = Modifier,
    itemList: List<T>,
    showDateHeader: Boolean = true,
    getDate: (T) -> String,
    itemContent: @Composable (T, Color) -> Unit
) {
    val dateToColorMap = remember { mutableMapOf<String, Color>() }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(colorScheme.surface)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(itemList.size) { index ->
            val item = itemList[index]
            val date = getDate(item)

            val backgroundColor = dateToColorMap.getOrPut(date) {
                ColorGenerator.generateRandomColor()
            }

            val isFirstInGroup = index == 0 || date != getDate(itemList[index - 1])

            if (isFirstInGroup) {
                if (index > 0) {
                    // 날짜가 변경될 때 구분선 추가
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 1.dp,
                        color = colorScheme.outline.copy(alpha = 0.2f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                if (showDateHeader) {
                    Text(
                        text = date,
                        style = MaterialTheme.typography.titleMedium,
                        color = colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            } else {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 0.5.dp,
                    color = colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            }

            itemContent(item, backgroundColor)
        }
    }
}


@Composable
fun BikeMemoList(
    modifier: Modifier,
    bikeMemoList: List<BikeMemo>,
    route: String,
    onDeleteBikeMemo: (Int) -> Unit,
    onItemClick: (Int) -> Unit,
) {
    // 날짜별 색상저장
    val dateToColorMap = remember { mutableMapOf<String, Color>() }

    //  var previousDate by remember { mutableStateOf("") } // 이전 아이템의 날짜 저장
    LazyColumn(
        modifier = modifier
            .height(180.dp)
            .background(Color(0xFFF8F4B7))
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(bikeMemoList.size) { index -> // bikeMemoList의 크기를 이용하여 items 생성
            val bikeMemo = bikeMemoList[index]
            //이전 날짜와 바이크메모 날짜를 비교해 같은 날짜인지 확인 즉 날짜가 변경되었는지 확인하는 변수
            // val isDateChanged = bikeMemo.date != previousDate
            // 날짜를 키로 같은 날짜에 랜덤컬러 적용
            val backgroundColor =
                dateToColorMap.getOrPut(bikeMemo.date) { ColorGenerator.generateRandomColor() }

            // 첫 번째 항목이 아니고 날짜가 변경된 경우 스페이서를 추가합니다
            if (index > 0 && bikeMemo.date != bikeMemoList[index - 1].date) {
                Spacer(modifier = Modifier.height(16.dp))
            }
            // 날짜가 변경된 경우에만 Spacer 추가
            /*  if (isDateChanged && previousDate.isNotEmpty()) {
                  Spacer(modifier = Modifier.height(16.dp))
              }*/
            BikeMemoDetail(
                bikeMemo = bikeMemo,
                route = route,
                onDeleteClick = { onDeleteBikeMemo(bikeMemo.no) },
                onItemClick = { onItemClick(bikeMemo.no) },
                backgroundColor = backgroundColor
            )
            //  previousDate = bikeMemo.date // 현재 아이템의 날짜를 previousDate에 저장
        }
    }

}

@Composable
fun CashBookDetail(
    modifier: Modifier = Modifier,
    cashBook: CashBook,
    route: String,
    onDeleteClick: () -> Unit,
    onItemClick: (() -> Unit)? = null,
    backgroundColor: Color = Color.Transparent
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp)
            .combinedClickable(
                onClick = { },
                onLongClick = { showDialog = true }
            ),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = colorScheme.onSurface
        ),
        shape = RectangleShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            CashBookDetailTexts(cashBook = cashBook, route = route)
        }
    }

    // 삭제 다이얼로그 추가
    if (showDialog) {
        OptionDialog(
            onDismissRequest = { showDialog = false },
            onDelete = {
                onDeleteClick()
                showDialog = false
            },
            onEdit = onItemClick?.let {
                {
                    it()
                    showDialog = false
                }
            }
        )
    }
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun BikeMemoDetail(
    modifier: Modifier = Modifier,
    bikeMemo: BikeMemo,
    route: String,
    onDeleteClick: () -> Unit,
    onItemClick: () -> Unit,
    backgroundColor: Color = Color.Transparent // 기본값을 투명으로 변경
) {
    var showDialog by remember { mutableStateOf(false) }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(1.dp)
            .combinedClickable(
                onClick = { },
                onLongClick = { showDialog = true }
            ),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = colorScheme.onSurface
        ),
        shape = RectangleShape
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
        ) {
            /*FlowRow는 Jetpack Compose에서 제공하는 레이아웃 컴포저블 중 하나로, Row와 Column과 유사하지만,
             컨테이너 공간이 부족할 때 아이템들을 자동으로 다음 줄로 넘겨 배치하는 기능을 제공합니다.
              즉, 아이템들이 마치 물 흐르듯이(flow) 배치된다고 해서 FlowRow라는 이름이 붙었습니다.*/
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                itemVerticalAlignment = Alignment.CenterVertically

            ) {

                // 코드 중복을 방지하기 위해 도우미 기능 사용
                BikeMemoDetailTexts(bikeMemo, route)


            }
            if (bikeMemo.note.isNotEmpty()) {
                Text(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .align(Alignment.CenterHorizontally),
                    text = bikeMemo.note,
                    style = TextStyle(fontSize = 20.sp),
                    textAlign = TextAlign.Justify
                )
            }
        }
    }
    // 삭제 다이얼로그
    if (showDialog) {
        OptionDialog(
            onDismissRequest = { showDialog = false },
            onDelete = {
                onDeleteClick()
                showDialog = false
            },
            onEdit = {
                onItemClick()
                showDialog = false
            }
        )

    }
}

@Composable
fun CashBookDetailTexts(
    modifier: Modifier = Modifier,
    cashBook: CashBook,
    route: String
) {

    val isIncome = cashBook.income != 0L

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isIncome) stringResource(R.string.income) else stringResource(R.string.expenditure),
            style = MaterialTheme.typography.bodyMedium,
            color = if (isIncome) colorScheme.primary else colorScheme.error,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = cashBook.content,
            style = MaterialTheme.typography.bodyLarge,
            color = colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(2f)
        )
        Text(
            text = if (isIncome) cashBook.income.toString()
                .formatNumberWithCommas() else cashBook.expense.toString().formatNumberWithCommas(),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = if (isIncome) colorScheme.primary else colorScheme.error,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.5f)
        )
    }
}

@Composable
fun BikeMemoDetailTexts(bikeMemo: BikeMemo, route: String) {

    val fontSize = if (route == TotalScreenDestination.route) 18.sp else 20.sp
    if (route == TotalScreenDestination.route) {
        Text(
            text = bikeMemo.date,
            style = TextStyle(fontSize = fontSize),
            textAlign = TextAlign.Center
        )
    }
    Text(
        text = bikeMemo.refer,
        style = TextStyle(fontSize = fontSize),
        textAlign = TextAlign.Center,
    )
    Text(
        text = bikeMemo.km.toString().formatNumberWithCommas() + "km",
        style = TextStyle(fontSize = fontSize),
        textAlign = TextAlign.Center
    )
    Text(
        text = bikeMemo.amount.toString().formatNumberWithCommas() + "원",
        style = TextStyle(fontSize = fontSize),
        textAlign = TextAlign.Center
    )


}

@Composable
fun OptionDialog(
    onDismissRequest: () -> Unit,
    onDelete: () -> Unit,
    onEdit: (() -> Unit)? = null
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surfaceContainerHigh,
                contentColor = colorScheme.onSurface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.choose),
                    style = MaterialTheme.typography.headlineSmall,
                    color = colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.delete))
                }

                onEdit?.let {
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = it
                    ) {
                        Text(stringResource(R.string.edit))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDismissRequest
                ) {
                    Text(stringResource(R.string.korea_cancel))
                }
            }
        }
    }
}


@Preview
@Composable
private fun BikeMemoDetailPreview() {
    BikeMemoDetail(
        bikeMemo = BikeMemo(),
        route = "TOTAL",
        onDeleteClick = {},
        onItemClick = {},
        backgroundColor = Color(0xFFE0BBE4)
    )
}

