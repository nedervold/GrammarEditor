package org.nedervold.grammareditor.grammar.transformations

import org.nedervold.grammareditor.grammar.Grammar
import org.nedervold.grammareditor.grammar.Production
import org.nedervold.grammareditor.grammar.Nonterminal

object DepthFirstSortTransformation extends GrammarTransformation {
    private def depthFirstSearch[N](roots: Seq[N], succ: (N) => List[N]): List[N] = {
        def go(roots: Seq[N], seen: Set[N]): List[N] = {
            roots match {
                case Nil => Nil
                case hd :: tl => if (seen.contains(hd)) {
                    go(tl, seen)
                } else {
                    hd :: go(succ(hd) ::: tl, seen + hd)
                }
            }
        }
        go(roots, Set[N]())
    }

    def apply(gram: Grammar): Grammar = {
        require(gram != null)
        val newGram = AddUndefinedProductionsTransformation(gram)
        val productions = newGram.productions

        def dfsProds: List[Production] = {
            val lhss: Seq[Nonterminal] = productions.map(_.lhs)
            def succ(nt: Nonterminal): List[Nonterminal] = {
                newGram(nt).get.collect { case nt@Nonterminal(_) => nt }.toList
            }

            depthFirstSearch(lhss, succ).map(newGram(_).get)
        }
        new Grammar(dfsProds);
    }

    val displayName = "Sort depth-first"
    override val accelerator = mkAccelerator('D')
}