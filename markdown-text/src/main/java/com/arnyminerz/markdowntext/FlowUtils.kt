package com.arnyminerz.markdowntext

/**
 * If [condition] is true, returns the result of [block], otherwise `this` is returned.
 * @author Arnau Mora
 * @since 20221216
 */
fun <T> T.letIf(condition: Boolean, block: (obj: T) -> T) = takeIf { !condition } ?: block(this)

fun CharSequence.indexOfOrNull(predicate: Char) = indexOf(predicate).takeIf { it >= 0 }

operator fun CharSequence.plus(other: CharSequence): CharSequence = toString() + other.toString()

context(Boolean)
        infix fun <T> T.or(other: T): T = if (this@Boolean) this else other
