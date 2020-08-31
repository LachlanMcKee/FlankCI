package net.lachlanmckee.bitrise.results.presentation

import io.ktor.application.ApplicationCall
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.http.withCharset
import io.ktor.util.cio.bufferedWriter
import io.ktor.utils.io.ByteWriteChannel
import kotlinx.html.*
import net.lachlanmckee.bitrise.core.presentation.ErrorScreenFactory
import net.lachlanmckee.bitrise.results.domain.entity.TestResultModel
import net.lachlanmckee.bitrise.results.domain.interactor.TestResultInteractor

internal class HtmlContent(private val htmlText: String) : OutgoingContent.WriteChannelContent() {

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

internal class TestResultScreen(
    private val testResultInteractor: TestResultInteractor,
    private val errorScreenFactory: ErrorScreenFactory
) {
    suspend fun respondHtml(call: ApplicationCall, buildSlug: String) {
        testResultInteractor
            .execute(buildSlug)
            .onSuccess { render(call, it) }
            .onFailure { errorScreenFactory.respondHtml(call, "Failed to parse content", it.message!!) }
    }

    private suspend fun render(
        call: ApplicationCall,
        resultModel: TestResultModel
    ) {
        call.respondHtml {
            head {
                link(rel = "stylesheet", href = "/static/styles.css", type = "text/css")
            }
            body {
                h1 { +"Bitrise Test Result" }
                div {
                    span {
                        classes = setOf("heading")
                    }
                    span {
                        classes = setOf("content")
                        b {
                            text(resultModel.cost)
                        }
                    }
                }
                resultModel.testSuites.testsuite.forEach { testSuite ->
                    div {
                        p {
                            classes = setOf("heading")
                        }
                        p {
                            classes = setOf("content")
                            b {
                                text("${testSuite.name}. Success: ${testSuite.tests - testSuite.failures}/${testSuite.tests}, Time: ${testSuite.time}")
                            }
                        }
                    }
                    testSuite.testcase.forEach { testCase ->
                        div {
                            span {
                                classes = setOf("heading")
                                if (testCase.failure != null) {
                                    text("Failure")
                                } else {
                                    text("Success")
                                }
                            }
                            span {
                                classes = if (testCase.failure != null) {
                                    setOf("content", "test-failure")
                                } else {
                                    setOf("content", "test-success")
                                }
                                text("${testCase.classname}#${testCase.name}")
                                br()
                                a(href = testCase.webLink) {
                                    target = "_blank"
                                    text("Open in Firebase")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
