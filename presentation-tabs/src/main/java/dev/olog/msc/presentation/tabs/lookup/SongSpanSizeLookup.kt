package dev.olog.msc.presentation.tabs.lookup

class SongSpanSizeLookup : AbsSpanSizeLookup() {

    override fun getSpanSize(position: Int): Int {
        return spanCount
    }
}