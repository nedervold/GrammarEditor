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

    /**
     * Treats a [[Grammar]] as a map from [[Production]] heads to [[Production]]s.
     *
     * @param nt the head of the [[Production]] to find
     * @return the production, if found
     */
    def apply(nt: Nonterminal) = productions.find((p) => p.lhs == nt)

    def definedNonterminals = TreeSet[Nonterminal]() ++ productions.collect {
        case Production(nt, Some(_)) => nt
    }

    def undefinedNonterminals: SortedSet[Nonterminal] = nonterminals.diff(definedNonterminals)

    def foreach[U](f: Term => U): Unit = {
        productions.map(_.foreach(f))
    }
    override def toString = productions.mkString("\n");
}