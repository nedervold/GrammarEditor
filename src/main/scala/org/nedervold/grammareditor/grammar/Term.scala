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
case class Nonterminal(val name: String) extends Term with Ordered[Nonterminal] {
    require(name != null)
    def foreach[U](f: Term => U): Unit = { f(this) }
    def toStringPrec(prec: Int) = name
    def compare(that: Nonterminal) = this.name.compare(that.name)
}

/**
 * A synthesized nonterminal in this grammar
 * @author nedervold
 * @constructor
 * @param tag the tag for this nonterminal
 * @param num a serial number for this nonterminal
 * @param sourceTerm the term this nonterminal is synthesized from
 */
class SyntheticNonterminal(val tag: String, val num: Int, val sourceTerm: Term)
    extends Nonterminal("synth_" + tag + "_" + num) {}

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

/**
 * An optional term
 * @author nedervold
 * @constructor
 * @param body the optional term
 */
case class Optional(val body: Term) extends Term {
    require(body != null)

    def toStringPrec(prec: Int) = "[" + body.toString ++ "]"
    def foreach[U](f: Term => U): Unit = { f(this); body.foreach(f) }
}

/**
 * A repeated term, possibly no times
 * @author nedervold
 * @constructor
 * @param body the repeated term
 */
case class Repetition0(val body: Term) extends Term {
    require(body != null)

    def toStringPrec(prec: Int) = "{" + body.toString ++ "}"
    def foreach[U](f: Term => U): Unit = { f(this); body.foreach(f) }
}

/**
 * A repeated term, appearing at least once
 * @author nedervold
 * @constructor
 * @param body the repeated term
 */
case class Repetition1(val body: Term) extends Term {
    require(body != null)

    def toStringPrec(prec: Int) = "{" + body.toString ++ "}+"
    def foreach[U](f: Term => U): Unit = { f(this); body.foreach(f) }
}

/**
 * A repeated term with a separator, possibly repeated no times
 * @author nedervold
 * @constructor
 * @param body the repeated term
 * @param sep the separator
 */
case class RepetitionSep0(val body: Term, val sep: Term) extends Term {
    require(body != null)
    require(sep != null)

    def toStringPrec(prec: Int) = "{" + body.toStringPrec(2) + " ... " + sep.toStringPrec(2) + "}"
    def foreach[U](f: Term => U): Unit = { f(this); body.foreach(f); sep.foreach(f) }
}

/**
 * A repeated term with a separator, appearing at least once
 * @author nedervold
 * @constructor
 * @param body the repeated term
 * @param sep the separator
 */
case class RepetitionSep1(val body: Term, val sep: Term) extends Term {
    require(body != null)
    require(sep != null)

    def toStringPrec(prec: Int) = "{" + body.toStringPrec(2) + " ... " + sep.toStringPrec(2) + "}+"
    def foreach[U](f: Term => U): Unit = { f(this); body.foreach(f); sep.foreach(f) }
}

