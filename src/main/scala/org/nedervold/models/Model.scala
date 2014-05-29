package org.nedervold.models

import scala.swing.Publisher
import scala.swing.event.Event
import scala.language.existentials

/**
 * Holds a value that may change over time.
 * @author nedervold
 */
trait Model[T] extends Publisher {
    def value: T

    // There is no reason for Models to listen to their own changes.
    deafTo(this)
}

/**
 * Signals that a [[Model]]'s value has changed.
 * @author nedervold
 */
case class ModelChangedEvent(val source: Model[_]) extends Event {}