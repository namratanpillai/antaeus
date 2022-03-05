plugins {
    kotlin("jvm")
}

kotlinProject()


dependencies {
    implementation(project(":pleo-antaeus-data"))
    implementation("org.jetbrains.exposed:exposed:0.17.7")
    implementation("org.xerial:sqlite-jdbc:3.30.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")
    
    api(project(":pleo-antaeus-models"))
}