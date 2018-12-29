plugins {
    id("org.jetbrains.kotlin.jvm").version("1.3.11")
    application
}

repositories {
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}

application {
    mainClassName = "net.harshtuna.processmining.AppKt"
}
