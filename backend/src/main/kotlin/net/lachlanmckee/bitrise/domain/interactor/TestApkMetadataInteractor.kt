package net.lachlanmckee.bitrise.domain.interactor

import com.google.gson.JsonObject
import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import net.lachlanmckee.bitrise.data.datasource.remote.BitriseDataSource
import net.lachlanmckee.bitrise.domain.mapper.TestApkMetadataMapper

class TestApkMetadataInteractor(
    private val bitriseDataSource: BitriseDataSource,
    private val testApkMetadataMapper: TestApkMetadataMapper
) {
    suspend fun execute(
        call: ApplicationCall,
        buildSlug: String,
        artifactSlug: String
    ) {
        println("Fetching test apk data for build[$buildSlug] and artifact[$artifactSlug]")

        bitriseDataSource
            .getArtifact(buildSlug, artifactSlug)
            .onSuccess { artifactData ->
                fetchTestData(
                    call,
                    (artifactData as JsonObject).getAsJsonObject("data").get("expiring_download_url").asString
                )
            }
            .onFailure { println("Failure: $it") }
    }

    private suspend fun fetchTestData(
        call: ApplicationCall,
        testApkUrl: String
    ) {
        bitriseDataSource
            .getTestApkTestMethods(testApkUrl)
            .mapCatching {
                val startTime = System.currentTimeMillis()
                testApkMetadataMapper.mapTestApkMetadata(it)
                    .also {
                        println("Mapping finished. Time: ${System.currentTimeMillis() - startTime}")
                    }
            }
            .onSuccess { call.respond(it) }
            .onFailure { println("Failure: $it") }
    }
}
