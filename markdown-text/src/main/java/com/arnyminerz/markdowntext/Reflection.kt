package com.arnyminerz.markdowntext

import kotlin.reflect.KClass

val <T : Any> KClass<T>.companionClass get() =
    if (isCompanion)
        this.java.declaringClass
    else
        null
