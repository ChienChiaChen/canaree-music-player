package dev.olog.msc.presentation.base.theme.player.theme

interface IPlayerTheme {
   fun isDefault(): Boolean
   fun isFlat(): Boolean
   fun isSpotify(): Boolean
   fun isFullscreen(): Boolean
   fun isBigImage(): Boolean
   fun isClean(): Boolean
   fun isMini(): Boolean
}