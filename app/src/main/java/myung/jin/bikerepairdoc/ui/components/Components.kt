package myung.jin.bikerepairdoc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import myung.jin.bikerepairdoc.R
import myung.jin.bikerepairdoc.ui.room.ContentName


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashDropDownMenu(
    modifier: Modifier = Modifier,
    selectedOption: String,
    onValueChange: (String) -> Unit,
    onDeleteName: (ContentName) -> Unit,
    selectedNames: List<ContentName>,
) {
    var isExpanded = remember { mutableStateOf(false) }
    val colorScheme = androidx.compose.material3.MaterialTheme.colorScheme

    ExposedDropdownMenuBox(
        modifier = modifier.fillMaxWidth(),
        expanded = isExpanded.value,
        onExpandedChange = { isExpanded.value = !isExpanded.value }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            label = { Text(stringResource(R.string.select_option)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded.value)
            },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                focusedContainerColor = colorScheme.surface,
                unfocusedContainerColor = colorScheme.surface,
                focusedBorderColor = colorScheme.primary,
                unfocusedBorderColor = colorScheme.outline,
            ),
            textStyle = TextStyle(
                color = colorScheme.onSurface,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            ),
            enabled = selectedNames.isNotEmpty()
        )

        ExposedDropdownMenu(
            expanded = isExpanded.value,
            onDismissRequest = { isExpanded.value = false },
            modifier = Modifier.background(colorScheme.surfaceContainer)
        ) {
            selectedNames.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = item.name,
                            color = colorScheme.onSurface,
                            textAlign = TextAlign.Start,
                            fontSize = 18.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { onDeleteName(item) }) {
                            Icon(
                                painter = painterResource(R.drawable.delete_24px),
                                contentDescription = stringResource(R.string.delete),
                                tint = colorScheme.error.copy(alpha = 0.7f)
                            )
                        }
                    },
                    onClick = {
                        isExpanded.value = false
                        onValueChange(item.name)
                    }
                )
            }
        }
    }
}
