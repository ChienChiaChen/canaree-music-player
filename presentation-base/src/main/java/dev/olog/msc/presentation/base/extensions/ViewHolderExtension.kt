@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.msc.presentation.base.extensions

import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import dev.olog.msc.presentation.base.R
import dev.olog.msc.presentation.base.adapter.BasePagedAdapter
import dev.olog.msc.presentation.base.animation.ScaleInOnTouch
import dev.olog.msc.presentation.base.animation.ScaleMoreInOnTouch
import dev.olog.msc.presentation.base.model.BaseModel

inline fun RecyclerView.ViewHolder.elevateAlbumOnTouch() {
    itemView.setOnTouchListener(ScaleMoreInOnTouch(itemView))
}

inline fun RecyclerView.ViewHolder.elevateSongOnTouch() {
    val viewToAnimate = itemView.findViewById<View>(R.id.root)?.let { it } ?: itemView
    itemView.setOnTouchListener(ScaleInOnTouch(viewToAnimate))
}

//////////////////////////////////////

fun <T : BaseModel> RecyclerView.ViewHolder.setOnClickListener(
    data: BasePagedAdapter<T>,
    func: (item: T, position: Int, view: View) -> Unit
) {

    this.itemView.setOnClickListener {
        if (adapterPosition != RecyclerView.NO_POSITION) {
            data.tryGetItem(adapterPosition)?.let { model -> func(model, adapterPosition, it) }
        }
    }
}

fun <T : BaseModel> RecyclerView.ViewHolder.setOnClickListener(
    @IdRes resId: Int,
    data: BasePagedAdapter<T>,
    func: (item: T, position: Int, view: View) -> Unit
) {

    this.itemView.findViewById<View>(resId)?.setOnClickListener {
        if (adapterPosition != RecyclerView.NO_POSITION) {
            data.tryGetItem(adapterPosition)?.let { model -> func(model, adapterPosition, it) }
        }
    }
}

fun <T : BaseModel> RecyclerView.ViewHolder.setOnLongClickListener(
    data: BasePagedAdapter<T>,
    func: (item: T, position: Int, view: View) -> Unit
) {

    itemView.setOnLongClickListener inner@{
        if (adapterPosition != RecyclerView.NO_POSITION) {
            data.tryGetItem(adapterPosition)?.let { model -> func(model, adapterPosition, it) } ?: return@inner false
            return@inner true
        }
        false
    }
}