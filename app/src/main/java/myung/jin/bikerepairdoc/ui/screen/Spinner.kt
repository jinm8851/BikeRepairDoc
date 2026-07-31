package myung.jin.bikerepairdoc.ui.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import myung.jin.bikerepairdoc.R
import myung.jin.bikerepairdoc.ui.theme.BikeRepairDocTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDropdownMenu(
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    selectOption: String = stringResource(R.string.select_option), // 라벨 매개변수를 기본값으로 추가했습니다
    dropdownItems: Array<String> = stringArrayResource(id = R.array.spinner_item) // 항목에 대한 매개변수를 추가했습니다
) {
    // 드롭다운메뉴
    //`isExpanded`는 드롭다운 메뉴가 확장되었는지(열렸는지) 또는 축소되었는지(닫혔는지)
    var isExpanded by remember { mutableStateOf(false) }
    //`dropdownItems.firstOrNull() ?: ""`는 `dropdownItems` 배열에서 첫 번째 항
    var selectedOption by remember { mutableStateOf(dropdownItems.firstOrNull() ?: "") } // 빈 배열 처리

    //===1 드롭다운 메뉴의 전체적인 레이아웃을 구성하는 컴포저블입니다.
    ExposedDropdownMenuBox(
        modifier = modifier.wrapContentSize(),
        expanded = isExpanded,
        onExpandedChange = { isExpanded = !isExpanded },

        ) {
        //===2 실제 드롭다운 메뉴를 구성하는 컴포저블입니다.
        OutlinedTextField(
            value = selectedOption,
            onValueChange = { },
            label = { Text(text = selectOption, color = MaterialTheme.colorScheme.primary) },
            trailingIcon = { // 아이콘
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = isExpanded,
                    modifier = Modifier.size(50.dp)
                )
            },
            readOnly = true,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant)
                //.width(180.dp)
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            //.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            //.background(color = Color(0x32B193E6)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary, // 선택 선 색갈
                unfocusedBorderColor = MaterialTheme.colorScheme.outline, // 선택 안됐을때 색갈
                focusedTrailingIconColor = MaterialTheme.colorScheme.primary, // 선택됐을때 아이콘 색갈
                unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant, // 선택 안됐을때 아이콘 색갈
            ),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            ),
            enabled = dropdownItems.isNotEmpty() // 항목이 없는 경우 비활성화

        )
        ///3
        ExposedDropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }
        ) {
            //===4  드롭다운 메뉴의 각 항목을 구성하는 컴포저블입니다.
            dropdownItems.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = item, color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    modifier = Modifier.background(color = MaterialTheme.colorScheme.surface),
                    onClick = {
                        selectedOption = item
                        isExpanded = false
                        onValueChange(item) // 선택한 옵션을 직접 사용합니다
                    })
            }
        }
    }


}

@Preview
@Composable
private fun DropdownMenuPreview() {
    BikeRepairDocTheme {
        MyDropdownMenu(modifier = Modifier, onValueChange = {})
    }
}