package dev.olog.msc.core.gateway.podcast

import dev.olog.msc.core.entity.podcast.PodcastAlbum
import dev.olog.msc.core.gateway.base.*

interface PodcastAlbumGateway :
    BaseGateway<PodcastAlbum, Long>,
    ChildsHasPodcasts<Long>,
    HasLastPlayed<PodcastAlbum>,
    HasRecentlyAdded<PodcastAlbum>,
    HasSiblings<PodcastAlbum>