package net.lachlanmckee.flankci.integration.bitrise

import com.google.gson.TypeAdapterFactory
import gsonpath.GsonFieldValidationType
import gsonpath.annotation.AutoGsonAdapterFactory

@AutoGsonAdapterFactory(
  fieldValidationType = GsonFieldValidationType.VALIDATE_EXPLICIT_NON_NULL
)
internal interface BitriseIntegrationGsonTypeFactory : TypeAdapterFactory
