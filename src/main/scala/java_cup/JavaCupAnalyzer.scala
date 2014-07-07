package java_cup

import org.nedervold.grammareditor.grammar.analysis._
import org.nedervold.grammareditor.grammar.Grammar;

object JavaCupAnalyzer extends Lalr1Analyzer {
    def analyze(g: Grammar): JavaCupAnalysis = new JavaCupAnalysis(g)
}

class JavaCupAnalysis(val grammar: Grammar) extends Lalr1Analysis {
    val spec = JavaCupSpec.grammarToSpec(grammar)
    val message = {
        try {
            NewMain.run(spec)
        } catch {
            case e: Exception => e.getMessage()
        }
    }
    def reduceReduceCount: Int = NewMain.reduceReduceCount
    def shiftReduceCount: Int = NewMain.shiftReduceCount
    override def toString: String = {
        (if (isLalr1) "Yay!  It's LALR(1)!" else message) + "\n----\n" + spec
    }
}