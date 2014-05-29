package org.nedervold.grammareditor.models

import scala.swing.Reactor

import org.scalatest.FlatSpec

class FmapModelSpec extends FlatSpec {
    behavior of "An FmapModel"

    it should "return the value of its function applied to the value of the base model" in {
        val constModel = new ConstModel(5)
        def square(n: Int) = n * n
        val fmapModel = new FmapModel(square, constModel)
        assert(fmapModel.value == 25)
    }

    it should "change value when its base model changes value" in {
        val varModel = new VarModel(5)
        def square(n: Int) = n * n
        val fmapModel = new FmapModel(square, varModel)
        varModel.value = 3
        assert(fmapModel.value == 9)
    }

    it should "publish an event when the new value is different" in {
        val varModel = new VarModel(5)
        def square(n: Int) = n * n
        val fmapModel = new FmapModel(square, varModel)
        var heard = false
        val reactor = new Reactor {
            reactions += { case ModelChangedEvent(`fmapModel`) => heard = true }
        }
        reactor.listenTo(fmapModel)

        assert(!heard)
        varModel.value = 3
        assert(heard)
    }

    it should "not publish an event when the new value is the same" in {
        val varModel = new VarModel(5)
        def square(n: Int) = n * n
        val fmapModel = new FmapModel(square, varModel)
        var heard = false
        val reactor = new Reactor {
            reactions += { case ModelChangedEvent(`fmapModel`) => heard = true }
        }
        reactor.listenTo(fmapModel)

        assert(!heard)
        varModel.value = -5
        assert(!heard)
    }

    /* TODO Tests for the rest of the FmapNModels.  They're mostly boilerplate.  
     * I need to learn how to  make boilerplate in FlatSpecs.
     */

}