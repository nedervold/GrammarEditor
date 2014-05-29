package org.nedervold.grammareditor.grammar

/**
 * Grammars in our grammar of grammars.
 * @author nedervold
 * 
 * @constructor
 * @param productions the productions of the grammar; must be at least one
 */
case class Grammar(val productions: Seq[Production]) extends Syntax {
    require(productions != null)
    require(!productions.isEmpty)

    def foreach[U](f: Term => U): Unit = {
        productions.map(_.foreach(f))
    }
    override def toString = productions.mkString("\n");
}