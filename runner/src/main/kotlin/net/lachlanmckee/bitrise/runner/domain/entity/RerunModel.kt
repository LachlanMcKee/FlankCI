package net.lachlanmckee.bitrise.runner.domain.entity

data class RerunModel(
    val failedTests: List<String>
)
