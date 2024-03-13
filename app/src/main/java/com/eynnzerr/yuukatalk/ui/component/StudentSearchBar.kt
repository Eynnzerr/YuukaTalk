package com.eynnzerr.yuukatalk.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eynnzerr.yuukatalk.R

@Composable
fun StudentSearchBar(
    textValue: String,
    onTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        PlainTextField(
            value = textValue,
            onValueChange = onTextChanged,
            placeholder = { Text(stringResource(id = R.string.search_bar_hint)) },
            leadingIcon = leadingIcon ?: { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = trailingIcon ?: { Icon(Icons.Outlined.Edit, contentDescription = null) },
        )
    }
}

@Preview(
    name = "student search bar",
    showBackground = true
)
@Composable
private fun SearchBarPreview() {
    var text by rememberSaveable { mutableStateOf("") }
    StudentSearchBar(
        textValue = text,
        onTextChanged = { text = it }
    )
}