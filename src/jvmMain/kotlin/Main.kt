import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import androidx.compose.ui.window.WindowPlacement.*
import cn.yurin.stardewlauncher.App
import cn.yurin.stardewlauncher.Data
import cn.yurin.stardewlauncher.TitleBar
import cn.yurin.stardewlauncher.generated.resources.*
import cn.yurin.stardewlauncher.generated.resources.Res
import cn.yurin.stardewlauncher.generated.resources.close_24px
import cn.yurin.stardewlauncher.generated.resources.expand_content_24px
import cn.yurin.stardewlauncher.generated.resources.remove_24px
import org.jetbrains.compose.resources.painterResource

fun main() = application {
    val state = rememberWindowState(
        position = WindowPosition(Alignment.Center),
        size = DpSize(900.dp, 600.dp)
    )
    Window(
        onCloseRequest = ::exitApplication,
        state = state,
        title = "Stardew Launcher",
        undecorated = true,
        transparent = true
    ) {
        Data.window = this
        App(
            titleBar = { body ->
                WindowDraggableArea(
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                when (state.placement) {
                                    Floating -> state.placement = Maximized
                                    Maximized -> state.placement = Floating
                                    Fullscreen -> state.placement = Maximized
                                }
                            }
                        )
                    }
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceBright
                    ) {
                        TitleBar(
                            title = {
                                Text(
                                    text = "Stardew Launcher",
                                    textAlign = TextAlign.Center
                                )
                            },
                            body = body,
                            action = {
                                IconButton(
                                    onClick = { state.isMinimized = true }
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.remove_24px),
                                        contentDescription = null
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        when (state.placement) {
                                            Floating -> state.placement = Maximized
                                            Maximized -> state.placement = Floating
                                            Fullscreen -> state.placement = Maximized
                                        }
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(
                                            when (state.placement) {
                                                Floating -> Res.drawable.expand_content_24px
                                                Maximized -> Res.drawable.collapse_content_24px
                                                Fullscreen -> Res.drawable.collapse_content_24px
                                            }
                                        ),
                                        contentDescription = null
                                    )
                                }
                                IconButton(
                                    onClick = { exitApplication() }
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.close_24px),
                                        contentDescription = null
                                    )
                                }
                            },
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        )
    }
}