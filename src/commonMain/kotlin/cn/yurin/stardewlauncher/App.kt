package cn.yurin.stardewlauncher

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.ScaleTransition
import cn.yurin.stardewlauncher.theme.Theme

@Composable
@Preview
fun App() {
    Theme(
        darkMode = isSystemInDarkTheme(),
        typography = MaterialTheme.typography
    ) {
        Navigator(HomeScreen) { navigator ->
            ScaleTransition(navigator)
        }
    }
}