/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.servicekit.endpoint

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import net.loxal.servicekit.App
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.WebTarget

abstract class AbstractEndpointTest {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(AbstractEndpointTest::class.java)
        val NON_EXISTENT: String = "non-existent"
        val SERVICE_TARGET: String = App.PROPERTIES.getProperty("deploymentTarget")
        private val client = ClientBuilder.newClient()
        private val mapper = ObjectMapper()
        var resourcePath: String = "/"

        fun prepareGenericRequest(path: String): WebTarget {
            return prepareTarget(SERVICE_TARGET).path(path)
        }

        fun prepareTarget(target: String): WebTarget {
            return client.target(target)
        }

        init {
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            client.register(mapper)
        }
    }
}