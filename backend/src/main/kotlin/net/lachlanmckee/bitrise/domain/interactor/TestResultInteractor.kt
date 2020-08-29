package net.lachlanmckee.bitrise.domain.interactor

import net.lachlanmckee.bitrise.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.data.datasource.remote.BitriseDataSource
import net.lachlanmckee.bitrise.data.entity.BitriseArtifactsListResponse
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

                TestResultModel(
                    cost = getText(artifactDetails, buildSlug,  "CostReport.txt"),
                    junit = getText(artifactDetails, buildSlug,  "JUnitReport.xml"),
                    matrixIds = getText(artifactDetails, buildSlug,  "matrix_ids.json")
                )
            }
    }

    private suspend fun getText(artifactDetails: BitriseArtifactsListResponse, buildSlug: String, fileName: String): String {
        val artifactDetail = artifactDetails
            .data
            .first { it.title == fileName }

        val artifact = bitriseDataSource
            .getArtifact(buildSlug, artifactDetail.slug)
            .getOrThrow()

        return bitriseDataSource
            .getArtifactText(artifact.expiringDownloadUrl)
            .getOrThrow()
    }
}
