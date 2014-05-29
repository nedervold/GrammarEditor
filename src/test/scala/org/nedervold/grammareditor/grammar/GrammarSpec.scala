package org.nedervold.grammareditor.grammar

import org.scalatest.FlatSpec

class GrammarSpec extends FlatSpec {
    behavior of "A Grammar"

    it should "require non-empty productions" in {
        intercept[IllegalArgumentException] {
            new Grammar(null)
        }
        intercept[IllegalArgumentException] {
            new Grammar(List())
        }
    }

}