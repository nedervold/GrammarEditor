package org.nedervold.grammareditor.models.views

import org.nedervold.grammareditor.models.Model

/**
 * A component that displays the contents of a [[Model]].
 *
 * @author nedervold
 */
trait View[T] {
    /**
     * The [[Model]] whose contents are displayed
     */
    def model: Model[T]
}