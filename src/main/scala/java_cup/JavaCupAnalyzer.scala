package java_cup

import org.nedervold.grammareditor.grammar.analysis._
import org.nedervold.grammareditor.grammar.Grammar;

object JavaCupAnalyzer extends Lalr1Analyzer {
    def analyze(g: Grammar): JavaCupAnalysis = new JavaCupAnalysis(g)
}

class JavaCupAnalysis(val grammar: Grammar) extends Lalr1Analysis {
    val message = NewMain.run(JavaCupSpec.grammarToSpec(grammar))
    def reduceReduceCount: Int = NewMain.reduceReduceCount
    def shiftReduceCount: Int = NewMain.shiftReduceCount
}