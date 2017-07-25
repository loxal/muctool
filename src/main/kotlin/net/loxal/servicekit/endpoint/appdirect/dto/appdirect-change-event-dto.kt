/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.servicekit.endpoint.appdirect.dto

import java.net.URI
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "event")
data class ChangeEvent(
        var type: EventType = EventType.SUBSCRIPTION_ORDER,
        var marketplace: EventMarketplace? = null,
        var flag: EventFlag = EventFlag.STATELESS,
        var creator: EventCreator? = null,
        var payload: ChangeEventPayload? = null,
        var returnUrl: URI? = null
)

data class ChangeEventPayload(
        var account: EventPayloadAccount? = null,
        var configuration: EventPayloadConfiguration? = null
)

data class EventPayloadAccount(
        var accountIdentifier: String = "",
        var status: String = ""
)

