package com.pos.laborator.utils

import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder

class HateoasModelBuilder<T>(private val resource: T) {
    private val links = mutableListOf<Link>()

    fun self(linkProvider: () -> Any) {
        links += WebMvcLinkBuilder.linkTo(linkProvider()).withSelfRel()
    }

    fun parent(linkProvider: () -> Any) {
        links += WebMvcLinkBuilder.linkTo(linkProvider()).withRel("parent")
    }

    fun collection(linkProvider: () -> Any) {
        links += WebMvcLinkBuilder.linkTo(linkProvider()).withRel("collection")
    }

    fun custom(rel: String, linkProvider: () -> Any) {
        links += WebMvcLinkBuilder.linkTo(linkProvider()).withRel(rel)
    }

    fun addManualLink(link: Link) {
        links += link
    }

    fun build(): EntityModel<T> = EntityModel.of(resource, *links.toTypedArray<Link>())
}

fun <T> buildHateoasModel(resource: T, builderAction: HateoasModelBuilder<T>.() -> Unit): EntityModel<T> {
    val builder = HateoasModelBuilder(resource)
    builder.builderAction()
    return builder.build()
}
