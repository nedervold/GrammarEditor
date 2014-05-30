package org.nedervold.grammareditor.grammar.transformations

import org.scalatest.FlatSpec
import org.nedervold.grammareditor.grammar.Grammar
import org.nedervold.grammareditor.grammar.Production
import org.nedervold.grammareditor.grammar.Nonterminal
import org.nedervold.grammareditor.grammar.Term
import scala.collection.immutable.TreeSet

class AddUndefinedProductionsTransformationSpec extends FlatSpec {
    behavior of "An AddUndefinedProductionsTransformation"

    it should "require a non-null grammar" in {
        intercept[NullPointerException] {
            AddUndefinedProductionsTransformation(null)
        }
    }

    /* 
     * TODO 2014-5-30 The next two tests are more property tests and 
     * should rather be done in ScalaCheck.
     */

    it should "contain productions for all nonterminals" in {
        val gram = Grammar(List(new Production(Nonterminal("a"), Nonterminal("b"))))
        val newGram = AddUndefinedProductionsTransformation(gram)
        def productionHeads(gram: Grammar): TreeSet[Nonterminal] = {
            new TreeSet[Nonterminal]() ++ gram.productions.map(_.lhs)
        }
        assert(newGram.nonterminals === productionHeads(newGram))
    }

    it should "not change undefined nonterminals" in {
        val gram = Grammar(List(new Production(Nonterminal("a"), Nonterminal("b"))))
        val newGram = AddUndefinedProductionsTransformation(gram)
        assert(gram.undefinedNonterminals === newGram.undefinedNonterminals)
    }
}