package org.nedervold.grammareditor.models.views

import scala.swing.Swing
import scala.swing.TextArea
import org.nedervold.grammareditor.models.onEDT
import org.nedervold.grammareditor.models.Model
import org.nedervold.grammareditor.models.ModelChangedEvent

/**
 * A TextArea that displays the value of a [[Model]]
 *
 * @author nedervold
 */
class TextAreaView[T](val model: Model[T],
                      val unparse: T => String = ((t: T) => t.toString))
    extends TextArea with View[T] {
    require(model != null)
    require(unparse != null)

    listenTo(model);
    deafTo(this);
    editable = false;
    text = unparse(model.value)
    reactions += {
        case ModelChangedEvent(_) => onEDT { text = unparse(model.value) }
    }
}