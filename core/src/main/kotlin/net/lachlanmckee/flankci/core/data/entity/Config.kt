package net.lachlanmckee.flankci.core.data.entity

import com.google.gson.JsonObject
import java.util.*

class Config(
  configModel: ConfigModel,
  secretProperties: Properties
) {
  val ci: JsonObject = configModel.ci
  val testData: ConfigModel.TestData = configModel.testData
  val bitriseToken: String = secretProperties.getProperty("bitriseToken")
}
