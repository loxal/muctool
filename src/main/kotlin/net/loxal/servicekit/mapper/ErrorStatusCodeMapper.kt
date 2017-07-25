/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.servicekit.mapper

import net.loxal.servicekit.model.common.ErrorMessage
import org.glassfish.jersey.server.ParamException
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

/**
 * Maps Java exceptions to HTTP error codes.
 */
@Provider
class ErrorStatusCodeMapper : ExceptionMapper<WebApplicationException> {
    override fun toResponse(exception: WebApplicationException): Response {
        var status: Response.Status? = EXCEPTION_TO_ERROR_MAPPING[exception.javaClass]
        if (status == null) {
            status = Response.Status.BAD_REQUEST
        }

        val errorMessage = ErrorMessage(status)
        errorMessage.statusCode = status.statusCode
        errorMessage.message = exception.message
        errorMessage.moreInfo = status.family.name

        return Response.status(status.statusCode).entity(errorMessage).type(MediaType.APPLICATION_JSON_TYPE).build()
    }

    companion object {
        private val EXCEPTION_TO_ERROR_MAPPING = mapOf(
                NotFoundException::class.java to Response.Status.NOT_FOUND,
                ParamException.PathParamException::class.java to Response.Status.NOT_FOUND,
                ForbiddenException::class.java to Response.Status.FORBIDDEN,
                InternalServerErrorException::class.java to Response.Status.INTERNAL_SERVER_ERROR,
                NotAuthorizedException::class.java to Response.Status.UNAUTHORIZED
        )
    }
}