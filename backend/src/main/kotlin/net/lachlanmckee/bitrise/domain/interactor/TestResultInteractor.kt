package net.lachlanmckee.bitrise.domain.interactor

import net.lachlanmckee.bitrise.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.data.datasource.remote.BitriseDataSource
import net.lachlanmckee.bitrise.domain.entity.TestResultModel

class TestResultInteractor(
    private val bitriseDataSource: BitriseDataSource,
    private val configDataSource: ConfigDataSource
) {
    suspend fun execute(buildSlug: String): Result<TestResultModel> {
        return bitriseDataSource
            .getArtifactDetails(buildSlug)
            .mapCatching { artifactDetails ->
                println(artifactDetails)
                val costReportArtifact = artifactDetails
                    .data
                    .first { it.title == "CostReport.txt" }

                val artifact = bitriseDataSource
                    .getArtifact(buildSlug, costReportArtifact.slug)
                    .getOrThrow()

                val cost = bitriseDataSource
                    .getArtifactText(artifact.expiringDownloadUrl)
                    .getOrThrow()

                TestResultModel(cost)
            }
    }
}
