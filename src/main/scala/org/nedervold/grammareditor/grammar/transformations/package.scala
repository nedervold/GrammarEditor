package org.nedervold.grammareditor.grammar

import javax.swing.KeyStroke
import java.awt.event.InputEvent

package object transformations {
    /**
     * Makes an accelerator for the given key.  Convenience function.
     *
     * @param c
     * @return
     */
    def mkAccelerator(c: Char): Option[KeyStroke] = {
        // TODO 2014-05-29 This might not be right for non-Mac platforms
        Some(KeyStroke.getKeyStroke(c, InputEvent.META_DOWN_MASK))
    }
}