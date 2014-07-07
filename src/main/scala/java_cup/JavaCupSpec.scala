package java_cup

import org.nedervold.grammareditor.grammar.Grammar;

object JavaCupSpec {
    def grammarToSpec(g: Grammar): String = {
         // TODO Implement this
        return """non terminal foo;
        foo ::= ;"""
    }
}