package org.nedervold.grammareditor

import javax.swing.SwingUtilities
import scala.swing.Swing

package object models {
    def onEDT(op: ⇒ Unit): Unit = if (SwingUtilities.isEventDispatchThread) op else Swing.onEDT(op)
    def onEDTWait(op: ⇒ Unit): Unit = if (SwingUtilities.isEventDispatchThread) op else Swing.onEDTWait(op)
}