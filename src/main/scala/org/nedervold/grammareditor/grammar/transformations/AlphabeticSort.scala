package org.nedervold.grammareditor.grammar.transformations

import org.nedervold.grammareditor.grammar.Grammar

/**
 * Returns a grammar with the productions sorted alphabetically by head (except the first,
 * assumed to be the start symbol).
 * @author nedervold
 */
object AlphabeticSort extends GrammarTransformation {
    def apply(gram: Grammar): Grammar = {
        require(gram != null)
        val hd :: tl = AddUndefinedProductions(gram).productions
        new Grammar(hd :: tl.sortBy(_.head.toString))
    }

    val displayName = "Sort alphabetically"
}