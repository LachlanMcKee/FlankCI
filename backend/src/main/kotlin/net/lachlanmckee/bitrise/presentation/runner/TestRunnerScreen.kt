package net.lachlanmckee.bitrise.presentation.runner

import io.ktor.application.ApplicationCall
import io.ktor.html.respondHtml
import kotlinx.html.*
import net.lachlanmckee.bitrise.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.data.entity.ConfigModel

class TestRunnerScreen(private val configDataSource: ConfigDataSource) {
    suspend fun respondHtml(call: ApplicationCall) {
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

                    input {
                        id = "root-package"
                        name = "rootPackage"
                        type = InputType.hidden
                    }

                    input {
                        id = "commit-hash"
                        name = "commitHash"
                        type = InputType.hidden
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
                script(src = "/static/script.js") {
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
