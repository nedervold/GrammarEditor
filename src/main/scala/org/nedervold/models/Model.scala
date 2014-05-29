package org.nedervold.models

import scala.swing.Publisher
import scala.swing.event.Event
import scala.language.existentials

/**
 * Holds a value that may change over time.
 */
trait Model[T] extends Publisher {
    def value: T

    // There is no reason for Models to listen to their own changes.
    deafTo(this)
}

/**
 * Holds a value that may be set.
 */
trait VariableModel[T] extends Model[T] {
    def value_=(newValue: T);
}

/**
 * Signals that a model's value has changed.
 */
case class ModelChangedEvent(val source: Model[_]) extends Event {}