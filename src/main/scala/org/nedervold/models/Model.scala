package org.nedervold.models

import scala.swing.Publisher
import scala.swing.event.Event
import scala.language.existentials

/**
 * Holds a value that may change over time.
 * @author nedervold
 */
trait Model[T] extends Publisher {
    /**
     * the value of the [[Model]]
     */
    def value: T

    /**
     * A [[ModelChangedEvent]] for this model
     */
    lazy val event = new ModelChangedEvent(this)

    // There is no reason for Models to listen to their own changes.
    deafTo(this)
}

/**
 * Signals that a [[Model]]'s value has changed.
 * @author nedervold
 * @constructor
 * @param source the [[Model]] whose value changed
 */
case class ModelChangedEvent(val source: Model[_]) extends Event {}