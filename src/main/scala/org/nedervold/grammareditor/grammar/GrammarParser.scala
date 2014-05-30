package org.nedervold.grammareditor.grammar

import scala.util.parsing.combinator.RegexParsers

/**
 * Parses source text into [[Grammar]]s (or not).
 */
object GrammarParser {
    /**
     * Parses the source text
     * @param input the source text
     * @return Left(msg) if the parse failed; Right(grammar) if the parse succeeds
     */
    def parseGrammar(input: String): Either[String, Grammar] = {
        Impl.parseAll(Impl.grammar, input) match {
            case Impl.Success(g, _) => Right(g)
            case Impl.NoSuccess(msg, _) => Left(msg)
        }
    }

    /**
     * Hides the mechanism of parsing from the user, giving a clean interface.
     *
     * TODO 2014-05-29 Do I really want to hide this all?  It means I can't test the parsing
     * routines.  How testable are they?
     *
     * @author nedervold
     */
    private object Impl extends RegexParsers {

        private[GrammarParser] def grammar = rep1(production) ^^ ((ps) => new Grammar(ps)) | failure("Empty grammar")
        private def production = nonterminal ~ opt(rhs) <~ "." ^^ {
            case nonterminal ~ optRhs => Production(nonterminal, optRhs)
        }

        private def rhs = "::=" ~> alternatives 

        private def alternatives: Parser[Term] = rep1sep(sequence_, "|") ^^ { alternate(_) }

        /*
         * "Sequence" can be a verb or a noun, so we append an underscore
         * here (to the noun) to avoid clashing with the package function (the verb).
         */
        private def sequence_ : Parser[Term] = rep(term) ^^ { sequence(_) }

        private def term: Parser[Term] = nonterminal | terminal

        private def nonterminal = """[a-z][a-z0-9_]*""".r ^^ (Nonterminal(_))

        private def terminal = namedTerminal | literalTerminal
        private def namedTerminal = """[A-Z][A-Z0-9_]*""".r ^^ (Terminal(_))
        private def literalTerminal = """["][^"]+["]""".r ^^ (Terminal(_))
    }
}