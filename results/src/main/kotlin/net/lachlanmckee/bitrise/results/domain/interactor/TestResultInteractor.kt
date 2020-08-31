package net.lachlanmckee.bitrise.results.domain.interactor

import net.lachlanmckee.bitrise.core.data.datasource.remote.BitriseDataSource
import net.lachlanmckee.bitrise.core.data.entity.BitriseArtifactsListResponse
import net.lachlanmckee.bitrise.results.domain.entity.TestResultModel
import net.lachlanmckee.bitrise.results.domain.mapper.TestSuitesMapper
import javax.inject.Inject

internal class TestResultInteractor @Inject constructor(
    private val bitriseDataSource: BitriseDataSource,
    private val testSuitesMapper: TestSuitesMapper
) {
    suspend fun execute(buildSlug: String): Result<TestResultModel> {
        return bitriseDataSource
            .getArtifactDetails(buildSlug)
            .mapCatching { artifactDetails ->
                println(artifactDetails)

                TestResultModel(
                    cost = getArtifactText(artifactDetails, buildSlug, "CostReport.txt"),
                    testSuites = testSuitesMapper.mapTestSuites(
                        getArtifactText(
                            artifactDetails,
                            buildSlug,
                            "JUnitReport.xml"
                        )
                    ),
                    matrixIds = getArtifactText(artifactDetails, buildSlug, "matrix_ids.json")
                )
            }
    }

    private suspend fun getArtifactText(
        artifactDetails: BitriseArtifactsListResponse,
        buildSlug: String,
        fileName: String
    ): String {
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
