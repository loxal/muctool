/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.servicekit.endpoint.appdirect

import net.loxal.servicekit.endpoint.Endpoint
import net.loxal.servicekit.endpoint.appdirect.dto.OpenIdInfo
import net.loxal.servicekit.model.common.ErrorMessage
import org.springframework.security.openid.OpenID4JavaConsumer
import org.springframework.security.openid.OpenIDAuthenticationStatus
import org.springframework.security.openid.OpenIDAuthenticationToken
import org.springframework.security.openid.OpenIDConsumer
import java.net.URI
import java.net.URL
import javax.servlet.http.HttpServletRequest
import javax.validation.constraints.NotNull
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.QueryParam
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.container.Suspended
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response

@Path(OpenIdAuthentication.RESOURCE_PATH)
class OpenIdAuthentication : Endpoint() {

    @GET
    fun verify(
            @Context req: HttpServletRequest,
            @Suspended asyncResponse: AsyncResponse
    ) {
        val token: OpenIDAuthenticationToken = openIdConsumer.endConsumption(req)

        Endpoint.LOG.info("openid.return_to: ${req.getParameter("openid.return_to")}")

        if (OpenIDAuthenticationStatus.SUCCESS == token.status) {
            Endpoint.LOG.info("requestUri: ${req.requestURI}")
            val redirection = "${req.requestURL}/../$clientRedirectionPath?openid.id=${token.identityUrl}"
            asyncResponse.resume(Response.temporaryRedirect(URI.create(redirection)).build())
        } else {
            asyncResponse.resume(Response.status(Response.Status.UNAUTHORIZED).entity(ErrorMessage(Response.Status.UNAUTHORIZED)).build())
        }
    }

    @Path("openid")
    @GET
    fun authenticate(
            @NotNull @QueryParam("url") url: URL?,
            @NotNull @QueryParam("returnToUrl") returnToUrl: URL?,
            @Context req: HttpServletRequest,
            @Suspended asyncResponse: AsyncResponse
    ) {
        val signInUrl: URL = URL(openIdConsumer.beginConsumption(req, url.toString(), returnToUrl.toString(), returnToUrl.toString()))

        Endpoint.LOG.info("signInUrl: $signInUrl")
        val openIdInfo = OpenIdInfo(url = url, returnToUrl = returnToUrl, signInUrl = signInUrl)
        asyncResponse.resume(Response.ok(openIdInfo).location(signInUrl.toURI()).build())
    }

    companion object {
        const val RESOURCE_PATH = "authentication"

        private const val clientRedirectionPath = "play/ground.html"
        private val openIdConsumer: OpenIDConsumer = OpenID4JavaConsumer()
    }
}