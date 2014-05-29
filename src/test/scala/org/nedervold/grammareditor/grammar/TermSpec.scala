package org.nedervold.grammareditor.grammar

import org.scalatest.FlatSpec

class TermSpec extends FlatSpec {
    behavior of "A Terminal"

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
}