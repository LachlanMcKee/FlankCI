package net.lachlanmckee.bitrise.domain.mapper

import com.linkedin.dex.parser.TestMethod
import net.lachlanmckee.bitrise.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.data.entity.Config
import net.lachlanmckee.bitrise.domain.entity.PathWithAnnotationGroups
import net.lachlanmckee.bitrise.domain.entity.TestApkMetadata

class TestApkMetadataMapper(private val configDataSource: ConfigDataSource) {
    suspend fun mapTestApkMetadata(originalTestMethods: List<TestMethod>): TestApkMetadata {
        val config = configDataSource.getConfig()
        val testMethods: List<TestMethod> = getValidTestMethods(config, originalTestMethods)
        val packages: List<String> = getPackages(testMethods)

        val rootPackage: String = findRootPackage(packages)
        val classNamesWithAnnotations: List<PathWithAnnotationGroups> =
            getClassNamesWithAnnotations(config, testMethods, rootPackage)

        val packagesWithAnnotations: List<PathWithAnnotationGroups> = getPackagesWithAnnotations(
            packages,
            classNamesWithAnnotations,
            rootPackage
        )

        val annotations: List<String> = classNamesWithAnnotations
            .flatMap { method -> method.annotationGroups?.flatten() ?: emptyList() }
            .distinct()
            .sorted()

        return TestApkMetadata(
            rootPackage = rootPackage,
            annotations = annotations,
            packages = packagesWithAnnotations,
            classes = classNamesWithAnnotations.sortedBy { it.path }
        )
    }

    private fun getValidTestMethods(
        config: Config,
        originalTestMethods: List<TestMethod>
    ): List<TestMethod> {
        return originalTestMethods
            .sortedBy { testMethod -> testMethod.testName }
            .filter { testMethod ->
                config.testData.ignoreTestsWithAnnotations
                    .intersect(testMethod.annotations.map { it.name })
                    .isEmpty()
            }
    }

    private fun getPackages(testMethods: List<TestMethod>): List<String> {
        return testMethods
            .map { method ->
                val testNameSplit = method
                    .testName
                    .split(".")

                testNameSplit
                    .take(testNameSplit.lastIndex)
                    .joinToString(separator = ".")
            }
            .distinct()
    }

    private fun getClassNamesWithAnnotations(
        config: Config,
        testMethods: List<TestMethod>,
        rootPackage: String
    ): List<PathWithAnnotationGroups> {
        val classNamesWithMethods: List<PathWithAnnotationGroups> = testMethods
            .map { method ->
                PathWithAnnotationGroups(
                    path = method.testName.removeRange(0, rootPackage.length + 1),
                    annotationGroups = getAnnotations(config, method)?.let { listOf(it) }
                )
            }

        // Append the class itself without the method name so that all methods can be run.
        return classNamesWithMethods
            .plus(
                classNamesWithMethods
                    .map { it to it.path.substringBefore('#') }
                    .groupBy { (_, path) -> path }
                    .map { (path, pathAndClassMethods) ->
                        val annotationGroups: List<List<String>> =
                            pathAndClassMethods.flatMap { it.first.annotationGroups ?: emptyList() }

                        PathWithAnnotationGroups(
                            path = path,
                            annotationGroups = annotationGroups
                        )
                    }
            )
    }

    private fun getPackagesWithAnnotations(
        packagesWithPrefix: List<String>,
        classNamesWithAnnotations: List<PathWithAnnotationGroups>,
        rootPackage: String
    ): List<PathWithAnnotationGroups> {
        return packagesWithPrefix.map { pathWithAnnotation ->
            val packagePath = if (pathWithAnnotation != rootPackage) {
                pathWithAnnotation.removeRange(0, rootPackage.length + 1)
            } else {
                ""
            }

            // Find all classes within this package and collate their annotations.
            val annotations = classNamesWithAnnotations
                .flatMap {
                    if (it.path.startsWith(packagePath) && it.annotationGroups != null) {
                        it.annotationGroups
                    } else {
                        emptyList()
                    }
                }
                .distinct()

            PathWithAnnotationGroups(
                path = packagePath,
                annotationGroups = annotations.takeIf { it.isNotEmpty() }
            )
        }
    }

    private fun findRootPackage(sortedPackages: List<String>): String {
        val size: Int = sortedPackages.size

        if (size == 0) return ""
        if (size == 1) return sortedPackages[0]

        val end: Int = sortedPackages[0].length.coerceAtMost(sortedPackages.last().length)

        var i = 0
        while (i < end && sortedPackages[0][i] == sortedPackages[size - 1][i]) {
            i++
        }

        return sortedPackages[0].substring(0, i).removeSuffix(".")
    }

    private fun getAnnotations(
        config: Config,
        testMethod: TestMethod
    ): List<String>? {
        return testMethod.annotations
            .asSequence()
            .map { annotation -> annotation.name }
            .filter { name ->
                name != "dalvik.annotation.SourceDebugExtension" &&
                        name != "kotlin.Metadata" &&
                        name != "org.junit.Test"
            }
            .filter { name ->
                !config.testData.hiddenAnnotations.contains(name) &&
                        !config.testData.ignoreTestsWithAnnotations.contains(name)
            }
            .toList()
            .let {
                if (it.isNotEmpty()) {
                    it
                } else {
                    null
                }
            }
    }
}