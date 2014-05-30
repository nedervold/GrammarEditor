package org.nedervold.grammareditor.models

import org.scalatest.FlatSpec

class ConstModelSpec extends FlatSpec {
    behavior of "a ConstModel"

    it should "return the value it was initialized with" in {
        val value = 5
        val constModel = new ConstModel(value)
        assert(constModel.value === value)
    }
}