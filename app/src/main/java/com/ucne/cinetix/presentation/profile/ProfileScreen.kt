package com.ucne.cinetix.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Shop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skydoves.landscapist.CircularReveal
import com.skydoves.landscapist.ShimmerParams
import com.skydoves.landscapist.coil.CoilImage
import com.ucne.cinetix.R
import com.ucne.cinetix.presentation.auth.AuthState
import com.ucne.cinetix.presentation.auth.AuthViewModel
import com.ucne.cinetix.presentation.auth.UsuarioUIState
import com.ucne.cinetix.presentation.components.BackButton
import com.ucne.cinetix.ui.theme.AppOnPrimaryColor
import com.ucne.cinetix.ui.theme.AppPrimaryColor
import com.ucne.cinetix.ui.theme.ButtonColor
import com.ucne.cinetix.ui.theme.rememberGradientColors

@Composable
fun ProfileScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    goToHomeScreen: () -> Unit,
    goToWatchListScreen: () -> Unit,
    goToLoginScreen: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    viewModel.usuarios.collectAsStateWithLifecycle()
    val authState = viewModel.authState.observeAsState()
    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> goToLoginScreen()
            else -> Unit
        }
    }
    ProfileBody(
        uiState = uiState,
        signOut = viewModel::signOut,
        goToHomeScreen = goToHomeScreen,
        goToWatchListScreen = goToWatchListScreen
    )
}

@Composable
fun ProfileBody(
    uiState: UsuarioUIState,
    signOut: () -> Unit,
    goToHomeScreen: () -> Unit,
    goToWatchListScreen: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        val gradientColors = rememberGradientColors()

        ConstraintLayout(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(brush = Brush.verticalGradient(gradientColors))
        ) {
            // Declaración de referencias para ConstraintLayout
            val (
                backButton,
                editButton,
                profileHeading,
                userName,
                topBgImage,
                profilePhoto,
                imageBorder,
                translucentBackground,
                btnWatchList,
                settingsBox,
                appVersion
            ) = createRefs()

            // Botón de retroceso
            BackButton(
                modifier = Modifier
                    .constrainAs(backButton) {
                        start.linkTo(parent.start, margin = 10.dp)
                        top.linkTo(parent.top, margin = 16.dp)
                    }
            ) {
                goToHomeScreen()
            }

            // Botón de edición de perfil
            FloatingActionButton(
                modifier = Modifier
                    .size(42.dp)
                    .constrainAs(editButton) {
                        end.linkTo(parent.end, margin = 10.dp)
                        top.linkTo(parent.top, margin = 16.dp)
                    },
                containerColor = ButtonColor,
                contentColor = AppOnPrimaryColor,
                onClick = signOut
            ) {
                Icon(imageVector = Icons.Rounded.Logout, contentDescription = "LogOut")
            }

            // Encabezado de perfil
            Text(
                modifier = Modifier.constrainAs(profileHeading) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(backButton.top)
                    bottom.linkTo(backButton.bottom)
                },
                text = "Profile",
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppOnPrimaryColor
            )

            // Imagen de fondo superior
            Image(
                painter = painterResource(id = R.drawable.hashiras),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.27F)
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .constrainAs(topBgImage) {
                        top.linkTo(profileHeading.bottom, margin = 16.dp)
                    },
                contentScale = ContentScale.Crop,
                contentDescription = null
            )

            // Caja translúcida sobre la imagen de fondo superior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color(0XFF180E36).copy(alpha = 0.5F),
                                Color(0XFF180E36).copy(alpha = 0.75F),
                                Color(0XFF180E36).copy(alpha = 1F)
                            ),
                            startY = 0F
                        )
                    )
                    .constrainAs(translucentBackground) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(topBgImage.bottom)
                    }
            )

            // Imagen de perfil con borde personalizado
            Box(
                modifier = Modifier
                    .size(83.5.dp)
                    .clip(CircleShape)
                    .background(AppPrimaryColor)
                    .constrainAs(imageBorder) {
                        top.linkTo(topBgImage.bottom)
                        start.linkTo(topBgImage.start, margin = 26.dp)
                        bottom.linkTo(topBgImage.bottom)
                    }
            )

            // Foto de perfil
            CoilImage(
                imageModel = R.drawable.muzanprofilepic,
                previewPlaceholder = R.drawable.profile_photo,
                contentScale = ContentScale.Crop,
                circularReveal = CircularReveal(duration = 1000),
                shimmerParams = ShimmerParams(
                    baseColor = AppOnPrimaryColor,
                    highlightColor = Color.LightGray,
                    durationMillis = 500,
                    dropOff = 0.65F,
                    tilt = 20F
                ),
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .constrainAs(profilePhoto) {
                        top.linkTo(topBgImage.bottom)
                        start.linkTo(topBgImage.start, margin = 28.dp)
                        bottom.linkTo(topBgImage.bottom)
                    },
                contentDescription = "Foto de perfil"
            )

            // Nombre de usuario
            Text(
                text = uiState.userName,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = AppOnPrimaryColor,
                modifier = Modifier.constrainAs(userName) {
                    top.linkTo(profilePhoto.bottom, margin = 4.dp)
                    start.linkTo(profilePhoto.start)
                    end.linkTo(profilePhoto.end)
                }
            )

            // Watch List Button
            Button(
                modifier = Modifier
                    .constrainAs(btnWatchList) {
                        end.linkTo(parent.end, margin = 24.dp)
                        top.linkTo(topBgImage.bottom)
                        bottom.linkTo(topBgImage.bottom)
                    },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4C3D6D),
                    contentColor = AppOnPrimaryColor
                ),
                onClick = {
                    goToWatchListScreen()
                }
            ) {
                Icon(imageVector = Icons.Rounded.Shop, contentDescription = "Watch List")
                Text(text = "WatchList")
            }

            // Versión de la aplicación y logotipo
            Column(
                modifier = Modifier.constrainAs(appVersion) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom, margin = 24.dp)
                },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.cinetix_name),
                    modifier = Modifier.widthIn(max = 100.dp),
                    alpha = 0.78F,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "version: 1.6",
                    fontWeight = FontWeight.Light,
                    fontSize = 14.sp,
                    color = AppOnPrimaryColor.copy(alpha = 0.5F)
                )
            }

            // Configuraciones de perfil
            var passwordVisible by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(12.dp))
                    .background(Color(0XFF423460).copy(alpha = 0.46F))
                    .heightIn(min = 100.dp)
                    .constrainAs(settingsBox) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(appVersion.top)
                        top.linkTo(profilePhoto.bottom)
                    }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    Text(
                        text = "Information",
                        fontSize = 18.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Divider(color = Color.White)

                    ProfileInfoRow(
                        label = "Email",
                        value = uiState.email ?: "no email"
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileInfoRow(
                        label = "Password",
                        value = if (passwordVisible) uiState.password ?: "no password" else "*******",
                        isPassword = true,
                        onEyeClick = { passwordVisible = !passwordVisible }
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileInfoRow(
                        label = "Username",
                        value = uiState.userName
                    )
                }
            }
        }
    }

}

@Composable
fun ProfileInfoRow(
    label: String,
    value: String,
    isPassword: Boolean = false,
    onEyeClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = Color.White,
            fontWeight = FontWeight.Light,
            modifier = Modifier.weight(1f),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(2f)
        ) {
            Text(
                text = value,
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Normal,
            )
            if (isPassword && onEyeClick != null) {
                IconButton(onClick = onEyeClick) {
                    Icon(
                        imageVector = if (value == "*******") Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (value == "*******") "Show password" else "Hide password",
                        tint = Color.White
                    )
                }
            }
        }
    }
}