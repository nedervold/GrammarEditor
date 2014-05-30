package org.nedervold.grammareditor.grammar

import javax.swing.KeyStroke
import java.awt.event.InputEvent

package object transformations {
    /**
     * Makes an accelerator for the given key.  Convenience function.
     *
     * NOTE: Implementation might not be right for non-Mac platforms
     *
     * @param ch the character the accelerator is based on
     * @return a [[KeyStroke]] for Meta-ch
     */
    def mkAccelerator(ch: Char): Option[KeyStroke] = {
        // TODO 2014-05-29 This might not be right for non-Mac platforms
        Some(KeyStroke.getKeyStroke(ch, InputEvent.META_DOWN_MASK))
    }
}