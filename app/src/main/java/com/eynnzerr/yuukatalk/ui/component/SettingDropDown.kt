package com.eynnzerr.yuukatalk.ui.component

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingDropDown(
    modifier: Modifier = Modifier,
    title: String,
    initialValue: String = "",
    options: List<String>,
    onSelect: (String) -> Unit,
) {
    var text by remember { mutableStateOf(initialValue) }
    val (allowExpanded, setExpanded) = remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
       Text(
           text = title,
           style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp)
       )

        ExposedDropdownMenuBox(
            expanded = allowExpanded,
            onExpandedChange = setExpanded,
        ) {
            OutlinedTextField(
                modifier = Modifier.menuAnchor().width(120.dp),
                value = text,
                onValueChange = {
                    text = it
                    onSelect(text)
                },
                singleLine = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = allowExpanded,
                        // modifier = Modifier.menuAnchor(MenuAnchorType.SecondaryEditable),
                    )
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
            )
            ExposedDropdownMenu(
                expanded = allowExpanded,
                onDismissRequest = { setExpanded(false) },
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                        onClick = {
                            text = option
                            setExpanded(false)
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun SettingDropDownPreview() {
    SettingDropDown(
        title = "字体",
        options = (0..10).map { it.toString() }
    ) { Log.d("Preview", "select $it") }
}