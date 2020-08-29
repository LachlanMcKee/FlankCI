package net.lachlanmckee.bitrise.presentation

import io.ktor.application.ApplicationCall
import io.ktor.html.respondHtml
import io.ktor.response.respond
import kotlinx.html.*
import net.lachlanmckee.bitrise.data.entity.BuildsData
import net.lachlanmckee.bitrise.domain.interactor.TestResultInteractor

class TestResultScreen(
    private val testResultInteractor: TestResultInteractor,
    private val errorScreenFactory: ErrorScreenFactory
) {
    suspend fun respondHtml(call: ApplicationCall, buildSlug: String) {
       testResultInteractor
            .execute(buildSlug)
            .onSuccess { call.respond(it) }
            .onFailure { errorScreenFactory.respondHtml(call, "Failed to parse content", it.message!!) }
    }
//
//    private suspend fun render(
//        call: ApplicationCall,
//        buildsData: BuildsData
//    ) {
//        call.respondHtml {
//            head {
//                link(rel = "stylesheet", href = "/static/styles.css", type = "text/css")
//            }
//            body {
//                h1 { +"Bitrise Test Results" }
//                div {
//                    p {
//                        classes = setOf("heading")
//                        text("Jobs:")
//                    }
//                    p {
//                        classes = setOf("content")
//                        id = "artifact-details"
//                        text(buildsData.branchBuilds.map { it.value.toString() }.joinToString(separator = "<br/>"))
//                    }
//                }
//            }
//        }
//    }
}
