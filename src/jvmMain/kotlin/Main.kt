import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cn.yurin.stardewlauncher.App
import cn.yurin.stardewlauncher.Data

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Stardew Launcher"
    ) {
        Data.window = this
        App()
    }
}