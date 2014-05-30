package org.nedervold.grammareditor.grammar

import org.scalatest.FlatSpec

class TermSpec extends FlatSpec {
    behavior of "A Terminal"

    it should "require a name" in {
        intercept[IllegalArgumentException] {
            new Terminal(null)
        }
    }

    it should "print as its name" in {
        val name = "BOB"
        assert(new Terminal(name).toString == name)
    }

    it should "contain no nonterminals" in {
        assert(new Terminal("term").nonterminals.isEmpty)
    }

    it should "contain only itself as terminal" in {
        val terminal = new Terminal("term");
        assert(terminal.terminals == Set(terminal))
    }

    behavior of "A Nonterminal"

    it should "require a name" in {
        intercept[IllegalArgumentException] {
            new Nonterminal(null)
        }
    }

    it should "print as its name" in {
        val name = "bob"
        assert(new Nonterminal(name).toString == name)
    }

    it should "contain no terminals" in {
        assert(new Nonterminal("nonterm").terminals.isEmpty)
    }

    it should "contain only itself as nonterminal" in {
        val nonterminal = new Nonterminal("nonterm");
        assert(nonterminal.nonterminals == Set(nonterminal))
    }

    behavior of "A Sequenced term"

    it should "require an lhs and rhs" in {
        val t = new Terminal("T")
        intercept[IllegalArgumentException] {
            new Sequenced(null, t)
        }
        intercept[IllegalArgumentException] {
            new Sequenced(t, null)
        }
    }

    behavior of "An Or term"

    it should "require an lhs and rhs" in {
        val t = new Terminal("T")
        intercept[IllegalArgumentException] {
            new Or(null, t)
        }
        intercept[IllegalArgumentException] {
            new Or(t, null)
        }
    }
}