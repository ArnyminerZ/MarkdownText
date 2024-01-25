package com.arnyminerz.markdowntext.component.model

interface IContainer<Type : Feature> : IComponent {
    val list: List<Type>
}