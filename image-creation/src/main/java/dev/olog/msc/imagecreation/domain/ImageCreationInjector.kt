package dev.olog.msc.imagecreation.domain

import dagger.Binds
import dagger.Module
import dev.olog.msc.imagecreation.IImageCreator
import dev.olog.msc.imagecreation.ImagesCreator
import javax.inject.Singleton

@Module
abstract class ImageCreationInjector {

    @Binds
    @Singleton
    internal abstract fun provideImageCreator(impl: ImagesCreator): IImageCreator

}