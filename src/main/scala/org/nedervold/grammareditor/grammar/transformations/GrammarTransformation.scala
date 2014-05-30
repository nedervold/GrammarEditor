package org.nedervold.grammareditor.grammar.transformations

import javax.swing.KeyStroke
import org.nedervold.grammareditor.grammar.Grammar

/**
 * A transformation of a [[Grammar]] to a [[Grammar]].
 * @author nedervold
 */
trait GrammarTransformation extends Function1[Grammar, Grammar] {
    // def apply(gram: Grammar): Grammar

	/**
	 * The name used to display this transformation, say, in a MenuItem.
	 */
	def displayName: String
	
	/** 
	 *  The accelerator key used in a Menu for this transformation, if any.
	 */
	val accelerator : Option[KeyStroke]= None
}