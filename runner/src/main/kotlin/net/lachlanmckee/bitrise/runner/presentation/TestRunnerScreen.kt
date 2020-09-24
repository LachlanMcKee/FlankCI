package net.lachlanmckee.bitrise.runner.presentation

import io.ktor.application.ApplicationCall
import io.ktor.html.respondHtml
import kotlinx.html.*
import net.lachlanmckee.bitrise.core.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.core.data.entity.ConfigModel

internal class TestRunnerScreen(private val configDataSource: ConfigDataSource) {
    suspend fun respondHtml(call: ApplicationCall) {
        val options: List<ConfigModel.Option> = configDataSource.getConfig().testData.options
        call.respondHtml {
            head {
                link(rel="stylesheet", href="https://fonts.googleapis.com/icon?family=Material+Icons")
                link(rel="stylesheet", href="https://code.getmdl.io/1.3.0/material.indigo-pink.min.css")
                script {
                    src = "https://code.getmdl.io/1.3.0/material.min.js"
                }
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
                            classes = setOf("mdl-textfield__input")
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
                            classes = setOf("mdl-textfield__input")
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
                                classes = setOf("mdl-button mdl-button--colored", "mdl-js-button", "mdl-js-ripple-effect", "green-button")
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
                        id = "build-slug"
                        name = "buildSlug"
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
                            submitInput {
                                classes = setOf("mdl-button mdl-button--colored", "mdl-js-button", "mdl-js-ripple-effect", "green-button")
                                value = "Trigger Tests" }
                        }
                    }
                }
                script(src = "/static/test-runner-script.js") {
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
                            classes = setOf("mdl-textfield__input")
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
