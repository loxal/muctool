/*
 * MUCtool Web Toolkit
 *
 * Copyright 2019 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
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

let socket = null;

function connect() {
    socket = new WebSocket("wss://api.muctool.de/curl");

    socket.onerror = function () {
        console.warn("socket error");
    };

    socket.onopen = function () {
        console.warn("Connected");
    };

    socket.onclose = function (e) {
        console.warn(e);
        setTimeout(connect, 500);
    };

    socket.onmessage = function (e) {
        console.warn(e.data.toString());
    };
}