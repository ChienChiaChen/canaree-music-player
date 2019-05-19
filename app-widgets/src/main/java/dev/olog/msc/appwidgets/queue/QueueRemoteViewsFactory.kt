package dev.olog.msc.appwidgets.queue

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import dev.olog.msc.appwidgets.R
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.PlayingQueueSong
import dev.olog.msc.core.gateway.PlayingQueueGateway
import dev.olog.msc.imageprovider.glide.getBitmap
import dev.olog.msc.shared.MusicConstants
import javax.inject.Inject

class QueueRemoteViewsFactory @Inject constructor(
    @ApplicationContext private val context: Context,
    private val playingQueueGateway: PlayingQueueGateway

) : RemoteViewsService.RemoteViewsFactory {

    private val dataSet = mutableListOf<WidgetItem>()

    override fun onCreate() {
        onDataSetChanged()
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun onDataSetChanged() {
        // TODO
    }
//        val data = playingQueueGateway.get().blockingGet()
//        this.dataSet.clear()
//        this.dataSet.addAll(data.map { it.toWidgetItem() })
//    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getItemId(position: Int): Long {
        return -1
    }

    override fun onDestroy() {
    }

    override fun getViewAt(position: Int): RemoteViews {
        val item = dataSet[position]
        val removeViews = RemoteViews(context.packageName, R.layout.widget_colored_with_queue_item)
        removeViews.setTextViewText(R.id.firstText, item.title)
        removeViews.setTextViewText(R.id.secondText, item.subtitle)

        val extras = bundleOf(MusicConstants.EXTRA_SKIP_TO_ITEM_ID to item.idInPlaylist)
        val fillIntent = Intent().also { it.putExtras(extras) }
        removeViews.setOnClickFillInIntent(R.id.root, fillIntent)
        val bitmap = context.getBitmap(item.mediaId, 100)
        removeViews.setImageViewBitmap(R.id.cover, bitmap)

        return removeViews
    }

    override fun getCount(): Int = dataSet.size

    override fun getViewTypeCount(): Int {
        return 1
    }

    private class WidgetItem(
        val id: Long,
        val idInPlaylist: Int,
        val mediaId: MediaId,
        val title: String,
        val subtitle: String
    )

    private fun PlayingQueueSong.toWidgetItem(): WidgetItem {
        val mediaId = if (this.isPodcast) {
            MediaId.podcastId(this.id)
        } else {
            MediaId.songId(this.id)
        }

        return WidgetItem(
            this.id,
            this.trackNumber,
            mediaId,
            this.title,
            this.artist
        )
    }

}