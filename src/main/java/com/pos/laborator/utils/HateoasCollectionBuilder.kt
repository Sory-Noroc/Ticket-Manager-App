package com.pos.laborator.utils

import com.pos.laborator.view.Entity
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder

class HateoasCollectionBuilder(resourceList: List<Entity>) {
    private val collectionLinks = mutableListOf<Link>()

    private val embeddedResources: List<EntityModel<Entity>> = resourceList.map { item ->
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

    fun build(): CollectionModel<EntityModel<Entity>> = CollectionModel.of(embeddedResources, *collectionLinks.toTypedArray())
}

fun buildHateoasCollection(resourceList: List<Entity>, builderAction: HateoasCollectionBuilder.() -> Unit): CollectionModel<EntityModel<Entity>> {
    val builder = HateoasCollectionBuilder(resourceList)
    builder.builderAction()
    return builder.build()
}