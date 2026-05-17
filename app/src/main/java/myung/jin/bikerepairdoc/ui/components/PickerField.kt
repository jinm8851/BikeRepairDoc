package myung.jin.bikerepairdoc.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import myung.jin.bikerepairdoc.ui.theme.shapes
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun DatePickerField(
    label: @Composable () -> Unit,
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // 다이얼로그가 열릴 때 현재 선택된 날짜를 초기값으로 설정 (선택 사항)
    if (showDatePicker){
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false},
            confirmButton = {
                TextButton(onClick = {
                    val selectedMillis = datePickerState.selectedDateMillis
                    if (selectedMillis != null){
                        val localDate = Instant.ofEpochMilli(selectedMillis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        onDateSelected(localDate.format(formatter))
                    }
                    showDatePicker = false
                }) {
                    Text("확인", color = Color(0xFF703BE1), fontSize = 18.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false}) {
                    Text("취소", color = Color(0xFF703BE1), fontSize = 18.sp)
                }
            }
        ) {
            DatePicker(state = datePickerState,
                colors = DatePickerDefaults.colors(
                    dayContentColor = Color(0xFF703BE1),
                    titleContentColor = Color(0xFF703BE1),
                    headlineContentColor = Color(0xFF703BE1),
                    weekdayContentColor = Color(0xFF703BE1),
                    subheadContentColor = Color(0xFF703BE1),
                    yearContentColor = Color(0xFF703BE1),
                    navigationContentColor = Color(0xFF703BE1),
                ))
        }
    }
    // 클릭 가능한 읽기 전용 텍스트 필드
    OutlinedTextField(
        value = selectedDate,
        onValueChange = { }, // 읽기 전용이므로 비워둠
        label = label,
        readOnly = true,
        enabled = false, // 클릭 이벤트를 부모 Box나 Modifier에서 처리하기 위해 false 또는 readonly 설정
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = Color(0xFF703BE1),
            disabledBorderColor = Color(0xFF703BE1),
            disabledLabelColor = Color(0xFF703BE1),
            disabledPlaceholderColor = Color(0xFF703BE1),
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable { showDatePicker = true },
        textStyle = TextStyle(
            fontSize = 20.sp,
            color = Color(0xFF703BE1), textAlign = TextAlign.Center
        ),
        shape = shapes.small,
    )
}