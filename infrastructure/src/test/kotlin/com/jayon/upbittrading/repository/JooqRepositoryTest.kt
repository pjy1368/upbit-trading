package com.jayon.upbittrading.repository

import com.jayon.upbittrading.TestPostgreSQLContainerTest
import io.kotest.core.spec.style.DescribeSpec
import io.r2dbc.spi.ConnectionFactory
import org.jooq.DSLContext
import org.jooq.impl.DSL

@TestPostgreSQLContainerTest
class JooqRepositoryTest(
    private val connectionFactory: ConnectionFactory,
    private val dslContext: DSLContext = DSL.using(connectionFactory)
) : DescribeSpec() {

    init {
        describe("#test") {
            println(dslContext.meta())
        }
    }
}