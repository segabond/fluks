package net.skycanvas.fluks


abstract class Predicate {
    abstract fun render(): String
    fun and(other: Predicate): Predicate = ConjunctionPredicate(this, other)
    fun or(other: Predicate): Predicate = DisjunctionPredicate(this, other)
}

class EqualityPredicate(val left: Expression, val right: Expression) : Predicate() {
    override fun render(): String = "(${left.render()}=${right.render()})"
}

class PatternPredicate(private val left: Expression,
                       private val right: Expression) : Predicate() {

    override fun render(): String = "(${left.render()} LIKE ${right.render()}"
}

class ConjunctionPredicate(private val p1: Predicate,
                           private val p2: Predicate) : Predicate() {
    override fun render(): String = "(${p1.render()}) AND (${p2.render()})"
}

class DisjunctionPredicate(private val p1: Predicate,
                           private val p2: Predicate) : Predicate() {
    override fun render(): String = "(${p1.render()}) OR (${p2.render()})"
}
