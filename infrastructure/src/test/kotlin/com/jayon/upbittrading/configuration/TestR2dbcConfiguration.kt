package com.jayon.upbittrading.configuration

import com.jayon.upbittrading.testcontainers.TestPostgreSQLContainer
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration

@Configuration
class TestR2dbcConfiguration : AbstractR2dbcConfiguration() {

    @Bean
    override fun connectionFactory(): ConnectionFactory {
        val property = TestPostgreSQLContainer.start()

        return ConnectionFactories.get(
            ConnectionFactoryOptions.builder()
                .option(ConnectionFactoryOptions.DRIVER, "postgresql")
                .option(ConnectionFactoryOptions.HOST, property.host)
                .option(ConnectionFactoryOptions.PORT, property.port)
                .option(ConnectionFactoryOptions.DATABASE, property.name)
                .option(ConnectionFactoryOptions.USER, property.username)
                .option(ConnectionFactoryOptions.PASSWORD, property.password)
                .build()
        )
    }
}
