package com.ucne.cinetix.presentation.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ucne.cinetix.R
import com.ucne.cinetix.ui.theme.AppOnPrimaryColor
import com.ucne.cinetix.ui.theme.ButtonColor


@Composable
fun SearchBar(
    viewModel: SearchViewModel = hiltViewModel(),
    searchParam: String,
    onSearch: () -> Unit,
    onSearchParamChanged: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchInput: String by remember { mutableStateOf(searchParam) }

    Box(
        modifier = Modifier
            .padding(start = 12.dp, end = 12.dp, bottom = 8.dp)
            .clip(CircleShape)
            .background(ButtonColor)
            .fillMaxWidth()
            .height(54.dp)
    ) {

        TextField(
            value = searchInput,
            onValueChange = {
                searchInput = it
                onSearchParamChanged(searchInput)
            },
            modifier = Modifier
                .fillMaxSize(),
            singleLine = true,
            placeholder = {
                Text(
                    text = "Search...",
                    color = AppOnPrimaryColor.copy(alpha = 0.8F)
                )
            },
            colors = TextFieldDefaults.colors(
                focusedTextColor = AppOnPrimaryColor,
                unfocusedTextColor = AppOnPrimaryColor,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = true,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch()
                }
            ),
            trailingIcon = {
                Row {
                    AnimatedVisibility(visible = searchInput.trim().isNotEmpty()) {
                        IconButton(
                            onClick = {
                                searchInput = ""
                                uiState.searchParam = ""
                                onSearchParamChanged("")
                            }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                tint = AppOnPrimaryColor,
                                contentDescription = "Clear"
                            )
                        }
                    }

                    IconButton(
                        onClick = { onSearch() }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search),
                            tint = AppOnPrimaryColor,
                            contentDescription = "Search"
                        )
                    }
                }
            }
        )
    }
}