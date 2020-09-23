package net.lachlanmckee.bitrise.runner.presentation

import io.ktor.application.*
import kotlinx.html.*
import net.lachlanmckee.bitrise.core.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.core.presentation.ErrorScreenFactory
import net.lachlanmckee.bitrise.runner.domain.interactor.TestRerunInteractor

internal class TestRerunScreen(
    private val configDataSource: ConfigDataSource,
    private val errorScreenFactory: ErrorScreenFactory,
    private val testRerunInteractor: TestRerunInteractor
) {
    private val delegate by lazy {
        TestRunnerScreenDelegate(configDataSource)
    }

    suspend fun respondHtml(call: ApplicationCall, buildSlug: String) {
        testRerunInteractor
            .execute(buildSlug)
            .onSuccess {
                delegate.respondHtml(call) {
                    addTestRerunOptions(it.failedTests)
                }
            }
            .onFailure { errorScreenFactory.respondHtml(call, "Failed to parse content", it.message!!) }

    }

    private fun BODY.addTestRerunOptions(tests: List<String>) {
        p {
            id = "classes-heading"
            classes = setOf("heading")
            text("Classes")
        }

        div {
            classes = setOf("test-runner-group", "classes", "data-list", "content")
            fieldSet {
                id = "classes-field-set"

                tests.forEachIndexed { index, test ->
                    div {
                        input {
                            placeholder = "full_class$index"
                            type = InputType.checkBox
                            id = "full_class$index"
                            name = "full_class"
                            value = test
                        }
                        label {
                            htmlFor = "full_class$index"
                            text(test)
                        }
                    }
                }
            }
        }
    }
}
