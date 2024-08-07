package com.ucne.cinetix.presentation.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ucne.cinetix.R
import com.ucne.cinetix.ui.theme.rememberGradientColors

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    goToSignUpScreen: () -> Unit,
    goToHomeScreen: () -> Unit
){
    val uiState by viewModel.uiState.collectAsState()
    val authState = viewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        if(authState.value is AuthState.Loading){
            Toast.makeText(context, "Loading", Toast.LENGTH_SHORT).show()
        }
        when(authState.value) {
            is AuthState.Authenticated -> {goToHomeScreen()}
            is AuthState.Error -> Toast.makeText(context,
                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }
    LoginBody(
        uiState = uiState,
        goToSignUpScreen = goToSignUpScreen,
        goToHomeScreen = goToHomeScreen,
        onEmailChanged = viewModel::onEmailChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        authState = authState,
        login = viewModel::login
    )
}

@Composable
fun LoginBody(
    uiState: UsuarioUIState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    authState: State<AuthState?>,
    login: (String, String) -> Unit,
    goToSignUpScreen: () -> Unit,
    goToHomeScreen: () -> Unit
) {
    var showPassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    rememberGradientColors()
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ctxalt_logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(100.dp)
                .padding(top = 32.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "CineTix",
            color = Color.White,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "All films in one place",
            fontSize = 16.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "Login To Your Account",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            label = { Text(text = "Email", color = Color.White) },
            value = uiState.email ?: "",
            onValueChange = onEmailChanged,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Email,
                    contentDescription = null,
                    tint = Color.White
                )
            },
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.White,
                cursorColor = Color.White
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            label = { Text(text = "Password", color = Color.White) },
            value = uiState.password ?: "",
            onValueChange = onPasswordChanged,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = Color.White
                )
            },
            trailingIcon = {
                IconButton(onClick = {
                    showPassword = !showPassword
                }) {
                    if (showPassword) {
                        Icon(
                            imageVector = Icons.Outlined.VisibilityOff,
                            contentDescription = "Password Icon",
                            tint = Color.White
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Visibility,
                            contentDescription = "Password Icon",
                            tint = Color.White
                        )
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.White,
                cursorColor = Color.White
            ),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password,
                imeAction = ImeAction.None),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(
            onClick = {
                login(
                    uiState.email ?: "",
                    uiState.password ?: ""
                ) },
            enabled = authState.value != AuthState.Loading,
            modifier = Modifier.width(300.dp)
        ) {
            Text(text = "Login", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ){
            TextButton(onClick = goToSignUpScreen) {
                Text(text = "Don't Have Account?", color = Color(0xFF1FC57A))
            }

            Text(text = " | ", color = Color(0xFF1FC57A))

            TextButton(onClick = goToHomeScreen ) {
                Text(text = "Join as a Guest", color = Color(0xFF1FC57A))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}