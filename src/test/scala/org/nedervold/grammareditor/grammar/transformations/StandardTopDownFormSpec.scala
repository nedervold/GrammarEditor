package org.nedervold.grammareditor.grammar.transformations

import org.scalatest.FlatSpec

class StandardTopDownFormSpec extends FlatSpec {

    behavior of "an DepthFirstSort"

    it should "require a non-null grammar" in {
        intercept[IllegalArgumentException] {
            StandardTopDownForm(null)
        }
    }

    it should "result in standard form" in {
        assert(!SampleGrammar.grammar.isInStandardForm)
        assert(StandardTopDownForm(SampleGrammar.grammar).isInStandardForm)
    }
}