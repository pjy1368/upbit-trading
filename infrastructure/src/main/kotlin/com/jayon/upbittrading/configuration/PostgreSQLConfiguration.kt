package com.jayon.upbittrading.configuration

import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.transaction.ReactiveTransactionManager

@Configuration
@EnableConfigurationProperties(DatabaseProperty::class)
class PostgreSQLConfiguration(private val databaseProperty: DatabaseProperty) : AbstractR2dbcConfiguration() {

    @Bean
    override fun connectionFactory(): ConnectionFactory {
        val connectionFactory = ConnectionFactories.get(
            ConnectionFactoryOptions.builder()
                .option(ConnectionFactoryOptions.DRIVER, "pool")
                .option(ConnectionFactoryOptions.PROTOCOL, "postgresql")
                .option(ConnectionFactoryOptions.HOST, databaseProperty.host)
                .option(ConnectionFactoryOptions.PORT, databaseProperty.port.toInt())
                .option(ConnectionFactoryOptions.DATABASE, databaseProperty.name)
                .option(ConnectionFactoryOptions.USER, databaseProperty.username)
                .option(ConnectionFactoryOptions.PASSWORD, databaseProperty.password)
                .build()
        )
        return ConnectionPool(ConnectionPoolConfiguration.builder(connectionFactory).build())
    }

    @Bean
    fun dslContext(connectionFactory: ConnectionFactory): DSLContext =
        DSL.using(connectionFactory)

    @Bean
    fun transactionManager(connectionFactory: ConnectionFactory): ReactiveTransactionManager {
        return R2dbcTransactionManager(connectionFactory)
    }
}

@ConfigurationProperties(prefix = "spring.r2dbc.postgresql")
data class DatabaseProperty(
    val username: String,
    val password: String,
    val host: String,
    val port: String,
    val name: String
)
