package dev.olog.msc.shared

object MusicConstants {

    private const val TAG = "MusicConstants"
    const val ACTION_PLAY = "$TAG.shortcut.play"
    const val ACTION_SHUFFLE = "$TAG.action.play_shuffle"
    const val ACTION_PLAY_FROM_URI = "$TAG.action.play_uri"
    const val ACTION_PLAY_PAUSE = "$TAG.action.play"
    const val ACTION_SKIP_NEXT = "$TAG.action.skip.next"
    const val ACTION_SKIP_PREVIOUS = "$TAG.action.skip.previous"
    const val ACTION_SKIP_TO_ITEM = "$TAG.action.skip.to.item"
    const val EXTRA_SKIP_TO_ITEM_ID = "$TAG.extra.skip.to.item.id"
    const val ACTION_TOGGLE_FAVORITE = "$TAG.action.toggle.favorite"

    const val ACTION_SWAP = TAG + "action.swap"
    const val ACTION_SWAP_RELATIVE = TAG + "action.swap_relative"
    const val ACTION_REMOVE = TAG + "action.remove"
    const val ACTION_REMOVE_RELATIVE = TAG + "action.remove_relative"
    const val ACTION_FORWARD_10_SECONDS = TAG + "action.forward_10_seconds"
    const val ACTION_REPLAY_10_SECONDS = TAG + "action.replay_10_seconds"
    const val ACTION_FORWARD_30_SECONDS = TAG + "action.forward_30_seconds"
    const val ACTION_REPLAY_30_SECONDS = TAG + "action.replay_30_seconds"

    const val ARGUMENT_SWAP_FROM = "$TAG.argument.swap_from"
    const val ARGUMENT_SWAP_TO = "$TAG.argument.swap_to"

    const val ARGUMENT_REMOVE_POSITION = "$TAG.argument.remove_position"

    const val BUNDLE_RECENTLY_PLAYED = "$TAG.bundle.recently.added"
    const val BUNDLE_MOST_PLAYED = "$TAG.bundle.most.played"

    const val ARGUMENT_SORT_TYPE = "$TAG.argument.sort.type"
    const val ARGUMENT_SORT_ARRANGING = "$TAG.argument.sort.arranging"

    const val EXTRA_QUEUE_CATEGORY = "$TAG.extra.queue_category"

    const val PATH = "$TAG.PATH"
    const val IS_PODCAST = "$TAG.extra.is_podcast"

}