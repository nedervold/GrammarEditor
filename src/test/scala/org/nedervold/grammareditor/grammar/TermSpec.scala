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
        assert(new Terminal(name).toString === name)
    }

    it should "contain no nonterminals" in {
        assert(new Terminal("term").nonterminals.isEmpty)
    }

    it should "contain only itself as terminal" in {
        val terminal = new Terminal("term");
        assert(terminal.terminals === Set(terminal))
    }

    behavior of "A Nonterminal"

    it should "require a name" in {
        intercept[IllegalArgumentException] {
            new Nonterminal(null)
        }
    }

    it should "print as its name" in {
        val name = "bob"
        assert(new Nonterminal(name).toString === name)
    }

    it should "contain no terminals" in {
        assert(new Nonterminal("nonterm").terminals.isEmpty)
    }

    it should "contain only itself as nonterminal" in {
        val nonterminal = new Nonterminal("nonterm");
        assert(nonterminal.nonterminals === Set(nonterminal))
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

    behavior of "An Optional term"

    it should "require a body" in {
        intercept[IllegalArgumentException] {
            new Optional(null)
        }
    }

    behavior of "A Repetition0 term"

    it should "require a body" in {
        intercept[IllegalArgumentException] {
            new Repetition0(null)
        }
    }

    behavior of "A Repetition1 term"

    it should "require a body" in {
        intercept[IllegalArgumentException] {
            new Repetition1(null)
        }
    }

    behavior of "A RepetitionSep0 term"

    it should "require a non-null body" in {
        intercept[IllegalArgumentException] {
            val t = new Terminal("T")
            new RepetitionSep0(null, t)
        }
    }

    it should "require a non-null separator" in {
        intercept[IllegalArgumentException] {
            val t = new Terminal("T")
            new RepetitionSep0(null, t)
        }
    }

    behavior of "A RepetitionSep1 term"

    it should "require a non-null body" in {
        intercept[IllegalArgumentException] {
            val t = new Terminal("T")
            new RepetitionSep1(null, t)
        }
    }

    it should "require a non-null separator" in {
        intercept[IllegalArgumentException] {
            val t = new Terminal("T")
            new RepetitionSep1(null, t)
        }
    }

}