plugins {
    java
}

group = "com.dsa"
version = "0.0.2-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Основные зависимости
    implementation("io.rest-assured:rest-assured:5.5.5")
    implementation("org.telegram:telegrambots:6.9.7.1")
    implementation("com.google.code.gson:gson:2.13.1")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")

    // Тесты
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

// Fat jar + Main-Class
tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.dsa.Main"
    }

    from({
        configurations.runtimeClasspath.get().map {
            if (it.isDirectory) it else zipTree(it)
        }
    })

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
