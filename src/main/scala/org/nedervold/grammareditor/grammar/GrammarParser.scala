package org.nedervold.grammareditor.grammar

import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.input.Position
import scala.util.Try
import scala.util.Success
import scala.util.Failure

/**
 * Parses source text into [[Grammar]]s (or not).
 */
object GrammarParser {
    /**
     * Parses the source text
     * @param input the source text
     * @return the grammar or an Exception
     */
    def parseGrammar(input: String): Try[Grammar] = {
        Impl.parseAll(Impl.grammar, input) match {
            case Impl.Success(g, _) => Success(g)
            case Impl.NoSuccess(msg, next) => {
                val pos: Position = next.pos
                val msg2 = msg + " on line " + pos.line
                Failure(new Exception(msg2))
            }
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

        private def term: Parser[Term] = nonterminal | terminal | optional | repetition | atLeastOne

        private def nonterminal = """[a-z][a-z0-9_]*""".r ^^ (Nonterminal(_))

        private def terminal = namedTerminal | literalTerminal
        private def namedTerminal = """[A-Z][A-Z0-9_]*""".r ^^ (Terminal(_))
        private def literalTerminal = """["][^"]+["]""".r ^^ (Terminal(_))

        private def optional = "[" ~> alternatives <~ "]" ^^ (Optional(_))

        /*
         * To make the grammar easy to parse top-down, we rewrite 
         * 
         * repetition ::= "{" term "}"
         * 				| "{" term "}+"    
         *       		| "{" term "..." term "}"
         *       		| "{" term "..." term "}+".
         * 
         * into an equivalent but LL(1) form by left-factoring.
         */
        private def repetition = "{" ~> term ~ repetitionTail ^^ {
            case body ~ repTail => repTail(body)
        }

        private def repetitionTail = repsepTail | repTail
        private def repsepTail: Parser[Term => Term] = "..." ~> term ~ repCloser ^^ {
            case sep ~ 0 => RepetitionSep0(_, sep)
            case sep ~ 1 => RepetitionSep1(_, sep)
        }
        private def repTail: Parser[Term => Term] = repCloser ^^ {
            case 0 => Repetition0(_)
            case 1 => Repetition1(_)
        }

        private def repCloser: Parser[Int] = "}+" ^^^ { 1 } | "}" ^^^ { 0 }

        private def atLeastOne: Parser[Term] = "<<" ~> rep1(term) <~ ">>" ^^ { AtLeastOne(_) }
    }
}