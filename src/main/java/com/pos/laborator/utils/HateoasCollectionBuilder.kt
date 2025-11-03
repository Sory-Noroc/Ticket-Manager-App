package com.pos.laborator.utils

import com.pos.laborator.interfaces.DataObject
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder

class HateoasCollectionBuilder(resourceList: List<DataObject>) {
    private val collectionLinks = mutableListOf<Link>()

    private val embeddedResources: List<EntityModel<DataObject>> = resourceList.map { item ->
        EntityModel.of(item)
    }

    fun self(linkProvider: () -> Any) {
        collectionLinks += WebMvcLinkBuilder.linkTo(linkProvider()).withSelfRel()
    }

    fun parent(linkProvider: () -> Any) {
        collectionLinks += WebMvcLinkBuilder.linkTo(linkProvider()).withRel("parent")
    }

    fun custom(rel: String, linkProvider: () -> Any) {
        collectionLinks += WebMvcLinkBuilder.linkTo(linkProvider()).withRel(rel)
    }

    fun addManualLink(link: Link) {
        collectionLinks += link
    }

    fun build(): CollectionModel<EntityModel<DataObject>> = CollectionModel.of(embeddedResources, *collectionLinks.toTypedArray())
}

fun buildHateoasCollection(resourceList: List<DataObject>, builderAction: HateoasCollectionBuilder.() -> Unit): CollectionModel<EntityModel<DataObject>> {
    val builder = HateoasCollectionBuilder(resourceList)
    builder.builderAction()
    return builder.build()
}