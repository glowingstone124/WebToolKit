plugins {
    id("java")
}

group = "ind.glowingstone.toolkit"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("ch.qos.logback:logback-classic:1.4.11")
    implementation("org.xerial:sqlite-jdbc:3.46.1.0")
}

tasks.test {
    useJUnitPlatform()
}