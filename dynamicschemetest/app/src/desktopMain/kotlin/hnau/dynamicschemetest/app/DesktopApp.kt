package hnau.dynamicschemetest.app

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import hnau.common.app.model.app.DesktopApp
import hnau.common.app.model.theme.ThemeBrightness
import kotlinx.coroutines.runBlocking

@OptIn(InternalComposeApi::class)
fun main() = runBlocking {
    val app = DesktopApp(
        scope = this,
        seed = createPinFinAppSeed(
            defaultBrightness = ThemeBrightness.Dark,
        ),
    )
    val projector = createAppProjector(
        scope = this,
        model = app,
    )
    application {
        val scale = 2f
        Window(
            onCloseRequest = { exitApplication() },
            title = "PinFin",
            state = rememberWindowState(
                width = 800.dp * scale,
                height = 320.dp * scale,
            ),
        ) {
            CompositionLocalProvider(
                LocalDensity provides Density(scale),
            ) {
                projector.Content()
            }
        }
    }
}