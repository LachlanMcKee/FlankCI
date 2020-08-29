package net.lachlanmckee.bitrise.presentation

import io.ktor.application.ApplicationCall
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.http.withCharset
import io.ktor.response.respond
import io.ktor.util.cio.bufferedWriter
import io.ktor.utils.io.ByteWriteChannel
import net.lachlanmckee.bitrise.domain.interactor.TestResultInteractor

class HtmlContent(private val htmlText: String) : OutgoingContent.WriteChannelContent() {

    override val status: HttpStatusCode?
        get() = HttpStatusCode.OK

    override val contentType: ContentType
        get() = ContentType.Text.Html.withCharset(Charsets.UTF_8)

    override suspend fun writeTo(channel: ByteWriteChannel) {
        channel.bufferedWriter().use {
            it.append("<!DOCTYPE html>\n")
            it.append(htmlText)
        }
    }
}

class TestResultScreen(
    private val testResultInteractor: TestResultInteractor,
    private val errorScreenFactory: ErrorScreenFactory
) {
    suspend fun respondHtml(call: ApplicationCall, buildSlug: String) {
       testResultInteractor
            .execute(buildSlug)
            .onSuccess {
                call.respond(it.matrixIds)
            }
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
