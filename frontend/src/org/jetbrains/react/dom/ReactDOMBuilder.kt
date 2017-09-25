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

import kotlinx.html.*
import org.jetbrains.react.RProps
import org.jetbrains.react.ReactBuilder
import org.jetbrains.react.ReactElement
import org.w3c.dom.events.Event

class InnerHTML(
        val __html: String
)

class DOMProps : RProps() {
    var dangerouslySetInnerHTML: InnerHTML? = null
}

class ReactDOMBuilder : ReactBuilder(), TagConsumer<ReactElement?> {
/*
    inline operator fun <reified T: ReactComponent<P, S>, reified P : RProps, S: RState> ReactComponentSpec<T, P, S>.invoke(
        noinline handler: P.() -> Unit = {}
    ) : ReactElement {
        val props = instantiateProps<P>()
        return node(props) { props.handler() }
    }

    inline operator fun <reified P : RProps> ReactExternalComponentSpec<P>.invoke(
        noinline handler: P.() -> Unit = {}
    ) : ReactElement {
        val props = instantiateProps<P>()
        return node(props) { props.handler() }
    }
*/

    override fun <P : RProps> createReactNode(type: Any, props: P) = Node(type, props)

    class DOMNode(val tagName: String) : Node<DOMProps>(tagName, DOMProps())

    private fun currentDOMNode() = currentNodeOfType<DOMNode>()

    var HTMLTag.key: String
        get() {
            return currentDOMNode().props.key ?: ""
        }
        set(value) {
            currentDOMNode().props.key = value
        }

    fun setProp(attribute: String, value: dynamic) {
        val node = currentNode()
        val key = fixAttributeName(attribute)
        if (value == null) {
            js("delete node.props[key]")
        } else {
            node.props.asDynamic()[key] = value
        }
    }

    override fun onTagAttributeChange(tag: Tag, attribute: String, value: String?) {
        setProp(attribute, value)
    }

    operator fun String.unaryPlus() {
        onTagContent(this)
    }

    override fun onTagContent(content: CharSequence): Unit {
        children.add(content)
    }

    override fun onTagContentEntity(entity: Entities): Unit {
        children.add(entity.text)
    }

    override fun onTagContentUnsafe(block: Unsafe.() -> Unit) {
        val sb = StringBuilder()
        object : Unsafe {
            override fun String.unaryPlus() {
                sb.append(this)
            }
        }.block()
        val node = currentDOMNode()
        node.props.dangerouslySetInnerHTML = InnerHTML(sb.toString())
    }

    override fun onTagStart(tag: Tag) {
        enterNode(DOMNode(tag.tagName))
        tag.attributesEntries.forEach { setProp(it.key, it.value) }
    }

    override fun onTagEnd(tag: Tag) {
        if (path.isEmpty() || currentDOMNode().tagName.toLowerCase() != tag.tagName.toLowerCase())
            throw IllegalStateException("We haven't entered tag ${tag.tagName} but trying to leave")
        exitCurrentNode()
    }

    override fun onTagEvent(tag: Tag, event: String, value: (Event) -> Unit) {
        setProp(event, value)
    }

    override fun finalize(): ReactElement? {
        return result()
    }
}

fun buildElement(handler: ReactDOMBuilder.() -> Unit) = with(ReactDOMBuilder()) {
    handler()
    finalize()
}
