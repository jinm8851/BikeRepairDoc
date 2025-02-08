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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import myung.jin.bikerepairdoc.R
import myung.jin.bikerepairdoc.ui.room.BikeMemo

//랜덤으로 색상 설정
object ColorGenerator {
    // 선택할 색상 목록 정의
    private val colorPalette = listOf(
        Color(0x30EF9A9A), // Light Purple
        Color(0x30CE93D8), // Medium Purple
        Color(0x309FA8DA), // Pink
        Color(0x3080DEEA), // Light Pink
        Color(0x30A5D6A7), // Peach
        Color(0x30E6EE9C), // Light Peach
        Color(0x30FFE082), // Yellow
        Color(0x30FFAB91), // Light Purple
        Color(0x303B65E1), // Purple
        Color(0x300B6380) // Blue
    )

    fun generateRandomColor(): Color {
        // 팔레트에서 무작위 색상 선택
        return colorPalette.random()
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
            contentColor = Color(0xFF703BE1)
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

//삭제 다이얼로그
@Composable
fun OptionDialog(
    onDismissRequest: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest, // 닫기 요청 시 호출되는 콜백 함수
        properties = DialogProperties(
            dismissOnBackPress = true, // 뒤로 가기 버튼으로 닫기 허용
            dismissOnClickOutside = true  // 바깥쪽 클릭으로 닫기 허용
        )
    ) {

        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                   // .background(Color(0x32B193E6)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = stringResource(R.string.choose),
                    style = TextStyle(fontSize = 24.sp, color = Color(0xFF0B6380)),
                    textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
                SaveButton(
                    modifier = Modifier
                        .height(60.dp)
                        .width(100.dp),
                    onClick = onDelete,
                    stringResId = R.string.삭제
                )

                Spacer(modifier = Modifier.height(16.dp))
                SaveButton(
                    modifier = Modifier
                        .height(60.dp)
                        .width(100.dp),
                    onClick = onEdit,
                    stringResId = R.string.edit
                )

                Spacer(modifier = Modifier.height(16.dp))
                SaveButton(
                    modifier = Modifier
                        .height(60.dp)
                        .width(100.dp),
                    onClick = onDismissRequest,
                    stringResId = R.string.korea_cancel
                )
                Spacer(modifier = Modifier.height(16.dp))

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

