package org.nedervold.grammareditor.models.adapters

import org.nedervold.grammareditor.models.VariableModel
import org.nedervold.grammareditor.models.onEDTWait

import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.DefaultStyledDocument
import javax.swing.text.Document

/**
 * Wraps a Document to make a [[VariableModel]][String]
 *
 * @constructor
 * @param document the Document to wrap
 */
class DocumentAdapter(val document: Document) extends VariableModel[String] {
    require(document != null)

    def this() = this(new DefaultStyledDocument())

    override def value = document.getText(0, document.getLength())
    override def value_=(newValue: String) = {
        require(newValue != null)
        val oldValue = value

        /*
         * TODO 2014-05-29 Is this test too expensive?  It extracts the entire string
         * from the Document.
         */
        if (!oldValue.equals(newValue)) {
            onEDTWait {
                listening = false
                document.remove(0, document.getLength())
                document.insertString(0, newValue, null)
                listening = true
            }
            publish(event)
        }
    } ensuring {
        newValue.equals(value)
    }

    private var listening = true

    private object documentListener extends DocumentListener {
        def changedUpdate(e: DocumentEvent) = if (listening) publish(event)
        def insertUpdate(e: DocumentEvent) = if (listening) publish(event)
        def removeUpdate(e: DocumentEvent) = if (listening) publish(event)
    }

    document.addDocumentListener(documentListener);
}