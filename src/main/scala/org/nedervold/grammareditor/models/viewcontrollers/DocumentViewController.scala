package org.nedervold.grammareditor.models.viewcontrollers

import org.nedervold.grammareditor.models.adapters.DocumentAdapter
import org.nedervold.grammareditor.models.views.DocumentView

/**
 * A TextArea that displays and allows editing of the value of a [[Model]][String]
 *
 * @author nedervold
 */
class DocumentViewController(override val model: DocumentAdapter)
    extends DocumentView(model) {
    editable = true
}