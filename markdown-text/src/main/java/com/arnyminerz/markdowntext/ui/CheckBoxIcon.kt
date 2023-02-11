package com.arnyminerz.markdowntext.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arnyminerz.markdowntext.ui.icons.CheckBox
import com.arnyminerz.markdowntext.ui.icons.CheckBoxOutlineBlank

@Composable
fun CheckBoxIcon(checked: Boolean, alt: String) {
    Icon(
        if (checked)
            CheckBox
        else
            CheckBoxOutlineBlank,
        alt,
        Modifier
            .fillMaxSize()
            .padding(end = 4.dp),
    )
}
