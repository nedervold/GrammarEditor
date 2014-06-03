package org.nedervold.grammareditor.grammar.transformations

import org.nedervold.grammareditor.grammar.alternate
import org.nedervold.grammareditor.grammar._

/**
 * Returns a grammar with extended BNF symbols expanded into their BNF equivalents
 * such that all productions in the final grammar are alternatives of sequences of
 * nonterminals and terminals.  Expansion is done to avoid right recursion and
 * optional structures are expanded inline.
 *
 * @author nedervold
 */
object StandardBottomUpForm extends GrammarTransformation {
    override val accelerator = mkAccelerator('B')

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
                case Optional(t) => Optional(toSeqElmt(t))
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
                case Repetition0(t) => {
                    val nt = synthNonterminal("rep0", term)
                    val newT = toSeqElmt(t)
                    toDo += new Production(nt, alternate(sequence(nt, newT), newT))
                    Optional(nt)
                }
                case Repetition1(t) => {
                    val nt = synthNonterminal("rep1", term)
                    val newT = toSeqElmt(t)
                    toDo += new Production(nt, alternate(sequence(nt, newT), newT))
                    nt
                }
                case RepetitionSep0(b, s) => {
                    val nt = synthNonterminal("repsep0", term)
                    val newB = toSeqElmt(b)
                    val newS = toSeqElmt(s)
                    toDo += new Production(nt, alternate(sequence(nt, newS, newB), newB))
                    Optional(nt)
                }
                case RepetitionSep1(b, s) => {
                    val nt = synthNonterminal("repsep1", term)
                    val newB = toSeqElmt(b)
                    val newS = toSeqElmt(s)
                    toDo += new Production(nt, alternate(sequence(nt, newS, newB), newB))
                    nt
                }
                // case AtLeastOne(ts) => AtLeastOne(ts.map(toSeqElmt))
                case _ => term
            }
        }

        def toSeq(term: Term): List[Term] = {
            term match {
                case Epsilon => List()
                case Sequenced(lhs, rhs) => toSeq(lhs) ++ toSeq(rhs)
                case _ => List(term)
            }
        }

        def toAlts(term: Term): List[Term] = {
            term match {
                case Fail => List()
                case Or(lhs, rhs) => toAlts(lhs) ++ toAlts(rhs)
                case _ => List(term)
            }
        }

        def allCombos(terms: List[Term]): List[List[Term]] = {
            terms match {
                case Nil => List(Nil)
                case t :: ts => {
                    val tails = allCombos(ts)
                    tails.map(t :: _) ++ tails
                }
            }
        }

        def flushOpts(seq: List[Term]): List[List[Term]] = {
            seq match {
                case Nil => List(Nil)
                case Optional(t) :: ts => {
                    val tails = flushOpts(ts)
                    val withs = tails.map(t :: _)
                    withs ++ tails
                }

                //                case AtLeastOne(ts) :: ts2 => {
                //                    val heads = allCombos(ts).filterNot(_.isEmpty)
                //                    val tails = flushOpts(ts2)
                //                    for (hd <- heads; tl <- tails)
                //                        yield hd ++ tl
                //
                //                }

                case t :: ts => {
                    flushOpts(ts).map(t :: _)
                }
            }
        }

        def doSeq(term: Term): List[Term] = { flushOpts(toSeq(term).map(toSeqElmt)).map(sequence) }

        def doAlts(rhs: Term) = { alternate(toAlts(rhs).flatMap(doSeq)) }

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

    def displayName = "To Standard Bottom-Up Form"

}