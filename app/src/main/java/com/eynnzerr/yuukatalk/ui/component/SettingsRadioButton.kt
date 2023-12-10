package com.eynnzerr.yuukatalk.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsRadioButton(
    modifier: Modifier = Modifier,
    title: String,
    isSelected: Boolean = false,
    onSelect: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(
                color = Color.Unspecified,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(8.dp, 16.dp)
            .selectable(selected = isSelected, onClick = onSelect),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            maxLines = 1,
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
        )
        RadioButton(
            selected = isSelected,
            onClick = onSelect,
            modifier = Modifier
                .padding()
                .clearAndSetSemantics { },
        )
    }
}