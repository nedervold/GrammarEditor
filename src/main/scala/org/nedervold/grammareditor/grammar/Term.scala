package org.nedervold.grammareditor.grammar

/**
 * A term (nonterminal, terminal, or extension) in this grammar
 * @author nedervold
 *
 */
sealed trait Term extends Syntax

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
sealed case class Nonterminal(val name: String) extends Term with TermPrinting with Ordered[Nonterminal] {
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
sealed case class Terminal(val name: String) extends Term with TermPrinting with Ordered[Terminal] {
    require(name != null)
    def foreach[U](f: Term => U): Unit = { f(this) }
    def toStringPrec(prec: Int) = name
    def compare(that: Terminal) = this.name.compare(that.name)
}
