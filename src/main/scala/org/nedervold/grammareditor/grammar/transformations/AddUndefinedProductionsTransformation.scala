package org.nedervold.grammareditor.grammar.transformations

import org.nedervold.grammareditor.grammar.Grammar
import org.nedervold.grammareditor.grammar.Production

object AddUndefinedProductionsTransformation extends GrammarTransformation {
    def apply(gram: Grammar): Grammar = {
        Grammar(gram.productions.filter(_.isDefined) ++ gram.undefinedNonterminals.toSeq.map(new Production(_)))
    }

    def displayName: String = "Show all undefined productions"
}
