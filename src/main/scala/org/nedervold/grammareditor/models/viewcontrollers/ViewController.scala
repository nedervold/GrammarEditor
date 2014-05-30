package org.nedervold.grammareditor.models.viewcontrollers

import org.nedervold.grammareditor.models.views.View
import org.nedervold.grammareditor.models.VariableModel

trait ViewController[T] extends View[T] {
    /**
     * The [[VariableModel]] whose contents are displayed and edited
     */
    def model: VariableModel[T]
}