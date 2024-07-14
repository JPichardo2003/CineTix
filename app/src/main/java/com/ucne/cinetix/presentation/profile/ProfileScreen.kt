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
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.skydoves.landscapist.CircularReveal
import com.skydoves.landscapist.ShimmerParams
import com.skydoves.landscapist.coil.CoilImage
import com.ucne.cinetix.R
import com.ucne.cinetix.presentation.components.BackButton
import com.ucne.cinetix.ui.theme.AppOnPrimaryColor
import com.ucne.cinetix.ui.theme.AppPrimaryColor
import com.ucne.cinetix.ui.theme.ButtonColor
import com.ucne.cinetix.ui.theme.rememberGradientColors

@Composable
fun ProfileScreen(
    goToHomeScreen: () -> Unit
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
                onClick = { /* Acción de edición de perfil */ }
            ) {
                Icon(imageVector = Icons.Rounded.Edit, contentDescription = "edit profile")
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
                imageModel = R.drawable.profile_photo,
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
                contentDescription = null
            )

            // Nombre de usuario
            Text(
                text = "JPichardo2003",
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = AppOnPrimaryColor,
                modifier = Modifier.constrainAs(userName) {
                    top.linkTo(profilePhoto.bottom, margin = 4.dp)
                    start.linkTo(profilePhoto.start)
                    end.linkTo(profilePhoto.end)
                }
            )

            // Botón de lista de deseos
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
                onClick = {}
            ) {
                Text(text = "WishList")
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
                    text = "version: 1.0.5",
                    fontWeight = FontWeight.Light,
                    fontSize = 14.sp,
                    color = AppOnPrimaryColor.copy(alpha = 0.5F)
                )
            }

            // Configuraciones de perfil
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
                        color = AppOnPrimaryColor,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Divider()

                    ProfileTextField(
                        label = "Email",
                        value = "example@email.com",
                        placeholder = "Enter your email",
                        onValueChange = {}
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileTextField(
                        label = "Password",
                        value = "********",
                        placeholder = "Enter your password",
                        onValueChange = {}
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileTextField(
                        label = "Username",
                        value = "JPichardo2003",
                        placeholder = "Enter your username",
                        onValueChange = {}
                    )
                }
            }
        }
    }

}

@Composable
fun ProfileTextField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
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
            color = AppOnPrimaryColor,
            fontWeight = FontWeight.Light,
            modifier = Modifier.weight(1f)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(2f),
            placeholder = { Text(placeholder) }
        )
    }
}