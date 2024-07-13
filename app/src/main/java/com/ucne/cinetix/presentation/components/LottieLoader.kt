package com.ucne.cinetix.presentation.components

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun LoopReverseLottieLoader(
    modifier: Modifier = Modifier,
    @RawRes lottieFile: Int,
    alignment: Alignment = Alignment.Center,
    enableMergePaths: Boolean = true,
) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(lottieFile))
    val reverse = remember { mutableStateOf(false) }
    val anim = rememberLottieAnimatable()
    if (reverse.value.not())
        LaunchedEffect(key1 = composition) {
            anim.animate(composition = composition, speed = 1f)
            reverse.value = true
        }
    if (reverse.value) {
        LaunchedEffect(composition) {
            anim.animate(composition = composition, speed = -1f)
            reverse.value = false
        }
    }

    LottieAnimation(
        composition,
        anim.value,
        modifier = modifier,
        enableMergePaths = remember { enableMergePaths },
        alignment = alignment
    )
}