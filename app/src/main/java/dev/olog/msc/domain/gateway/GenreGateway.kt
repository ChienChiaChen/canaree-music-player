package dev.olog.msc.domain.gateway

import dev.olog.msc.core.entity.Genre
import dev.olog.msc.core.gateway.BaseGateway
import dev.olog.msc.core.gateway.ChildsHasSongs

interface GenreGateway  :
        BaseGateway<Genre, Long>,
    ChildsHasSongs<Long>,
        HasMostPlayed