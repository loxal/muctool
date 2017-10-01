/*
 * MUCtool Web Toolkit
 *
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.loxal.muctool

import org.jetbrains.ktor.auth.OAuthServerSettings
import org.jetbrains.ktor.http.HttpMethod

val loginProviders = listOf(
        OAuthServerSettings.OAuth1aServerSettings(
                name = "twitter",
                requestTokenUrl = "https://api.twitter.com/oauth/request_token",
                authorizeUrl = "https://api.twitter.com/oauth/authorize",
                accessTokenUrl = "https://api.twitter.com/oauth/access_token",

                consumerKey = "***",
                consumerSecret = "***"
        ),
        OAuthServerSettings.OAuth2ServerSettings(
                name = "vk",
                authorizeUrl = "https://oauth.vk.com/authorize",
                accessTokenUrl = "https://oauth.vk.com/access_token",
                clientId = "***",
                clientSecret = "***"
        ),
        OAuthServerSettings.OAuth2ServerSettings(
                name = "github",
                authorizeUrl = "https://github.com/login/oauth/authorize",
                accessTokenUrl = "https://github.com/login/oauth/access_token",
                clientId = "034f972ac57485340250",
                clientSecret = System.getenv("MUCTOOL_GITHUB_CLIENT_SECRET")
        ),
        OAuthServerSettings.OAuth2ServerSettings(
                name = "google",
                authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                accessTokenUrl = "https://www.googleapis.com/oauth2/v3/token",
                requestMethod = HttpMethod.Post,

                clientId = "***.apps.googleusercontent.com",
                clientSecret = "***",
                defaultScopes = listOf("https://www.googleapis.com/auth/plus.login")
        ),
        OAuthServerSettings.OAuth2ServerSettings(
                name = "facebook",
                authorizeUrl = "https://graph.facebook.com/oauth/authorize",
                accessTokenUrl = "https://graph.facebook.com/oauth/access_token",
                requestMethod = HttpMethod.Post,

                clientId = "***",
                clientSecret = "***",
                defaultScopes = listOf("public_profile")
        ),
        OAuthServerSettings.OAuth2ServerSettings(
                name = "hub",
                authorizeUrl = "http://localhost:9099/api/rest/oauth2/auth",
                accessTokenUrl = "http://localhost:9099/api/rest/oauth2/token",
                requestMethod = HttpMethod.Post,

                clientId = "***",
                clientSecret = "***",
                defaultScopes = listOf("***"),
                accessTokenRequiresBasicAuth = true
        )
).associateBy { it.name }
