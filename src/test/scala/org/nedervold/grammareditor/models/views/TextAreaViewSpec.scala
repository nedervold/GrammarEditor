package org.nedervold.grammareditor.models.views

import org.scalatest.FlatSpec
import org.nedervold.grammareditor.models.VarModel

class TextAreaViewSpec extends FlatSpec {
    behavior of "a TextViewSpec"

    it should "require a non-null Model" in {
        intercept[IllegalArgumentException] {
            new TextAreaView(null)
        }
    }

    it should "require a non-null unparse function" in {
        var model = new VarModel(5)
        intercept[IllegalArgumentException] {
            new TextAreaView(model, null)
        }
    }
}