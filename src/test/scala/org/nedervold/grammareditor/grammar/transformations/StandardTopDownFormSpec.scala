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
        assert(!SampleGrammars.grammar1.isInStandardForm)
        assert(StandardTopDownForm(SampleGrammars.grammar1).isInStandardForm)

        assert(!SampleGrammars.grammar2.isInStandardForm)
        assert(StandardTopDownForm(SampleGrammars.grammar2).isInStandardForm)
    }
}
