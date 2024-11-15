package cn.yurin.stardewlauncher

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.FrameWindowScope

object Data {
    var window by mutableStateOf<FrameWindowScope?>(null)
    var gameInformation by mutableStateOf<GameInformation?>(null)
}

open class GameInformation(
    val folder: String,
    val vanillaGame: String,
    val vanillaVersion: String
)

class ModdingGameInformation(
    folder: String,
    vanillaGame: String,
    vanillaVersion: String,
    val moddingGame: String,
    val moddingVersion: String,
    val mods: List<ModTree>
) : GameInformation(folder, vanillaGame, vanillaVersion)