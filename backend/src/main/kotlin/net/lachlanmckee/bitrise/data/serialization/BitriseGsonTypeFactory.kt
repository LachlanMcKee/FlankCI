package net.lachlanmckee.bitrise.data.serialization

import com.google.gson.TypeAdapterFactory
import gsonpath.GsonFieldValidationType
import gsonpath.annotation.AutoGsonAdapterFactory

@AutoGsonAdapterFactory(
    fieldValidationType = GsonFieldValidationType.VALIDATE_EXPLICIT_NON_NULL
)
interface BitriseGsonTypeFactory : TypeAdapterFactory
