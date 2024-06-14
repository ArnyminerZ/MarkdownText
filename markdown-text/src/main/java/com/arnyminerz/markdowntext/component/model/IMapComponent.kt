package com.arnyminerz.markdowntext.component.model

interface IMapComponent<Key: Any, Value : Feature> : IComponent {
    val list: Map<Key, Value>
}
