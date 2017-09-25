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

package org.jetbrains.react.dom

import kotlinx.html.INPUT
import kotlinx.html.TEXTAREA
import kotlinx.html.attributes.Attribute
import kotlinx.html.attributes.StringAttribute

private val events = listOf(
        "onCopy",
        "onCut",
        "onPaste",
        "onCompositionEnd",
        "onCompositionStart",
        "onCompositionUpdate",
        "onKeyDown",
        "onKeyPress",
        "onKeyUp",
        "onFocus",
        "onBlur",
        "onChange",
        "onInput",
        "onSubmit",
        "onClick",
        "onContextMenu",
        "onDoubleClick",
        "onDrag",
        "onDragEnd",
        "onDragEnter",
        "onDragExit",
        "onDragLeave",
        "onDragOver",
        "onDragStart",
        "onDrop",
        "onMouseDown",
        "onMouseEnter",
        "onMouseLeave",
        "onMouseMove",
        "onMouseOut",
        "onMouseOver",
        "onMouseUp",
        "onSelect",
        "onTouchCancel",
        "onTouchEnd",
        "onTouchMove",
        "onTouchStart",
        "onScroll",
        "onWheel",
        "onAbort",
        "onCanPlay",
        "onCanPlayThrough",
        "onDurationChange",
        "onEmptied",
        "onEncrypted",
        "onEnded",
        "onError",
        "onLoadedData",
        "onLoadedMetadata",
        "onLoadStart",
        "onPause",
        "onPlay",
        "onPlaying",
        "onProgress",
        "onRateChange",
        "onSeeked",
        "onSeeking",
        "onStalled",
        "onSuspend",
        "onTimeUpdate",
        "onVolumeChange",
        "onWaiting",
        "onLoad",
        "onError",
        "onAnimationStart",
        "onAnimationEnd",
        "onAnimationIteration",
        "onTransitionEnd",


        // HTML attributes
        "accept",
        "acceptCharset",
        "accessKey",
        "action",
        "allowFullScreen",
        "allowTransparency",
        "alt",
        "async",
        "autoComplete",
        "autoFocus",
        "autoPlay",
        "capture",
        "cellPadding",
        "cellSpacing",
        "challenge",
        "charSet",
        "checked",
        "cite",
        "classID",
        "className",
        "colSpan",
        "cols",
        "content",
        "contentEditable",
        "contextMenu",
        "controls",
        "coords",
        "crossOrigin",
        "data",
        "dateTime",
        "default",
        "defer",
        "dir",
        "disabled",
        "download",
        "draggable",
        "encType",
        "form",
        "formAction",
        "formEncType",
        "formMethod",
        "formNoValidate",
        "formTarget",
        "frameBorder",
        "headers",
        "height",
        "hidden",
        "high",
        "href",
        "hrefLang",
        "htmlFor",
        "httpEquiv",
        "icon",
        "id",
        "inputMode",
        "integrity",
        "is",
        "keyParams",
        "keyType",
        "kind",
        "label",
        "lang",
        "list",
        "loop",
        "low",
        "manifest",
        "marginHeight",
        "marginWidth",
        "max",
        "maxLength",
        "media",
        "mediaGroup",
        "method",
        "min",
        "minLength",
        "multiple",
        "muted",
        "name",
        "noValidate",
        "nonce",
        "open",
        "optimum",
        "pattern",
        "placeholder",
        "poster",
        "preload",
        "profile",
        "radioGroup",
        "readOnly",
        "rel",
        "required",
        "reversed",
        "role",
        "rowSpan",
        "rows",
        "sandbox",
        "scope",
        "scoped",
        "scrolling",
        "seamless",
        "selected",
        "shape",
        "size",
        "sizes",
        "span",
        "spellCheck",
        "src",
        "srcDoc",
        "srcLang",
        "srcSet",
        "start",
        "step",
        "style",
        "summary",
        "tabIndex",
        "target",
        "title",
        "type",
        "useMap",
        "value",
        "width",
        "wmode",
        "wrap")

private val eventMap = events.map { it.toLowerCase() to it }.toMap()

fun fixAttributeName(event: String): String = eventMap[event] ?: if (event == "class") "className" else event

private val attributeStringString: Attribute<String> = StringAttribute()

// See https://facebook.github.io/react/docs/forms.html
var INPUT.defaultValue: String
    get() = attributeStringString.get(this, "defaultValue")
    set(newValue) {
        attributeStringString.set(this, "defaultValue", newValue)
    }

var TEXTAREA.defaultValue: String
    get() = attributeStringString.get(this, "defaultValue")
    set(newValue) {
        attributeStringString.set(this, "defaultValue", newValue)
    }

var TEXTAREA.value: String
    get() = attributeStringString.get(this, "value")
    set(newValue) {
        attributeStringString.set(this, "value", newValue)
    }

