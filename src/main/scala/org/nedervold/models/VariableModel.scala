package org.nedervold.models

/**
 * Holds a value that may be set.
 * @author nedervold
 */
trait VariableModel[T] extends Model[T] {
    /**
     * Sets the value of the [[Model]].
     * @param newValue the new value
     */
    def value_=(newValue: T);
}

