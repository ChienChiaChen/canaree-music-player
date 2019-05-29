package dev.olog.msc.presentation.categories.di

import dev.olog.msc.app.injection.coreComponent
import dev.olog.msc.presentation.categories.podcast.CategoriesPodcastFragment
import dev.olog.msc.presentation.categories.track.CategoriesFragment

fun CategoriesFragment.inject() {
    DaggerCategoriesFragmentComponent.factory()
        .create(requireActivity().coreComponent())
        .inject(this)
}

fun CategoriesPodcastFragment.inject() {
    DaggerCategoriesFragmentComponent.factory()
        .create(requireActivity().coreComponent())
        .inject(this)
}
