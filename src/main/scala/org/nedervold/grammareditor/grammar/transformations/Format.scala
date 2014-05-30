package org.nedervold.grammareditor.grammar.transformations

import org.nedervold.grammareditor.grammar.Grammar

/**
 * Returns a grammar with undefined productions made explicit.  This transformation is
 * used mostly for its side-effect: that running toString() on it pretty-prints the grammar.
 *
 * @author nedervold
 */
object Format extends GrammarTransformation {
    def apply(gram: Grammar): Grammar = AddUndefinedProductions(gram)

    val displayName = "Format"
    override val accelerator = mkAccelerator('F')
}
