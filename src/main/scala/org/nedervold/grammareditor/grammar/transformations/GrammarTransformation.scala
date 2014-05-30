package org.nedervold.grammareditor.grammar.transformations

import javax.swing.KeyStroke
import org.nedervold.grammareditor.grammar.Grammar

trait GrammarTransformation extends Function1[Grammar, Grammar] {
    // def apply(gram: Grammar): Grammar

	def displayName: String
	val accelerator : Option[KeyStroke]= None
}