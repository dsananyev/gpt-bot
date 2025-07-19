plugins {
    java
}

group = "com.dsa"
version = "0.0.5-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Основные зависимости
    implementation("com.squareup.okhttp3:okhttp:5.1.0")
    implementation("org.telegram:telegrambots:6.9.7.1")
    implementation("com.google.code.gson:gson:2.13.1")
    implementation("ch.qos.logback:logback-classic:1.4.14")


    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")

    //redis
    implementation("redis.clients:jedis:6.0.0")

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
