package org.nedervold.grammareditor.models.adapters

import scala.swing.Reactor

import org.nedervold.grammareditor.models.ModelChangedEvent
import org.nedervold.grammareditor.models.VariableModel
import org.scalatest.FlatSpec

import javax.swing.text._

class DocumentAdapterSpec extends FlatSpec {
    behavior of "a DocumentAdapter"

    it should "require a non-null document" in {
        intercept[IllegalArgumentException] {
            new DocumentAdapter(null)
        }
    }

    it should "contain a non-null document even if it wasn't given one" in {
        assert(new DocumentAdapter().document !== null)
        val doc = new PlainDocument()
        assert(new DocumentAdapter(doc).document !== null)
    }

    it should "contain the wrapped document" in {
        val doc = new PlainDocument()
        assert(new DocumentAdapter(doc).document === doc)
    }

    def getDocText(doc: AbstractDocument): String = doc.getText(0, doc.getLength)

    it should "reflect changes in the model" in {
        val doc = new PlainDocument()
        val model: VariableModel[String] = new DocumentAdapter(doc)
        model.value = "foo"
        assert(getDocText(doc) === "foo")
    }

    it should "transmit its changes to the model" in {
        val doc = new PlainDocument()
        val model: VariableModel[String] = new DocumentAdapter(doc)

        var changed = false
        var reactor = new Reactor {
            reactions += { case ModelChangedEvent(`model`) => changed = true }
        }
        reactor.listenTo(model)
        doc.insertString(0, "foo", null)
        assert(model.value === "foo")
        assert(changed)
    }

    it should "have the model send as many events as changes" in {
        val doc = new PlainDocument()
        doc.insertString(0, "foo", null)
        val model: VariableModel[String] = new DocumentAdapter(doc)

        var eventsHeard = 0
        var reactor = new Reactor {
            reactions += { case ModelChangedEvent(`model`) => eventsHeard += 1 }
        }
        reactor.listenTo(model)

        assert(eventsHeard === 0)
        model.value = "foo"
        assert(eventsHeard === 0)
        model.value = "foobar"
        assert(eventsHeard === 1)
    }
}