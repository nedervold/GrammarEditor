package org.nedervold.grammareditor.grammar

import scala.collection.immutable.TreeSet

/**
 * Syntax elements in our grammar of grammars considered, among other
 * things, as containers of [[Term]]s.
 *
 * @author nedervold
 */
trait Syntax extends Traversable[Term] {
    /**
     * the nonterminals appearing in this syntax element, sorted
     */
    final def nonterminals = {
        new TreeSet[Nonterminal]() ++ collect { case nt@Nonterminal(_) => nt }
    } /* ensuring (_ != null) */

    /**
     * the terminals appearing in this syntax element, sorted
     */
    final def terminals = {
        new TreeSet[Terminal]() ++ collect { case t@Terminal(_) => t }
    } /* ensuring (_ != null) */
}