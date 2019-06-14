package dev.olog.msc.core.gateway

import dev.olog.msc.core.entity.Genre

interface GenreGateway  :
        BaseGateway<Genre, Long>,
    ChildsHasSongs<Long>,
    HasMostPlayed