/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.servicekit

import org.glassfish.jersey.server.ResourceConfig
import java.util.*

class App : ResourceConfig() {
    init {
        packages(javaClass.`package`.name)
    }

    companion object {
        val PROPERTIES = Properties()
        private const val LOCAL_PROPERTIES = "/local.properties"

        init {
            PROPERTIES.load(App::class.java.getResourceAsStream(LOCAL_PROPERTIES))
        }
    }
}