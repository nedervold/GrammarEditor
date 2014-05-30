package org.nedervold.grammareditor.grammar.transformations

import org.nedervold.grammareditor.grammar.Grammar

object FormatTransformation extends GrammarTransformation {
    def apply(gram: Grammar): Grammar = AddUndefinedProductionsTransformation(gram)

    override val accelerator = mkAccelerator('F')

    val displayName = "Format"
}
