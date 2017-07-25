/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.servicekit.endpoint.appdirect

import net.loxal.servicekit.endpoint.AbstractEndpointTest
import net.loxal.servicekit.endpoint.appdirect.dto.*
import org.junit.Test
import java.net.URI
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import kotlin.test.assertEquals

class ADIntegrationIT {
    @Test
    fun order() {
        fireEvent(EventType.SUBSCRIPTION_ORDER.toString(), SubscriptionResource.RESOURCE_PATH)
    }

    @Test
    fun change() {
        fireEvent(EventType.SUBSCRIPTION_CHANGE.toString(), SubscriptionResource.RESOURCE_PATH)
    }

    @Test
    fun cancel() {
        fireEvent(EventType.SUBSCRIPTION_CANCEL.toString(), SubscriptionResource.RESOURCE_PATH)
    }

    @Test
    fun assign() {
        fireEvent(EventType.USER_ASSIGNMENT.toString(), AccessResource.RESOURCE_PATH)
    }

    @Test
    fun unassign() {
        fireEvent(EventType.USER_UNASSIGNMENT.toString(), AccessResource.RESOURCE_PATH)
    }

    private fun fireEvent(eventType: String, eventEndpointPath: String) {
        val dummyEventPrefix = "dummy"
        val eventId = dummyEventPrefix + eventType

        val event = AbstractEndpointTest.prepareGenericRequest(eventEndpointPath + "/" + eventType.toLowerCase())
                .queryParam(SubscriptionResource.EVENT_URL_QUERY_PARAM, APP_DIRECT_EVENT_ENDPOINT + eventId)
                .queryParam(SubscriptionResource.TOKEN_QUERY_PARAM, eventId)
                .request()
                .get()

        assertEquals(Response.Status.OK.statusCode, event.status, event.toString())
        assertEquals(MediaType.APPLICATION_XML_TYPE, event.mediaType)

        val result = event.readEntity(Result::class.java)
        assertEquals(accountIdentifier, result.accountIdentifier)
        assertEquals(true, result.success)
        assertEquals(null, result.errorCode)
        val eventNameLookup: EventType = when (eventType) {
            "Order" -> EventType.SUBSCRIPTION_ORDER
            "Change" -> EventType.SUBSCRIPTION_CHANGE
            "Cancel" -> EventType.SUBSCRIPTION_CANCEL
            "Assign" -> EventType.USER_ASSIGNMENT
            "Unassign" -> EventType.USER_UNASSIGNMENT
            else -> EventType.ADDON
        }
        assertEquals(eventNameLookup.name, result.message)
    }

    @Test
    fun checkDummyChange() {
        val referenceDummyEvent = ChangeEvent(
                type = EventType.SUBSCRIPTION_CHANGE,
                returnUrl = URI.create("https://www.appdirect.com/finishprocure?token=dummyChange"),
                marketplace = EventMarketplace(
                        baseUrl = URI.create("https://acme.appdirect.com"),
                        partner = "ACME"
                ),
                creator = EventCreator(
                        email = "test-email+creator@appdirect.com",
                        firstName = "DummyCreatorFirst",
                        lastName = "DummyCreatorLast",
                        language = "fr",
                        openId = URI.create("https://www.appdirect.com/openid/id/ec5d8eda-5cec-444d-9e30-125b6e4b67e2"),
                        uuid = "ec5d8eda-5cec-444d-9e30-125b6e4b67e2"
                ),
                payload = ChangeEventPayload(
                        account = EventPayloadAccount(
                                accountIdentifier = "dummy-account",
                                status = "ACTIVE"
                        ),
                        configuration = EventPayloadConfiguration()
                ),
                flag = EventFlag.STATELESS
        )

        val dummyEventResponse = AbstractEndpointTest
                .prepareTarget("${dummyEndpoint}dummyChange")
                .request()
                .get()

        assertEquals(Response.Status.OK.statusCode, dummyEventResponse.status)
        val dummyEvent = dummyEventResponse.readEntity(ChangeEvent::class.java)
        AbstractEndpointTest.LOG.info(dummyEvent.toString())
        assertEquals(referenceDummyEvent, dummyEvent)
    }

    @Test
    fun checkDummyOrder() {
        val referenceDummyOrder = Event(
                type = EventType.SUBSCRIPTION_ORDER,
                returnUrl = URI.create("https://www.appdirect.com/finishprocure?token=dummyOrder"),
                marketplace = EventMarketplace(
                        baseUrl = URI.create("https://acme.appdirect.com"),
                        partner = "ACME"
                ),
                creator = EventCreator(
                        email = "test-email+creator@appdirect.com",
                        firstName = "DummyCreatorFirst",
                        lastName = "DummyCreatorLast",
                        language = "fr",
                        openId = URI.create("https://www.appdirect.com/openid/id/ec5d8eda-5cec-444d-9e30-125b6e4b67e2"),
                        uuid = "ec5d8eda-5cec-444d-9e30-125b6e4b67e2"
                ),
                payload = EventPayload(
                        company = EventPayloadCompany(
                                country = "CA",
                                email = "company-email@example.com",
                                name = "Example Company Name",
                                phoneNumber = "415-555-1212",
                                uuid = "d15bb36e-5fb5-11e0-8c3c-00262d2cda03",
                                website = URI.create("http://www.example.com")
                        ),
                        configuration = EventPayloadConfiguration(
                                entry = ConfigurationEntry(
                                        key = "domain",
                                        value = "mydomain"
                                )
                        ),
                        order = EventPayloadOrder(
                                editionCode = "BASIC",
                                pricingDuration = "MONTHLY",
                                item = OrderItem(
                                        quantity = 15,
                                        unit = "MEGABYTE"
                                )
                        )
                ),
                flag = EventFlag.STATELESS
        )

        val dummyOrder = AbstractEndpointTest
                .prepareTarget("${dummyEndpoint}dummyOrder")
                .request()
                .get()

        assertEquals(Response.Status.OK.statusCode, dummyOrder.status)
        val dummyOrderEvent = dummyOrder.readEntity(Event::class.java)
        AbstractEndpointTest.LOG.info(dummyOrderEvent.toString())
        assertEquals(referenceDummyOrder, dummyOrderEvent)
    }

    companion object {
        private const val APP_DIRECT_EVENT_ENDPOINT = "https://www.appdirect.com/api/integration/v1/events/"
        private val dummyEndpoint = URI.create("https://www.appdirect.com/rest/api/events/")
        private const val accountIdentifier = "ec5d8eda-5cec-444d-9e30-125b6e4b67e2"
    }
}