package java_cup

import org.nedervold.grammareditor.grammar._;

object JavaCupSpec {
    def termToSpec(t: Term): String = {
        // TODO Literal terminals need to be converted
        t.toString
    }

    def rhsToSpec(rhs: Option[Term]): String = {
        termToSpec(rhs.getOrElse(Epsilon))
    }

    def productionToSpec(p: Production): String = {
        p.lhs.toString + " ::= " + rhsToSpec(p.rhs) + ";\n"
    }

    def grammarToSpec(g: Grammar): String = {
        val ts = if (g.terminals.isEmpty) "" else "terminal " + g.terminals.mkString(", ") + ";\n"
        val nts = if (g.nonterminals.isEmpty) "" else "non terminal " + g.nonterminals.mkString(", ") + ";\n"
        val prods = g.productions.map(productionToSpec(_)).mkString("\n");
        // TODO Implement this
        val res = s"${ts}\n${nts}\n${prods}"
        println(res)
        res
    }
}