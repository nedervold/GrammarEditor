package org.nedervold.grammareditor.grammar

import org.scalatest.FlatSpec

class GrammarSpec extends FlatSpec {
    behavior of "A Grammar"

    it should "require non-empty productions" in {
        intercept[IllegalArgumentException] {
            new Grammar(null)
        }
        intercept[IllegalArgumentException] {
            new Grammar(List())
        }
    }

    it should "be consistently defined or not" in {
        val a = new Nonterminal("a")
        val b = new Nonterminal("b")
        val c = new Nonterminal("c")

        val prod = new Production(a, b)
        val prod2 = new Production(b, a)
        val undefProd = new Production(c)

        assert(Grammar(List(prod, prod2)).isDefined)
        assert(!Grammar(List(prod, undefProd)).isDefined)
        assert(!Grammar(List(prod, prod2, undefProd)).isDefined)
    }
}