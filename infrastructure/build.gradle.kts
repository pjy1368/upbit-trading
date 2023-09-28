import org.jooq.meta.jaxb.Generate
import org.jooq.meta.jaxb.Property

plugins {
    id("nu.studer.jooq") version "8.1"
}

buildscript {
    configurations["classpath"].resolutionStrategy.eachDependency {
        if (requested.group == "org.jooq") {
            useVersion("3.18.0")
        }
    }
}

val dbName = "upbit"

sourceSets {
    main {
        kotlin {
            srcDirs("src/generated/$dbName")
        }
    }
}

dependencies {
    implementation(project(":domain"))

    implementation("org.postgresql:r2dbc-postgresql")
    implementation("io.r2dbc:r2dbc-pool")
    implementation("org.postgresql:postgresql")
    implementation("org.jooq:jooq:3.18.0")
    implementation("org.jooq:jooq-kotlin-coroutines:3.18.0")
    implementation("org.jooq:jooq-kotlin:3.18.0")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    jooqGenerator("org.postgresql:postgresql")
}

jooq {
    version.set("3.18.0")

    configurations {

        fun Generate.setDefaults() {
            isUdts = true
            isDeprecated = false
            isRecords = true
            isFluentSetters = true

            isPojosAsKotlinDataClasses = false
            isImmutablePojos = false
            isPojos = false

            // cause https://github.com/jOOQ/jOOQ/issues/14785
            isRecordsImplementingRecordN = false
            isKotlinNotNullRecordAttributes = true
            isKotlinNotNullPojoAttributes = true
            isKotlinNotNullInterfaceAttributes = true
        }

        create(dbName) {
            generateSchemaSourceOnCompilation.set(false)
            jooqConfiguration.apply {
                logging = org.jooq.meta.jaxb.Logging.WARN
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = "jdbc:postgresql://localhost:5432/$dbName"
                    user = "jooq"
                    password = "jooq123!"
                    properties = listOf(
                        Property().apply {
                            key = "PAGE_SIZE"
                            value = "2048"
                        }
                    )
                }
                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"
                    strategy.apply {  }
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                        includes = ".*"
                        excludes = "flyway_schema_history"
                    }
                    generate.setDefaults()
                    target.apply {
                        packageName = "com.jayon.$dbName"
                        directory = "src/generated/$dbName"
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}