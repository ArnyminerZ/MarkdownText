package com.arnyminerz.markdowntext

import kotlin.reflect.KClass

/**
 * Finds the first entry in the map that matches the given [predicate].
 */
fun <K, V> Map<K, V>.findByKey(predicate: (K) -> Boolean): Map.Entry<K, V>? {
    return this.entries.find { predicate(it.key) }
}

fun <K: Any, V> Map<K, V>.findByInstanceOfKey(kClass: KClass<*>): Map.Entry<K, V>? {
    return this.entries.find { kClass.isInstance(it.key) }
}
