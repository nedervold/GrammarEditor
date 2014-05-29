package org.nedervold.grammareditor

import scala.swing._
import scala.swing.BorderPanel.Position._

object Main extends SimpleSwingApplication {
    System.setProperty("apple.laf.useScreenMenuBar", "true")

    def top = new MainFrame {
        title = "Grammar Editor"

	val editPanel = new ScrollPane {
	    viewportView = new BorderPanel
	    verticalScrollBarPolicy = ScrollPane.BarPolicy.Always
        }

        val infoPanel = new ScrollPane {
	    contents = new BoxPanel(Orientation.Vertical)
	}

	contents = new BorderPanel {
            layout(new SplitPane(Orientation.Vertical, editPanel, infoPanel) {
                dividerLocation = 600
            }) = Center
        }

        size = new Dimension(800, 600)
    }
}
