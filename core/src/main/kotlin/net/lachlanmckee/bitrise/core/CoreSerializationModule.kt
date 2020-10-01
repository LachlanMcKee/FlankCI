package net.lachlanmckee.bitrise.core

import com.google.gson.TypeAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.multibindings.ElementsIntoSet
import gsonpath.GsonPath
import gsonpath.GsonPathTypeAdapterFactoryKt
import net.lachlanmckee.bitrise.core.data.serialization.BitriseGsonTypeFactory

@Module
object CoreSerializationModule {
  @Provides
  @ElementsIntoSet
  internal fun provideGsonTypeAdapterFactories(): Set<@JvmSuppressWildcards TypeAdapterFactory> {
    return setOf(
      GsonPathTypeAdapterFactoryKt(),
      GsonPath.createTypeAdapterFactory(BitriseGsonTypeFactory::class.java)
    )
  }
}
