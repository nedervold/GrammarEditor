package org.nedervold.grammareditor.grammar.transformations

import org.nedervold.grammareditor.grammar.Grammar

object AlphabeticSortTransformation extends GrammarTransformation {
    def apply(gram: Grammar): Grammar = {
        require(gram != null)
        val hd :: tl = AddUndefinedProductionsTransformation(gram).productions
        new Grammar(hd :: tl.sortBy(_.head.toString))
    }

    val displayName = "Sort alphabetically"
}