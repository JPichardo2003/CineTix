package com.ucne.cinetix.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ucne.cinetix.ui.theme.AppOnPrimaryColor
import com.ucne.cinetix.ui.theme.ButtonColor

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    goToHomeScreen: () -> Unit
) {
    FloatingActionButton(
        modifier = modifier.size(42.dp),
        containerColor = ButtonColor,
        contentColor = AppOnPrimaryColor,
        onClick = { goToHomeScreen() }
    ) {
        Icon(
            imageVector = Icons.Rounded.ArrowBack,
            contentDescription = "back icon",
        )
    }
}