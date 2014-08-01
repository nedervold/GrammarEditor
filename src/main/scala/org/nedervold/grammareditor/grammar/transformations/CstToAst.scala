package org.nedervold.grammareditor.grammar.transformations

import org.nedervold.grammareditor.grammar._

/**
 * Returns a grammar with the productions sorted alphabetically by head (except the first,
 * assumed to be the start symbol).
 * @author nedervold
 */
object CstToAst extends GrammarTransformation {
    def apply(gram: Grammar): Grammar = {
        require(gram != null)
        gram match {
            case Grammar(prods) => {
                Grammar(reduceOptReps(inlineUnitaries(doProds(prods))))
            }
        }
    }

    private def reduceOptReps(prods: List[Production]): List[Production] = {
//        def isRep1(prod: Production): Boolean = {
//            prod match {
//                case Production(_, Some(Repetition1(_))) => true
//                case _ => false
//            }
//        }
//        val (rep1s, others) = prods.partition(isRep1)
//        // need to reduce [nt] to {b} whenever nt ::= {b}+.
//        throw new Exception("unimplemented")
        
        prods
    }

    private def isUnitary(prod: Production): Boolean = {
        prod match {
            case Production(lhs, Some(Epsilon)) => true
            case Production(lhs, Some(Terminal(_))) => true
            case Production(lhs, Some(Nonterminal(_))) => true
            case _ => false
        }
    }

    private def inlineUnitaries(prods: List[Production]): List[Production] = {
        val (unitaries, nonunitaries) = prods.partition(isUnitary)
        def toPair(prod: Production): (Nonterminal, Term) = {
            (prod.lhs, prod.rhs.get)
        }
        val unitaryTable: Map[Nonterminal, Term] = unitaries.map(toPair).toMap

        def replaceUnitary(t: Term): Term = {
            t match {
                case Nonterminal(_) => {
                    unitaryTable.get(t.asInstanceOf[Nonterminal]) match {
                        case None => t
                        case Some(t2) => replaceUnitary(t2)
                    }
                }
                case _ => t
            }
        }

        def replaceUnitaryInTerm(t: Term): Term = {
            t match {
                case Nonterminal(_) => replaceUnitary(t)
                case Optional(b) => Optional(replaceUnitaryInTerm(b))
                case Repetition0(b) => Repetition0(replaceUnitaryInTerm(b))
                case Repetition1(b) => Repetition1(replaceUnitaryInTerm(b))
                case RepetitionSep0(b, s) => RepetitionSep0(replaceUnitaryInTerm(b), replaceUnitaryInTerm(s))
                case RepetitionSep1(b, s) => RepetitionSep1(replaceUnitaryInTerm(b), replaceUnitaryInTerm(s))
                case Or(lhs, rhs) => Or(replaceUnitaryInTerm(lhs), replaceUnitaryInTerm(rhs))
                case Sequenced(lhs, rhs) => Sequenced(replaceUnitaryInTerm(lhs), replaceUnitaryInTerm(rhs))
                case AtLeastOne(ts) => AtLeastOne(ts.map(replaceUnitaryInTerm))
                case _ => t
            }
        }

        def replaceUnitaryInProd(prod: Production): Production = {
            prod match {
                case Production(lhs, Some(rhs)) => Production(lhs, Some(replaceUnitaryInTerm(rhs)))
                case Production(lhs, None) =>
                    System.exit(1); prod
                case _ => prod
            }
        }
        nonunitaries.map(replaceUnitaryInProd)
    }

    private def doProds(prods: Seq[Production]): List[Production] = {
        def isFixedTerminal(term: Term): Boolean = {
            term match {
                case Terminal(name) => name.charAt(0) == '"'
                case _ => false
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

        def mark(s: String): String = { s.toUpperCase() + "_BASE_TYPE" }

        def doTerm(term: Term): Term = {
            term match {
                case Optional(body) => {
                    if (isFixedTerminal(body)) {
                        Terminal(mark("bool"))
                    } else {
                        term
                    }
                }
                case Repetition0(body) => {
                    if (isFixedTerminal(body)) {
                        Terminal(mark("enum"))
                    } else {
                        term
                    }
                }
                case Repetition1(body) => {
                    if (isFixedTerminal(body)) {
                        Terminal(mark("enum"))
                    } else {
                        term
                    }
                }
                case RepetitionSep0(body, sep) => {
                    if (isFixedTerminal(sep)) {
                        doTerm(Repetition0(body))
                    } else {
                        term
                    }
                }
                case RepetitionSep1(body, sep) => {
                    if (isFixedTerminal(sep)) {
                        doTerm(Repetition1(body))
                    } else {
                        term
                    }
                }
                case _ => term
            }
        }

        def doSeq(seq: List[Term]): List[Term] = {
            seq.filterNot(isFixedTerminal).map(doTerm)
        }

        def doAlt(alt: Term): Term = {
            val oldSeq = toSeq(alt)
            val newSeq = doSeq(oldSeq)
            sequence(newSeq)
        }

        def isEmptyAlt(alt: Term): Boolean = {
            alt == Epsilon
        }

        def doAlts(rhs: Term) = {
            val oldAlts: List[Term] = toAlts(rhs)
            val newAlts: List[Term] = oldAlts.map(doAlt)
            if (newAlts.forall(isEmptyAlt)) {
                newAlts.length match {
                    case 0 => Fail
                    case 1 => Terminal(mark("unit"))
                    case 2 => Terminal(mark("bool"))
                    case _ => Terminal(mark("enum"))
                }
            } else {
                alternate(newAlts)
            }
        }

        def doProd(prod: Production): Production = {
            prod match {
                case Production(lhs, optRhs) => Production(lhs, optRhs.map(doAlts))
            }
        }

        prods.map(doProd).toList
    }
    val displayName = "CST → AST"
}