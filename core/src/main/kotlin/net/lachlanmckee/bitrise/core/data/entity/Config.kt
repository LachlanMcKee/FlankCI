package net.lachlanmckee.bitrise.core.data.entity

import java.util.*

class Config(
  configModel: ConfigModel,
  secretProperties: Properties
) {
  val bitrise: ConfigModel.Bitrise = configModel.bitrise
  val testData: ConfigModel.TestData = configModel.testData
  val bitriseToken: String = secretProperties.getProperty("bitriseToken")
}
