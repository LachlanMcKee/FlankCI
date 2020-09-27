object Dependencies {
    const val androidDexParser = "com.linkedin.dextestparser:parser:2.2.1"
    const val logbackClassic = "ch.qos.logback:logback-classic:1.2.1"
    const val shadowVersion = "6.0.0"
    const val yamlParser = "org.yaml:snakeyaml:1.26"

    object Dagger {
        private const val version = "2.28.3"
        const val dagger = "com.google.dagger:dagger:$version"
        const val daggerCompiler = "com.google.dagger:dagger-compiler:$version"
    }
    object GsonPath {
        private const val version = "4.0.0"
        const val gsonpath = "net.lachlanmckee:gsonpath:$version"
        const val gsonpathKt = "net.lachlanmckee:gsonpath-kt:$version"
        const val gsonpathCompiler = "net.lachlanmckee:gsonpath-compiler:$version"
    }
    object Jackson {
        private const val version = "2.11.2"
        const val kotlinModule = "com.fasterxml.jackson.module:jackson-module-kotlin:$version"
        const val xml = "com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$version"
    }
    object Kotlin {
        const val version = "1.3.72"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
    }
    object Ktor {
        private const val version = "1.3.2"
        const val serverCore = "io.ktor:ktor-server-core:$version"
        const val serverNetty = "io.ktor:ktor-server-netty:$version"
        const val htmlBuilder = "io.ktor:ktor-html-builder:$version"
        const val clientCore = "io.ktor:ktor-client-core:$version"
        const val clientCoreJvm = "io.ktor:ktor-client-core-jvm:$version"
        const val clientApache = "io.ktor:ktor-client-apache:$version"
        const val gson = "io.ktor:ktor-gson:$version"
        const val clientJson = "io.ktor:ktor-client-json:$version"
        const val clientGson = "io.ktor:ktor-client-gson:$version"
        const val clientLogging = "io.ktor:ktor-client-logging:$version"
        const val clientLoggingJvm = "io.ktor:ktor-client-logging-jvm:$version"
    }
}
