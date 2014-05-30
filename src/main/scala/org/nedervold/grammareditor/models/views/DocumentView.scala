package org.nedervold.grammareditor.models.views

import scala.swing.TextArea

import org.nedervold.grammareditor.models.adapters.DocumentAdapter

import javax.swing.JTextArea

/**
 * A TextArea that displays the value of a [[Model]][String]
 *
 * @author nedervold
 */
class DocumentView(override val model: DocumentAdapter) extends TextArea with View[String] {
    require(model != null)

    override lazy val peer: JTextArea = new JTextArea(model.document) with SuperMixin
    editable = false;
}