/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.servicekit.endpoint

import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class Endpoint protected constructor() {
    companion object {
        const val ASYNC_RESPONSE_TIMEOUT: Int = 3
        const val ID_PATH_PARAM: String = "id"
        const val ID_PATH_PARAM_PLACEHOLDER: String = "{$ID_PATH_PARAM}"
        val LOG: Logger = LoggerFactory.getLogger(Endpoint::class.java)
    }
}