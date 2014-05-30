package org.nedervold.grammareditor

import java.util.concurrent.TimeUnit
import scala.swing.BorderPanel
import scala.swing.BorderPanel.Position.Center
import scala.swing.BoxPanel
import scala.swing.Dimension
import scala.swing.MainFrame
import scala.swing.Orientation
import scala.swing.ScrollPane
import scala.swing.SimpleSwingApplication
import scala.swing.SplitPane
import org.nedervold.grammareditor.grammar.GrammarParser
import org.nedervold.grammareditor.models.DebouncingModel
import org.nedervold.grammareditor.models.FmapModel
import org.nedervold.grammareditor.models.Model
import org.nedervold.grammareditor.models.adapters.DocumentAdapter
import org.nedervold.grammareditor.models.viewcontrollers.DocumentViewController
import org.nedervold.grammareditor.models.views.TextAreaView
import scala.swing.Swing
import java.awt.Color

object Main extends SimpleSwingApplication {
    System.setProperty("apple.laf.useScreenMenuBar", "true")

    val rawGrammarSource = new DocumentAdapter
    val grammarSource = new DebouncingModel(rawGrammarSource, 500, TimeUnit.MILLISECONDS)

    /**
     * Parses the grammar and if it's successful, displays the nonterminals
     *
     * @param source the source of the grammar
     * @return display of the nonterminals
     */
    def nonterminalsDisplay(source: String): String = {
        GrammarParser.parseGrammar(source) match {
            case Left(msg) => ""
            case Right(grammar) => grammar.nonterminals.mkString(", ")
        }
    }

    /**
     * Parses the grammar and if it's successful, displays the terminals
     *
     * @param source the source of the grammar
     * @return display of the terminals
     */
    def terminalsDisplay(source: String): String = {
        GrammarParser.parseGrammar(source) match {
            case Left(msg) => ""
            case Right(grammar) => grammar.terminals.mkString(", ")
        }
    }

    /**
     * Models a string describing the nonterminals.  This function is temporary scaffolding.
     *
     * @return a string describing the nonterminals
     */
    val nonterminalsModel: Model[String] = new FmapModel(nonterminalsDisplay, grammarSource)

    /**
     * Models a string describing the terminals.  This function is temporary scaffolding.
     *
     * @return a string describing the terminals
     */
    val terminalsModel: Model[String] = new FmapModel(terminalsDisplay, grammarSource)

    def top = new MainFrame {
        title = "Grammar Editor"

        val editPanel = new ScrollPane {
            viewportView = new DocumentViewController(rawGrammarSource)
            verticalScrollBarPolicy = ScrollPane.BarPolicy.Always
        }

        val infoPanel = new ScrollPane {
            contents = new BoxPanel(Orientation.Vertical) {
                val terminalsView = new TextAreaView(terminalsModel) {
                    lineWrap = true
                    border = Swing.TitledBorder(Swing.LineBorder(Color.BLACK), "Terminals")
                }
                contents += terminalsView

                val nonterminalsView = new TextAreaView(nonterminalsModel) {
                    lineWrap = true
                    border = Swing.TitledBorder(Swing.LineBorder(Color.BLACK), "Nonterminals")
                }
                contents += nonterminalsView
            }
        }

        contents = new BorderPanel {
            layout(new SplitPane(Orientation.Vertical, editPanel, infoPanel) {
                dividerLocation = 600
            }) = Center
        }

        size = new Dimension(800, 600)
    }
}
