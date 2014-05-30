package org.nedervold.grammareditor.models.viewcontrollers

import org.scalatest.FlatSpec

class DocumentViewControllerSpec extends FlatSpec {
    behavior of "A DocumentViewController"

    it should "require a non-null VariableModel" in {
        intercept[NullPointerException] {
            new DocumentViewController(null)
        }
    }
}