package org.nedervold.models

import org.scalatest.FlatSpec
import scala.swing.Reactor

class VarModelSpec extends FlatSpec {
    behavior of "A VarModel"

    it should "return the value it was initialized with" in {
        val value = 5
        val varModel = new VarModel(value)
        assert(varModel.value == value)
    }

    it should "return a new value after a new value is set" in {
        val value = 5
        val varModel = new VarModel(value)
        val newValue = -3
        varModel.value = newValue
        assert(varModel.value == newValue)
    }

    it should "publish an event when the new value is different" in {
        val value = 5
        val varModel = new VarModel(value)
        var heard = false
        val reactor = new Reactor {
            reactions += { case ModelChangedEvent(`varModel`) => heard = true }
        }
        reactor.listenTo(varModel)

        assert(!heard)
        varModel.value = -3
        assert(heard)
    }

    it should "not publish an event when the new value is the same" in {
        val value = 5
        val varModel = new VarModel(value)
        var heard = false
        val reactor = new Reactor {
            reactions += { case ModelChangedEvent(`varModel`) => heard = true }
        }
        reactor.listenTo(varModel)

        assert(!heard)
        varModel.value = value
        assert(!heard)
    }

    it should "use its elmtEqual predicate to decide whether values are the same" in {
        def sameParity(lhs: Int, rhs: Int) = lhs % 2 == rhs % 2
        val varModel = new VarModel(5, sameParity)
        var heard = false
        val reactor = new Reactor {
            reactions += { case ModelChangedEvent(`varModel`) => heard = true }
        }
        reactor.listenTo(varModel)

        assert(!heard)
        varModel.value = 5
        assert(!heard)
        varModel.value = 7
        assert(!heard)
        varModel.value = 8
        assert(heard)
        heard = false
        varModel.value = 0
        assert(!heard)
        varModel.value = 1
        assert(heard)
        heard = false
    }
}