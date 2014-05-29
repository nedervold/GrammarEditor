package org.nedervold.grammareditor.grammar

import org.scalatest.FlatSpec

class ProductionSpec extends FlatSpec {
    behavior of "A Production"

    it should "require a left-hand side" in {
        intercept[IllegalArgumentException] {
            new Production(null)
        }
    }

    it should "require a (non-null) right-hand side" in {
        intercept[IllegalArgumentException] {
            val rhs: Option[Term] = null
            new Production(new Nonterminal("nt"), rhs)
        }

        intercept[IllegalArgumentException] {
            val rhs: Term = null
            new Production(new Nonterminal("nt"), rhs)
        }
    }

    it should "be consistently defined or not" in {
        val nt = new Nonterminal("nt")

        assert(new Production(nt, Some(nt)).isDefined)
        assert(!new Production(nt, None).isDefined)
        assert(!new Production(nt).isDefined)
        assert(new Production(nt, nt).isDefined)
    }

}