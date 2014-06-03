package org.nedervold.grammareditor.grammar.transformations

import org.scalatest.FlatSpec

class StandardBottomUpFormSpec extends FlatSpec {

    behavior of "an DepthFirstSort"

    it should "require a non-null grammar" in {
        intercept[IllegalArgumentException] {
            StandardBottomUpForm(null)
        }
    }

    it should "result in standard form" in {
        assert(!SampleGrammars.grammar1.isInStandardForm)
        assert(StandardBottomUpForm(SampleGrammars.grammar1).isInStandardForm)

        assert(!SampleGrammars.grammar2.isInStandardForm)
        assert(StandardBottomUpForm(SampleGrammars.grammar2).isInStandardForm)
    }
}
