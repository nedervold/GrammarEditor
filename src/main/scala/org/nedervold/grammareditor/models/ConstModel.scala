package org.nedervold.grammareditor.models

/**
 * A constant [[Model]].
 *
 * @author nedervold
 */
class ConstModel[T](override val value: T) extends Model[T]