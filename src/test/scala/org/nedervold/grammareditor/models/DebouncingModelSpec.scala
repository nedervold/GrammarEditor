package org.nedervold.grammareditor.models

import java.util.concurrent.TimeUnit

import scala.swing.Reactor

import org.scalatest.FlatSpec
import org.scalatest.DoNotDiscover

@DoNotDiscover
class DebouncingModelSpec extends FlatSpec {
    behavior of "A DebouncingModel"

    /*
	 * All of these tests are problematic in that they use the system clock and multithreading.
	 * Any could fail if threads are delayed.  I could set up a Clock parameter to fake
	 * the passage of time 
	 * 
	 * http://www.javacodegeeks.com/2013/07/fake-system-clock-pattern-in-scala-with-implicit-parameters.html
	 * 
	 * but the bang for the buck is too low in this case. We'll just hope that the runtime is 
	 * well enough behaved.
	 *
	 * 2014-05-29 Disabled the tests because the threading isn't well-behaved on Travis CI.
	 */

    it should "require non-null base model and function" in {
        val varModel = new VarModel(5)
        intercept[NullPointerException] {
            new DebouncingModel(varModel, 5, null)
        }
        intercept[NullPointerException] {
            new DebouncingModel(null, 5, TimeUnit.SECONDS)
        }
    }

    it should "return the value of its base model" in {
        val value = 5
        val varModel = new VarModel(value)
        val debouncingModel = new DebouncingModel(varModel, 5, TimeUnit.MILLISECONDS)
        assert(debouncingModel.value == value)
    }

    it should "return a new value after a new value is set" in {
        val value = 5
        val varModel = new VarModel(value)
        val debouncingModel = new DebouncingModel(varModel, 5, TimeUnit.MILLISECONDS)
        val newValue = 6
        varModel.value = newValue

        /* 
         * Hopefully this is long enough for the debounce timer to kick in.
         * We sleep twice as long.
         */
        Thread.sleep(20)
        assert(debouncingModel.value == newValue)
    }

    it should "publish an event when the new value is different" in {
        val value = 5
        val varModel = new VarModel(value)
        val debouncingModel = new DebouncingModel(varModel, 5, TimeUnit.MILLISECONDS)
        var heard = false
        val reactor = new Reactor {
            reactions += { case ModelChangedEvent(`debouncingModel`) => heard = true }
        }
        reactor.listenTo(debouncingModel)

        assert(!heard)
        varModel.value = -3
        Thread.sleep(20)
        assert(heard)
    }

    it should "not publish an event when the new value is the same" in {
        val value = 5
        val varModel = new VarModel(value)
        val debouncingModel = new DebouncingModel(varModel, 5, TimeUnit.MILLISECONDS)
        var heard = false
        val reactor = new Reactor {
            reactions += { case ModelChangedEvent(`debouncingModel`) => heard = true }
        }
        reactor.listenTo(debouncingModel)

        assert(!heard)
        varModel.value = 5
        Thread.sleep(20)
        assert(!heard)
    }

    it should "use its elmtEqual predicate to decide whether values are the same" in {
        def sameParity(lhs: Int, rhs: Int) = lhs % 2 == rhs % 2
        val varModel = new VarModel(5, sameParity)
        val debouncingModel = new DebouncingModel(varModel, 5, TimeUnit.MILLISECONDS)
        var heard = false
        val reactor = new Reactor {
            reactions += { case ModelChangedEvent(`debouncingModel`) => heard = true }
        }
        reactor.listenTo(debouncingModel)

        assert(!heard)
        varModel.value = 5
        Thread.sleep(20)
        assert(!heard)
        varModel.value = 7
        Thread.sleep(20)
        assert(!heard)
        varModel.value = 8
        Thread.sleep(20)
        assert(heard)
        heard = false
        varModel.value = 0
        Thread.sleep(20)
        assert(!heard)
        varModel.value = 1
        Thread.sleep(20)
        assert(heard)
        heard = false
    }

    it should "debounce" in {
        val varModel = new VarModel(false)
        val debouncingModel = new DebouncingModel(varModel, 5, TimeUnit.MILLISECONDS)
        var eventsHeard = 0
        val reactor = new Reactor {
            reactions += { case ModelChangedEvent(`debouncingModel`) => eventsHeard += 1 }
        }
        reactor.listenTo(debouncingModel)

        for (i <- 1 to 101) {
            // We hope that the calls are not spaced more than 5μs apart
            varModel.value = !varModel.value;
        }
        Thread.sleep(20)
        assert(eventsHeard == 1)
    }
}