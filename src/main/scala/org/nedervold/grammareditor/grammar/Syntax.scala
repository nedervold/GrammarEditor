package org.nedervold.grammareditor.grammar

import scala.collection.immutable.TreeSet

trait Syntax extends Traversable[Term] {
    /**
     * the nonterminals appearing in this syntax, sorted
     */
    def nonterminals = new TreeSet[Nonterminal]() ++ collect { case nt@Nonterminal(_) => nt }
    
    /**
     * the terminals appearing in this syntax, sorted
     */
    def terminals = new TreeSet[Terminal]() ++ collect { case t@Terminal(_) => t }
}