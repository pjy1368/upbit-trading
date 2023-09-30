package com.jayon.upbittrading.testcontainers

import io.r2dbc.postgresql.PostgresqlConnectionFactoryProvider
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.PostgreSQLR2DBCDatabaseContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import java.time.Duration

class TestPostgreSQLContainer : PostgreSQLContainer<TestPostgreSQLContainer>("postgres:latest") {

    companion object {
        private const val USER_NAME = "test"
        private const val PASSWORD = "test"
        private const val DATABASE_NAME = "upbit"
        private const val POSTGRES_PORT = 5432

        private val kotlinLogger = mu.KotlinLogging.logger {}

        private lateinit var instance: TestPostgreSQLContainer
        private lateinit var dslContext: DSLContext
        private lateinit var connectionFactory: ConnectionFactory

        private fun getConnectionFactoryOption(): ConnectionFactoryOptions {
            return PostgreSQLR2DBCDatabaseContainer.getOptions(instance)
        }

        fun sql(sql: String) {
            dslContext.query(sql).execute()
        }

        fun start(): PostgreSQLProperty {
            if (!Companion::instance.isInitialized) {
                instance = TestPostgreSQLContainer()
                        .withLogConsumer(Slf4jLogConsumer(kotlinLogger))
                        .withUsername(USER_NAME)
                        .withPassword(PASSWORD)
                        .withStartupTimeout(Duration.ofSeconds(60))
                        .withDatabaseName(DATABASE_NAME)
                        .apply { start() }
            }
            connectionFactory = PostgresqlConnectionFactoryProvider().create(getConnectionFactoryOption())
            dslContext = DSL.using(connectionFactory)

            return PostgreSQLProperty(
                host = instance.host,
                port = instance.getMappedPort(POSTGRES_PORT),
                name = instance.databaseName,
                username = instance.username,
                password = instance.password
            )
        }

        fun stop() {
            instance.stop()
        }

        fun truncateAll() {
            dslContext.query(
                """
                CREATE OR REPLACE FUNCTION truncate_tables(username IN VARCHAR) RETURNS void AS ${'$'}${'$'}
                DECLARE
                    statements CURSOR FOR
                        SELECT tablename FROM pg_tables
                        WHERE tableowner = username AND schemaname = 'public';
                BEGIN
                    FOR stmt IN statements LOOP
                        EXECUTE 'TRUNCATE TABLE ' || quote_ident(stmt.tablename) || ' CASCADE;';
                    END LOOP;
                END;
                ${'$'}${'$'} LANGUAGE plpgsql;
                """.trimIndent()
            ).execute()

            dslContext.query("SELECT truncate_tables('postgres');").execute()
        }
    }

    data class PostgreSQLProperty(
        val username: String,
        val password: String,
        val host: String,
        val port: Int,
        val name: String
    )
}