/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.servicekit.endpoint.appdirect.dto

import java.net.URI
import javax.validation.constraints.Pattern
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "result")
data class Result(
        var success: Boolean = false,
        var accountIdentifier: String? = "",
        var message: String = "",
        var errorCode: ErrorCode? = null
)

@XmlRootElement(name = "error")
data class ADError(
        var message: String = "",
        var code: ErrorCode? = null
)

@XmlRootElement(name = "event")
data class Event(
        var type: EventType = EventType.SUBSCRIPTION_ORDER,
        var marketplace: EventMarketplace? = null,
        var flag: EventFlag = EventFlag.DEVELOPMENT,
        var creator: EventCreator? = null,
        var payload: EventPayload? = null,
        var returnUrl: URI? = null
)

data class EventPayload(
        var company: EventPayloadCompany? = null,
        var configuration: EventPayloadConfiguration? = null,
        var order: EventPayloadOrder? = null
)

data class EventPayloadCompany(
        var country: String = "",
        @Pattern(regexp = ".*@.*") var email: String = "",
        var name: String = "",
        var phoneNumber: String = "",
        var uuid: String = "",
        var website: URI = URI.create("")
)

data class EventPayloadConfiguration(
        var entry: ConfigurationEntry? = null
)

data class ConfigurationEntry(
        var key: String = "",
        var value: String = ""
)

data class EventPayloadOrder(
        var  editionCode: String = "",
        var  pricingDuration: String = "",
        var item: OrderItem? = null
)

data class OrderItem(
        var quantity: Int = 0,
        var unit: String = "REQUEST"
)

data class EventCreator(
        @Pattern(regexp = ".*@.*") var email: String = "",
        var firstName: String = "",
        var lastName: String = "",
        var language: String = "",
        var openId: URI = URI.create(""),
        var uuid: String = ""
)

data class EventMarketplace(
        var baseUrl: URI = URI.create(""),
        var partner: String = ""
)

enum class EventFlag {
    STATELESS,
    DEVELOPMENT,
}

enum class ErrorCode {
    NONE,

    USER_ALREADY_EXISTS,
    USER_NOT_FOUND,
    ACCOUNT_NOT_FOUND,
    MAX_USERS_REACHED,
    UNAUTHORIZED,
    OPERATION_CANCELED,
    CONFIGURATION_ERROR,
    INVALID_RESPONSE,
    UNKNOWN_ERROR,
    PENDING,
}

enum class EventType {
    SUBSCRIPTION_ORDER {
        override fun toString() = "Order"
    },
    SUBSCRIPTION_CHANGE {
        override fun toString() = "Change"
    },
    SUBSCRIPTION_CANCEL {
        override fun toString() = "Cancel"
    },
    SUBSCRIPTION_NOTICE,
    USER_ASSIGNMENT {
        override fun toString() = "Assign"
    },
    USER_UNASSIGNMENT {
        override fun toString() = "Unassign"
    },

    ADDON,
}