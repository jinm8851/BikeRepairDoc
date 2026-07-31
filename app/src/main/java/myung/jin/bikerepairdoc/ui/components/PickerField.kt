package myung.jin.bikerepairdoc.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
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
import androidx.compose.ui.text.style.TextAlign
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

    val colorScheme = MaterialTheme.colorScheme

    // 다이얼로그가 열릴 때 현재 선택된 날짜를 초기값으로 설정 (선택 사항)
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedMillis = datePickerState.selectedDateMillis
                    if (selectedMillis != null) {
                        val localDate = Instant.ofEpochMilli(selectedMillis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        onDateSelected(localDate.format(formatter))
                    }
                    showDatePicker = false
                }) {
                    Text(
                        "확인",
                        style = MaterialTheme.typography.labelLarge,
                        color = colorScheme.primary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(
                        "취소",
                        style = MaterialTheme.typography.labelLarge,
                        color = colorScheme.outline
                    )
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = colorScheme.primary,
                    selectedDayContentColor = colorScheme.onPrimary,
                    todayContentColor = colorScheme.primary,
                    todayDateBorderColor = colorScheme.primary
                )
            )
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
            disabledTextColor = colorScheme.onSurface,
            disabledBorderColor = colorScheme.outline,
            disabledLabelColor = colorScheme.onSurfaceVariant,
            disabledPlaceholderColor = colorScheme.onSurfaceVariant,
            disabledLeadingIconColor = colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = colorScheme.onSurfaceVariant,
            disabledContainerColor = Color.Transparent
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable { showDatePicker = true },
        textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
        shape = MaterialTheme.shapes.small,
    )
}