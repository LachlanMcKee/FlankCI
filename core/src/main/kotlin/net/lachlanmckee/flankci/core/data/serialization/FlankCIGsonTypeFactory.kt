package net.lachlanmckee.flankci.core.data.serialization

import com.google.gson.TypeAdapterFactory
import gsonpath.GsonFieldValidationType
import gsonpath.annotation.AutoGsonAdapterFactory

@AutoGsonAdapterFactory(
  fieldValidationType = GsonFieldValidationType.VALIDATE_EXPLICIT_NON_NULL
)
internal interface FlankCIGsonTypeFactory : TypeAdapterFactory
