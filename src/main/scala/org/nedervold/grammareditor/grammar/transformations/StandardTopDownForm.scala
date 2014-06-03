package org.nedervold.grammareditor.grammar.transformations

import org.nedervold.grammareditor.grammar.alternate
import org.nedervold.grammareditor.grammar._

/**
 * Returns a grammar with extended BNF symbols expanded into their BNF equivalents
 * such that all productions in the final grammar are alternatives of sequences of
 * nonterminals and terminals.  Expansion is done to avoid left recursion and new
 * productions are freely made.
 *
 * @author nedervold
 */
object StandardTopDownForm extends GrammarTransformation {
    override val accelerator = mkAccelerator('T')

    private def doProds(prods: Seq[Production]): List[Production] = {
        import scala.collection.mutable.ListBuffer;

        var count: Int = 0;
        var toDo = new ListBuffer[Production];
        toDo ++= prods
        var done = new ListBuffer[Production];

        def synthNonterminal(tag: String, source: Term): Nonterminal = {
            count += 1;
            new SyntheticNonterminal(tag, count, source);
        }

        def toSeqElmt(term: Term): Term = {
            term match {
                case Terminal(_) => term
                case Nonterminal(_) => term
                case Fail => {
                    val nt = synthNonterminal("fail", term)
                    toDo += new Production(nt, term)
                    nt
                }
                case Or(_, _) => {
                    val nt = synthNonterminal("or", term)
                    toDo += new Production(nt, term)
                    nt
                }
                case Optional(t) => {
                    val nt = synthNonterminal("opt", term)
                    toDo += new Production(nt, term)
                    nt
                }
                case Repetition0(t) => {
                    val nt = synthNonterminal("rep0", term)
                    val newT = toSeqElmt(t)
                    toDo += new Production(nt, alternate(sequence(newT, nt), Epsilon))
                    nt
                }
                case Repetition1(t) => {
                    val nt = synthNonterminal("rep1", term)
                    val nt2 = synthNonterminal("rep1", term)
                    val newT = toSeqElmt(t)
                    toDo += new Production(nt, sequence(newT, nt2))
                    toDo += new Production(nt2, alternate(sequence(newT, nt2), Epsilon))
                    nt
                }
                case RepetitionSep0(b, s) => {
                    val nt = synthNonterminal("repsep0", term)
                    val nt2 = synthNonterminal("repsep0", term)
                    val newB = toSeqElmt(b)
                    val newS = toSeqElmt(s)
                    toDo += new Production(nt, alternate(sequence(nt, newS, newB), newB))
                    nt
                }
                case RepetitionSep1(b, s) => {
                    val nt = synthNonterminal("repsep1", term)
                    val nt2 = synthNonterminal("repsep1", term)
                    val newB = toSeqElmt(b)
                    val newS = toSeqElmt(s)
                    toDo += new Production(nt, alternate(sequence(newB, nt2), Epsilon))
                    toDo += new Production(nt2, alternate(sequence(newS, newB, nt2), Epsilon))
                    nt
                }
                case AtLeastOne(ts) => {
                    val nt = synthNonterminal("alo", term)
                    toDo += new Production(nt, term)
                    nt
                }
                case _ => term
            }
        }

        def toSeq(term: Term): List[Term] = {
            term match {
                case Epsilon => List()
                case Sequenced(lhs, rhs) => toSeq(lhs) ++ toSeq(rhs)
                case Repetition1(b) => {
                    val nt = synthNonterminal("repsep1", term)
                    val newB = toSeq(b)
                    toDo += new Production(nt, alternate(sequence(newB ++ List(nt)), Epsilon))
                    newB ++ List(nt)
                }
                case RepetitionSep1(b, s) => {
                    val nt = synthNonterminal("repsep1", term)
                    val newB = toSeq(b)
                    val newS = toSeq(s)
                    toDo += new Production(nt, alternate(sequence(newS ++ newB ++ List(nt)), Epsilon))
                    newB ++ List(nt)
                }
                case _ => List(term)
            }
        }

        def toAlts(term: Term): List[Term] = {
            term match {
                case Fail => List()
                case Or(lhs, rhs) => toAlts(lhs) ++ toAlts(rhs)
                case Optional(t) => toAlts(t) ++ toAlts(Epsilon)
                case RepetitionSep0(b, s) => {
                    val nt = synthNonterminal("repsep0", term)
                    val nt2 = synthNonterminal("repsep0", term)
                    toDo += new Production(nt, sequence(b, nt2))
                    toDo += new Production(nt2, alternate(sequence(s, b, nt2), Epsilon))
                    List(nt, Epsilon)
                }
                case AtLeastOne(ts) => ts match {
                    case Nil => throw new Exception("Impossible");
                    case t :: Nil => toAlts(t)
                    case t :: ts => toAlts(alternate(sequence(t :: ts.map(Optional(_))), AtLeastOne(ts)))
                }
                case _ => List(term)
            }
        }

        def doSeq(term: Term): Term = { sequence(toSeq(term).map(toSeqElmt)) }

        def doAlts(rhs: Term) = { alternate(toAlts(rhs).map(doSeq)) }

        def doProd(prod: Production): Production = {
            prod match {
                case Production(lhs, optRhs) => Production(lhs, optRhs.map(doAlts))
            }
        }

        while (!toDo.isEmpty) {
            val prod = toDo.remove(0)
            val newProd = doProd(prod)
            done += newProd
        }

        return done.toList
    }

    def apply(gram: Grammar) = {
        require(gram != null)
        gram match {
            case Grammar(prods) => {
                Grammar(doProds(prods))
            }
        }
    }

    def displayName = "To Standard Top-Down Form"
}