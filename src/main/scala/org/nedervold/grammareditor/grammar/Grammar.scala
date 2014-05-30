package org.nedervold.grammareditor.grammar

import scala.collection.immutable.TreeSet
import scala.collection.SortedSet

/**
 * Grammars in our grammar of grammars.
 * @author nedervold
 *
 * @constructor
 * @param productions the productions of the grammar; must be at least one
 */
sealed case class Grammar(val productions: Seq[Production]) extends Syntax {
    require(productions != null)
    require(!productions.isEmpty)

    /**
     * Returns true if all productions are defined
     * @return true if all productions are defined
     */
    def isDefined = productions.forall(_.isDefined);

    def definedNonterminals = TreeSet[Nonterminal]() ++ productions.collect {
        case Production(nt, Some(_)) => nt
    }

    def undefinedNonterminals: SortedSet[Nonterminal] = nonterminals.diff(definedNonterminals)

    def foreach[U](f: Term => U): Unit = {
        productions.map(_.foreach(f))
    }
    override def toString = productions.mkString("\n");
}