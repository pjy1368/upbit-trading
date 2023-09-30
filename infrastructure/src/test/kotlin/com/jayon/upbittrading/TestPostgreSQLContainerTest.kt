package com.jayon.upbittrading

import com.jayon.upbittrading.configuration.TestR2dbcConfiguration
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [TestR2dbcConfiguration::class])
annotation class TestPostgreSQLContainerTest
