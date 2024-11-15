package cn.yurin.stardewlauncher

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.yurin.stardewlauncher.generated.resources.Res
import cn.yurin.stardewlauncher.generated.resources.download_24px
import cn.yurin.stardewlauncher.generated.resources.power_settings_new_24px
import cn.yurin.stardewlauncher.generated.resources.settings_24px
import cn.yurin.stardewlauncher.page.DownloadPage
import cn.yurin.stardewlauncher.page.LaunchPage
import cn.yurin.stardewlauncher.page.SettingPage
import cn.yurin.stardewlauncher.theme.Theme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App(
    titleBar: @Composable (@Composable RowScope.() -> Unit) -> Unit
) {
    LaunchedEffect(Unit) {
        val os = System.getProperty("os.name")
        val file = when {
            os.contains("Windows") -> File("C:\\Program Files (x86)\\Steam\\steamapps\\common\\Stardew Valley\\Stardew Valley.exe")
            os.contains("Linux") -> null
            os.contains("Mac OS") -> null
            else -> error("Unsupported OS: $os")
        }
        if (file != null) {
            if (file.exists()) {
                Data.setGame(file.parentFile)
            }
        }
    }
    Theme(
        darkMode = isSystemInDarkTheme(),
        typography = MaterialTheme.typography
    ) {
        var selectedPage by remember { mutableStateOf(HomePages.Launch) }
        Column {
            titleBar {
                SingleChoiceSegmentedButtonRow {
                    for (page in HomePages.entries) {
                        SegmentedButton(
                            selected = selectedPage == page,
                            onClick = { selectedPage = page },
                            shape = SegmentedButtonDefaults.itemShape(page.ordinal, HomePages.entries.size),
                            colors = SegmentedButtonDefaults.colors(
                                inactiveContainerColor = MaterialTheme.colorScheme.surfaceBright
                            ),
                            icon = {
                                Icon(
                                    painter = painterResource(page.icon),
                                    contentDescription = null
                                )
                            },
                            label = {
                                Text(
                                    text = page.label
                                )
                            }
                        )
                    }
                }
            }
            AnimatedContent(
                targetState = selectedPage,
                transitionSpec = {
                    slideInHorizontally {
                        when (targetState.ordinal > initialState.ordinal) {
                            true -> it
                            else -> -it
                        }
                    }.togetherWith(slideOutHorizontally {
                        when (targetState.ordinal > initialState.ordinal) {
                            true -> -it
                            else -> it
                        }
                    })
                }
            ) { page ->
                when (page) {
                    HomePages.Launch -> LaunchPage()
                    HomePages.Download -> DownloadPage()
                    HomePages.Setting -> SettingPage()
                }
            }
        }
    }
}

@Composable
fun TitleBar(
    title: @Composable RowScope.() -> Unit,
    body: @Composable RowScope.() -> Unit,
    action: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(0.25F),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            title()
        }
        Row(
            modifier = Modifier.weight(0.5F),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            body()
        }
        Row(
            modifier = Modifier.weight(0.25F),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            action()
        }
    }
}

enum class HomePages(val icon: DrawableResource, val label: String) {
    Launch(
        icon = Res.drawable.power_settings_new_24px,
        label = "Launch"
    ),
    Download(
        icon = Res.drawable.download_24px,
        label = "Download"
    ),
    Setting(
        icon = Res.drawable.settings_24px,
        label = "Setting"
    )
}