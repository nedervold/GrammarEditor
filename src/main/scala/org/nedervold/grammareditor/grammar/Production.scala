package org.nedervold.grammareditor.grammar

/**
 * Productions (possibly undefined) in our grammar of grammars.  An undefined production
 * will not have a right-hand side.
 * @author nedervold
 *
 * @constructor
 * @param lhs the [[Nonterminal]] that this production defines
 * @param rhs contains the [[Term]] that defines this production or empty if the production is undefined
 */
sealed case class Production(val lhs: Nonterminal,
                             val rhs: Option[Term]) extends Syntax {
    require(lhs != null)
    require(rhs != null)

    /**
     * Constructs an undefined production.
     *
     * @constructor
     * @param lhs the [[Nonterminal]] that this production will define
     */
    def this(lhs: Nonterminal) = { this(lhs, None) }

    /**
     * Constructs a defined production.
     *
     * @constructor
     * @param lhs the [[Nonterminal]] that this production defines
     * @param rhs the [[Term]] that defines this production
     */
    def this(lhs: Nonterminal, rhs: Term) = {
        this(lhs, Some(rhs))
        require(rhs != null)
    }

    /**
     * Returns true if this production is defined
     * @return true iff defined
     */
    def isDefined = rhs.nonEmpty

    override def toString = {
        val rhsStr = rhs match {
            case Some(rhs) => " ::= " + rhs.toString
            case None => ""
        }
        lhs + rhsStr + "."
    }

    def foreach[U](f: Term => U): Unit = {
        f(lhs)
        rhs.map(_.foreach(f))
    }
}