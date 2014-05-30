package org.nedervold.grammareditor.grammar.transformations

import org.nedervold.grammareditor.grammar.Grammar
import org.nedervold.grammareditor.grammar.Production

/**
 * Returns a grammar including explicit (undefined) productions for all undefined nonterminals.
 * @author nedervold
 */
object AddUndefinedProductions extends GrammarTransformation {
    def apply(gram: Grammar): Grammar = {
        Grammar(gram.productions.filter(_.isDefined) ++ gram.undefinedNonterminals.toSeq.map(new Production(_)))
    }

    def displayName: String = "Show all undefined productions"
}
