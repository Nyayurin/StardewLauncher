package cn.yurin.stardewlauncher.page.launch

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cn.yurin.stardewlauncher.*
import cn.yurin.stardewlauncher.generated.resources.Res
import cn.yurin.stardewlauncher.generated.resources.arrow_drop_down_40px
import cn.yurin.stardewlauncher.generated.resources.arrow_drop_up_40px
import io.github.vinceglb.filekit.core.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import java.io.File

@Composable
fun CorePage() {
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val gameInformation = Data.gameInformation
        AnimatedVisibility(gameInformation != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Stardew Valley",
                    )
                    Text(
                        text = buildString {
                            append(gameInformation!!.vanillaVersion)
                            if (gameInformation is ModdingGameInformation) {
                                append(", SMAPI ${gameInformation.moddingVersion}")
                            }
                        }
                    )
                }
            }
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Game path"
                )
                Box {
                    var expanded by remember { mutableStateOf(false) }
                    ElevatedButton(
                        onClick = { expanded = true },
                    ) {
                        Text(
                            text = gameInformation?.folder.toString()
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = gameInformation?.folder.toString()
                                )
                            },
                            onClick = {
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Select from"
                                )
                            },
                            onClick = {
                                scope.launch {
                                    val os = System.getProperty("os.name")
                                    val out = FileKit.pickFile(
                                        type = PickerType.File(listOf("exe")),
                                        mode = PickerMode.Single,
                                        title = when {
                                            os.contains("Windows") -> "Select Stardew Valley.exe or StardewModdingAPI.exe"
                                            os.contains("Linux") -> "Select Stardew Valley or StardewModdingAPI"
                                            os.contains("Mac OS") -> "Select Stardew Valley or StardewModdingAPI"
                                            else -> error("Unsupported OS: $os")
                                        },
                                        platformSettings = FileKitPlatformSettings(Data.window!!.window)
                                    )
                                    if (out != null && out.baseName in arrayOf("Stardew Valley", "StardewModdingAPI")) {
                                        Data.setGame(out.file.parentFile)
                                    }
                                    expanded = false
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModsPage() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        var selectedMod by remember { mutableStateOf<ModTree?>(null) }
        LazyColumn(
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1F)
        ) {
            items((Data.gameInformation as ModdingGameInformation).mods.subMods) {
                Mod(
                    mod = it,
                    onClick = { mod -> selectedMod = if (selectedMod == mod) null else mod }
                )
            }
        }
        AnimatedVisibility(selectedMod != null) {
            Surface(
                modifier = Modifier.fillMaxHeight(),
                color = MaterialTheme.colorScheme.surfaceContainer
            ) {
                AnimatedContent(selectedMod) { mod ->
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .width(300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        when (mod) {
                            is ModCategory -> {
                                Text(
                                    text = mod.name,
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = "file: ${mod.file.absolutePath}"
                                )
                            }

                            is Mod -> {
                                Text(
                                    text = mod.name,
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = "version: ${mod.version}"
                                )
                                Text(
                                    text = "author: ${mod.author}"
                                )
                                Text(
                                    text = "description: ${mod.description}"
                                )
                                Text(
                                    text = "unique id: ${mod.uniqueID}"
                                )
                                Text(
                                    text = "dependencies: ${mod.dependencies}"
                                )
                                Text(
                                    text = "update keys: ${mod.updateKeys}"
                                )
                                when (mod) {
                                    is SMAPIMod -> {
                                        Text(
                                            text = "smapi mod: ${mod.entryDll}"
                                        )
                                    }

                                    is ContentPackMod -> {
                                        Text(
                                            text = "content pack mod: ${mod.contentPackFor.uniqueID}"
                                        )
                                    }
                                }
                                Text(
                                    text = "file: ${mod.file.absolutePath}"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SMAPIPage() {

}

@Composable
private fun Mod(
    mod: ModTree,
    onClick: (ModTree) -> Unit,
) {
    val scope = rememberCoroutineScope()
    when (mod) {
        is ModCategory -> {
            var open by remember { mutableStateOf(true) }
            Row {
                IconButton(
                    onClick = {
                        open = !open
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            if (open) Res.drawable.arrow_drop_down_40px
                            else Res.drawable.arrow_drop_up_40px
                        ),
                        contentDescription = null
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ElevatedCard(
                        onClick = { onClick(mod) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = mod.enabled,
                                onCheckedChange = {
                                    scope.launch {
                                        mod.file.renameTo(
                                            File(
                                                mod.file.parent,
                                                if (mod.enabled) '.' + mod.file.name
                                                else mod.file.name.substring(1)
                                            )
                                        )
                                        Data.update()
                                    }
                                }
                            )
                            Text(
                                text = mod.name,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    AnimatedVisibility(open) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (subMod in mod.subMods) {
                                Mod(
                                    mod = subMod,
                                    onClick = onClick
                                )
                            }
                        }
                    }
                }
            }
        }

        is Mod -> {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(
                    modifier = Modifier.size(48.dp)
                )
                ElevatedCard(
                    onClick = { onClick(mod) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = mod.enabled,
                            onCheckedChange = {
                                scope.launch {
                                    mod.file.renameTo(
                                        File(
                                            mod.file.parent,
                                            if (mod.enabled) '.' + mod.file.name
                                            else mod.file.name.substring(1)
                                        )
                                    )
                                    Data.update()
                                }
                            }
                        )
                        Text(
                            text = mod.name,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}