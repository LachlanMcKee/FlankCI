package net.lachlanmckee.bitrise.runner.presentation

import io.ktor.application.*
import io.ktor.html.*
import kotlinx.html.*
import net.lachlanmckee.bitrise.core.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.core.data.entity.ConfigModel

internal class TestRunnerScreen(private val configDataSource: ConfigDataSource) {
    suspend fun respondHtml(call: ApplicationCall, tests: List<String>?) {
        val options: List<ConfigModel.Option> = configDataSource.getConfig().testData.options
        call.respondHtml {
            head {
                link(rel = "stylesheet", href = "/static/styles.css", type = "text/css")
            }
            body {
                h1 { +"Bitrise Test Runner" }
                form("/trigger-tests", encType = FormEncType.multipartFormData, method = FormMethod.post) {
                    acceptCharset = "utf-8"
                    target = "_blank"

                    div {
                        classes = setOf("test-runner-group")
                        label {
                            classes = setOf("inline-label")
                            htmlFor = "branch-select"
                            text("Branch: ")
                        }
                        select {
                            id = "branch-select"
                            name = "branch-select"
                        }
                    }

                    div {
                        classes = setOf("test-runner-group")
                        label {
                            classes = setOf("inline-label")
                            htmlFor = "build-select"
                            text("Build: ")
                        }
                        select {
                            id = "build-select"
                            name = "build-select"
                        }
                    }

                    div {
                        classes = setOf("test-runner-group")
                        p {
                            classes = setOf("heading")
                            text("Artifacts:")
                        }
                        p {
                            classes = setOf("content")
                            id = "artifact-details"
                        }
                    }

                    this@body.addOptions(options)

                    input {
                        id = "root-package"
                        name = "rootPackage"
                        type = InputType.hidden
                    }

                    input {
                        id = "build-slug"
                        name = "buildSlug"
                        type = InputType.hidden
                    }

                    input {
                        id = "commit-hash"
                        name = "commitHash"
                        type = InputType.hidden
                    }

                    if (tests == null) {
                        this@body.addTestRunOptions()
                    } else {
                        this@body.addTestRerunOptions(tests)
                    }

                    div {
                        p {
                            classes = setOf("test-runner-group", "heading")
                        }
                        p {
                            classes = setOf("content")
                            submitInput { value = "Trigger Tests" }
                        }
                    }
                }
                script(src = "/static/test-runner-script.js") {
                }
            }
        }
    }

    private fun BODY.addTestRunOptions() {
        div {
            classes = setOf("test-runner-group")
            p {
                classes = setOf("heading")
            }
            p {
                classes = setOf("content")
                button {
                    id = "load-test-data-button"
                    type = ButtonType.button
                    text("Load Test Data")
                }
            }
        }

        p {
            classes = setOf("heading")
            text("Annotations")
        }

        div {
            classes = setOf("test-runner-group", "annotations", "data-list", "content")
            fieldSet {
                id = "annotations-field-set"
            }
        }

        p {
            id = "packages-heading"
            classes = setOf("heading")
            text("Packages")
        }

        div {
            classes = setOf("test-runner-group", "packages", "data-list", "content")
            fieldSet {
                id = "packages-field-set"
            }
        }

        p {
            id = "classes-heading"
            classes = setOf("heading")
            text("Classes")
        }

        div {
            classes = setOf("test-runner-group", "classes", "data-list", "content")
            fieldSet {
                id = "classes-field-set"
            }
        }
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

    private fun BODY.addOptions(options: List<ConfigModel.Option>) {
        options.forEachIndexed { index, option ->
            when (option) {
                is ConfigModel.Option.Checkbox -> {
                    div {
                        classes = setOf("test-runner-group")
                        label {
                            classes = setOf("inline-label")
                            htmlFor = "option-checkbox-$index"
                            text("${option.label}:")
                        }
                        hiddenInput {
                            id = "option-checkbox-$index-hidden"
                            name = "option-checkbox-$index-hidden"
                            value = "off"
                        }
                        checkBoxInput {
                            id = "option-checkbox-$index"
                            name = "option-checkbox-$index"
                        }
                    }
                }
                is ConfigModel.Option.DropDown -> {
                    div {
                        classes = setOf("test-runner-group")
                        label {
                            classes = setOf("inline-label")
                            htmlFor = "option-drop-down-$index"
                            text("${option.label}:")
                        }
                        select {
                            id = "option-drop-down-$index"
                            name = "option-drop-down-$index"

                            option.values.forEachIndexed { index, optionValue ->
                                option {
                                    value = "$index"
                                    text(optionValue.label)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
