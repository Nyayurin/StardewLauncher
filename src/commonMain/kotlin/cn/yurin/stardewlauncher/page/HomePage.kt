package cn.yurin.stardewlauncher.page

import androidx.compose.animation.*
import androidx.compose.runtime.*
import cn.yurin.stardewlauncher.page.LaunchPages.Home
import cn.yurin.stardewlauncher.page.LaunchPages.Manage
import cn.yurin.stardewlauncher.page.launch.HomePage
import cn.yurin.stardewlauncher.page.launch.ManagePage

@Composable
fun LaunchPage() {
    var currentPage by remember { mutableStateOf(Home) }
    AnimatedContent(
        targetState = currentPage,
        transitionSpec = {
            slideInVertically {
                when (targetState.ordinal > initialState.ordinal) {
                    true -> it
                    else -> -it
                }
            }.togetherWith(slideOutVertically {
                when (targetState.ordinal > initialState.ordinal) {
                    true -> -it
                    else -> it
                }
            })
        }
    ) { page ->
        when (page) {
            Home -> HomePage(
                onNavigationToManage = { currentPage = Manage }
            )
            Manage -> ManagePage(
                onNavigationBack = { currentPage = Home }
            )
        }
    }
}

@Composable
fun DownloadPage() {

}

@Composable
fun SettingPage() {

}

enum class LaunchPages {
    Home, Manage
}