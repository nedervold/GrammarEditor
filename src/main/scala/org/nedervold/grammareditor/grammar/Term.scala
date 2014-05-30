package org.nedervold.grammareditor.grammar

/**
 * A term (nonterminal, terminal, or extension) in this grammar
 * @author nedervold
 *
 */
sealed trait Term extends Syntax with TermPrinting

/**
 * Printing utilities for [[Term]]
 * @author nedervold
 */
trait TermPrinting {
    this: Term =>
    override def toString = toStringPrec(0)

    /**
     * A toString method parameterized on a precedence level
     * @param prec the precedence
     * @return the string
     */
    def toStringPrec(prec: Int): String;

    /**
     * A string, possibly surrounded with parentheses
     * @param parens if true, use parentheses
     * @param str the string
     * @return the string, possibly parenthesized
     */
    final def parensIf(parens: Boolean, str: String): String =
        if (parens) { "(" + str + ")" } else { str }
}

/**
 * A nonterminal in this grammar
 * @author nedervold
 * @constructor
 * @param name the name of the nonterminal
 */
sealed case class Nonterminal(val name: String) extends Term with Ordered[Nonterminal] {
    require(name != null)
    def foreach[U](f: Term => U): Unit = { f(this) }
    def toStringPrec(prec: Int) = name
    def compare(that: Nonterminal) = this.name.compare(that.name)
}

/**
 * A terminal in this grammar
 * @author nedervold
 * @constructor
 * @param name the name of the terminal
 */
sealed case class Terminal(val name: String) extends Term with Ordered[Terminal] {
    require(name != null)
    def foreach[U](f: Term => U): Unit = { f(this) }
    def toStringPrec(prec: Int) = name
    def compare(that: Terminal) = this.name.compare(that.name)
}

/**
 * The term representing no terms
 */
case object Epsilon extends Term {
    def toStringPrec(prec: Int) = ""
    def foreach[U](f: Term => U): Unit = { f(this) }
}

/**
 * A term representing two terms, one after another
 * @author nedervold
 * @constructor
 * @param lhs the first term
 * @param rhs the second term
 */
case class Sequenced(val lhs: Term, val rhs: Term) extends Term {
    require(lhs != null)
    require(rhs != null)
    def toStringPrec(prec: Int) = parensIf(prec > 1, lhs.toStringPrec(2) + " " + rhs.toStringPrec(1))
    def foreach[U](f: Term => U): Unit = { f(this); lhs.foreach(f); rhs.foreach(f) }
}

/**
 * The term representing no alternatives: an impossible parse
 * @author nedervold
 */
case object Fail extends Term {
    def toStringPrec(prec: Int) = "<fail>"
    def foreach[U](f: Term => U): Unit = { f(this) }
}

/**
 * A term representing the choice of two terms, one or the other
 * @author nedervold
 * @constructor
 * @param lhs the first term
 * @param rhs the second term
 */
case class Or(val lhs: Term, val rhs: Term) extends Term {
    require(lhs != null)
    require(rhs != null)

    def toStringPrec(prec: Int) = parensIf(prec > 0, lhs.toStringPrec(1) + " | " + rhs.toStringPrec(0))
    def foreach[U](f: Term => U): Unit = { f(this); lhs.foreach(f); rhs.foreach(f) }
}

