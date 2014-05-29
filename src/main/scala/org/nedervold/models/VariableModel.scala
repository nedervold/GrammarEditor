package org.nedervold.models

/**
 * Holds a value that may be set.
 * @author nedervold
 */
trait VariableModel[T] extends Model[T] {
    def value_=(newValue: T);
}

