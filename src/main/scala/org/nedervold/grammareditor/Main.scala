package org.nedervold.grammareditor

    import scala.swing._

object Main extends SimpleSwingApplication {
    def top = new MainFrame {
        title = "Main"
        size = new Dimension(800, 600)
    }
}
