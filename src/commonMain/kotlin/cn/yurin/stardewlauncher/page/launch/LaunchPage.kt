package cn.yurin.stardewlauncher.page.launch

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.yurin.stardewlauncher.*
import cn.yurin.stardewlauncher.generated.resources.Res
import cn.yurin.stardewlauncher.generated.resources.arrow_back_24px
import cn.yurin.stardewlauncher.page.launch.ManagePages.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun HomePage(onNavigationToManage: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            modifier = Modifier
                .weight(0.4F)
                .fillMaxHeight(),
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom)
            ) {
                val gameInformation = Data.gameInformation
                if (gameInformation != null) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (gameInformation is ModdingGameInformation) {
                            OutlinedButton(
                                onClick = {
                                    GlobalScope.launch {
                                        ProcessBuilder("\"${gameInformation.moddingGame}\"").start()
                                    }
                                },
                                modifier = Modifier.weight(0.5F),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "SMAPI"
                                )
                            }
                        }
                        OutlinedButton(
                            onClick = {
                                GlobalScope.launch {
                                    ProcessBuilder("\"${gameInformation.vanillaGame}\"").start()
                                }
                            },
                            modifier = Modifier.weight(0.5F),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Vanilla"
                            )
                        }
                    }
                }
                OutlinedButton(
                    onClick = onNavigationToManage,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Manage"
                    )
                }
            }
        }
        Surface(
            modifier = Modifier
                .weight(0.6F)
                .fillMaxHeight(),
            shape = RoundedCornerShape(8.dp)
        ) {
        }
    }
}

@Composable
fun ManagePage(onNavigationBack: () -> Unit) {
    var selectedPage by remember { mutableStateOf(Core) }
    Row {
        Surface(
            modifier = Modifier.fillMaxHeight(),
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = onNavigationBack
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.arrow_back_24px),
                        contentDescription = null
                    )
                }
                ClickableText(
                    text = "Core",
                    isFocused = selectedPage == Core,
                    onClick = { selectedPage = Core }
                )
                AnimatedVisibility(Data.gameInformation is ModdingGameInformation) {
                    ClickableText(
                        text = "Mods",
                        isFocused = selectedPage == Mods,
                        onClick = { selectedPage = Mods }
                    )
                }
                AnimatedVisibility(Data.gameInformation is ModdingGameInformation) {
                    ClickableText(
                        text = "SMAPI",
                        isFocused = selectedPage == SMAPI,
                        onClick = { selectedPage = SMAPI }
                    )
                }
            }
        }

        AnimatedContent(
            targetState = selectedPage,
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
                Core -> CorePage()
                Mods -> ModsPage()
                SMAPI -> SMAPIPage()
            }
        }
    }
}

enum class ManagePages {
    Core, Mods, SMAPI
}