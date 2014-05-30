package org.nedervold.grammareditor.grammar.transformations

import org.scalatest.FlatSpec
import org.nedervold.grammareditor.grammar.Grammar
import org.nedervold.grammareditor.grammar.Production
import org.nedervold.grammareditor.grammar.Nonterminal
import org.nedervold.grammareditor.grammar.Term
import scala.collection.immutable.TreeSet
import org.nedervold.grammareditor.grammar.Epsilon

class AlphabeticSortTransformationSpec extends FlatSpec {
    behavior of "an AlphabeticSortTransformation"

    it should "require a non-null grammar" in {
        intercept[IllegalArgumentException] {
            AlphabeticSortTransformation(null)
        }
    }

    /* 
     * TODO 2014-5-30 This test is more of a property test and 
     * should rather be done in ScalaCheck.
     */

    it should "change only the order of the productions" in {
        val gram = Grammar(List(new Production(Nonterminal("a"), Nonterminal("b")),
            new Production(Nonterminal("c"), Nonterminal("a")),
            new Production(Nonterminal("b"), Epsilon)))
        val newGram = AlphabeticSortTransformation(gram)
        assert(Set(gram.productions: _*) === Set(newGram.productions: _*))
    }

}