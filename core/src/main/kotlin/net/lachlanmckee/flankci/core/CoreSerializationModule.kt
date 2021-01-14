package net.lachlanmckee.flankci.core

import com.google.gson.TypeAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.multibindings.ElementsIntoSet
import gsonpath.GsonPath
import gsonpath.GsonPathTypeAdapterFactoryKt
import net.lachlanmckee.flankci.core.data.serialization.FlankCIGsonTypeFactory

@Module
object CoreSerializationModule {
  @Provides
  @ElementsIntoSet
  internal fun provideGsonTypeAdapterFactories(): Set<@JvmSuppressWildcards TypeAdapterFactory> {
    return setOf(
      GsonPathTypeAdapterFactoryKt(),
      GsonPath.createTypeAdapterFactory(FlankCIGsonTypeFactory::class.java)
    )
  }
}
