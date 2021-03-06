package org.nedervold.grammareditor.models.views

import org.scalatest.FlatSpec

class DocumentViewSpec extends FlatSpec {
    behavior of "a DocumentView"

    it should "require a non-null Model" in {
        intercept[NullPointerException] {
            new DocumentView(null)
        }
    }
}