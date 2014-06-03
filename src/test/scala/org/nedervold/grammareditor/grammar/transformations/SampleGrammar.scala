package org.nedervold.grammareditor.grammar.transformations

import org.nedervold.grammareditor.grammar.GrammarParser

object SampleGrammar {
    val src = """grammar ::= {production}+.
	    	| production ::= NT [rhs] ".".
		| rhs ::= "::=" alternatives.
		| alternatives ::= {alternative ... "|"}+.
		| alternative ::= {term}.
		| term ::= NT | T | optional | repetition | at_least_one | group.
		| optional ::= "[" alternatives "]".
		| repetition ::= "{" term [separator_part] closer.
		| separator_part ::= "..." term.
		| closer ::= "}" | "}+".
		| at_least_one ::= "<<" terms ">>".
		| terms ::= {term}+.
		| group ::= "(" alternatives ")".""".stripMargin

    val grammar = GrammarParser.parseGrammar(src).get
}