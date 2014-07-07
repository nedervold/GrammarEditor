package org.nedervold.grammareditor.grammar.analysis

import org.nedervold.grammareditor.grammar.Grammar;

trait Lalr1Analysis {
    def isLalr1: Boolean = { reduceReduceCount + shiftReduceCount == 0 }
    def reduceReduceCount: Int
    def shiftReduceCount: Int
    def message: String
    override def toString: String = {
        if (isLalr1) "Yay!  It's LALR(1)!" else message
    }
}

trait Lalr1Analyzer {
    def analyze(g: Grammar): Lalr1Analysis
}