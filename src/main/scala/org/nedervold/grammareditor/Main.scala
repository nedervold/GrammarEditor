package org.nedervold.grammareditor

import java.awt.Color
import java.util.concurrent.TimeUnit
import scala.swing.Action
import scala.swing.BorderPanel
import scala.swing.BorderPanel.Position.Center
import scala.swing.BorderPanel.Position.South
import scala.swing.BoxPanel
import scala.swing.Dimension
import scala.swing.MainFrame
import scala.swing.Menu
import scala.swing.MenuBar
import scala.swing.MenuItem
import scala.swing.Orientation
import scala.swing.ScrollPane
import scala.swing.SimpleSwingApplication
import scala.swing.SplitPane
import scala.swing.Swing
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import org.nedervold.grammareditor.grammar.Grammar
import org.nedervold.grammareditor.grammar.GrammarParser
import org.nedervold.grammareditor.grammar.transformations.AlphabeticSort
import org.nedervold.grammareditor.grammar.transformations.DepthFirstSort
import org.nedervold.grammareditor.grammar.transformations.Format
import org.nedervold.grammareditor.models.DebouncingModel
import org.nedervold.grammareditor.models.FmapModel
import org.nedervold.grammareditor.models.Model
import org.nedervold.grammareditor.models.ModelChangedEvent
import org.nedervold.grammareditor.models.adapters.DocumentAdapter
import org.nedervold.grammareditor.models.viewcontrollers.DocumentViewController
import org.nedervold.grammareditor.models.views.TextAreaView
import org.nedervold.grammareditor.grammar.transformations.GrammarTransformation
import scala.swing.FileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import java.io.PrintWriter
import scala.io.Source

object Main extends SimpleSwingApplication {
    System.setProperty("apple.laf.useScreenMenuBar", "true")
 
    val rawGrammarSource = new DocumentAdapter
    val grammarSource = new DebouncingModel(rawGrammarSource, 500, TimeUnit.MILLISECONDS)
    val grammar: Model[Try[Grammar]] = new FmapModel(GrammarParser.parseGrammar(_: String), grammarSource)
    val grammarIsValid: Model[Boolean] = new FmapModel((_: Try[Grammar]).isSuccess, grammar)
    /**
     * Parses the grammar and if it's successful, displays the nonterminals
     *
     * @param source the source of the grammar
     * @return display of the nonterminals
     */
    def nonterminalsDisplay(gram: Try[Grammar]): String = {
        def displayNonterminals(g: Grammar): String = g.nonterminals.mkString(", ")
        gram.map(displayNonterminals).getOrElse("")
    }

    /**
     * Parses the grammar and if it's successful, displays the terminals
     *
     * @param source the source of the grammar
     * @return display of the terminals
     */
    def terminalsDisplay(gram: Try[Grammar]): String = {
        def displayTerminals(g: Grammar): String = g.terminals.mkString(", ")
        gram.map(displayTerminals).getOrElse("")
    }

    /**
     * Models a string describing the nonterminals.  This function is temporary scaffolding.
     *
     * @return a string describing the nonterminals
     */
    val nonterminalsModel: Model[String] = new FmapModel(nonterminalsDisplay, grammar)

    /**
     * Models a string describing the terminals.  This function is temporary scaffolding.
     *
     * @return a string describing the terminals
     */
    val terminalsModel: Model[String] = new FmapModel(terminalsDisplay, grammar)

    /**
     * Parses the grammar and if it's unsuccessful, displays the error message
     *
     * @param gram the  grammar attempt
     * @return the error message
     */
    def errorDisplay(gram: Try[Grammar]): String = {
        gram match {
            case Success(_) => ""
            case Failure(ex) => ex.getMessage
        }
    }

    /**
     * Models a string describing parsing errors.  This function is temporary scaffolding.
     *
     * @return a string describing parsing errors
     */
    val errorModel: Model[String] = new FmapModel(errorDisplay, grammar)

    /**
     * Opens a user-selected file and puts the contents into the edit panel.
     */
    def openCmd() = {
        val fileChooser: FileChooser = new FileChooser( /* dir */ ) {
            title = "Open..."
            fileFilter = new FileNameExtensionFilter("Grammar as text", "txt");
        }
        val res = fileChooser.showOpenDialog(null);
        res match {
            case FileChooser.Result.Approve => {
                val src = Source.fromFile(fileChooser.selectedFile, "UTF-8")
                rawGrammarSource.value = src.mkString
            }
            case _ => {}
        }
    }

    /**
     * Opens a user-selected file and puts the (pretty-printed) contents
     * of the edit panel into it.
     */
    def saveCmd() = {
        assert(grammarIsValid.value)
        val fileChooser: FileChooser = new FileChooser( /* dir */ ) {
            title = "Save As..."
            fileFilter = new FileNameExtensionFilter("Grammar as text", "txt");
        }
        val res = fileChooser.showSaveDialog(null);

        res match {
            case FileChooser.Result.Approve => {
                val out = new PrintWriter(fileChooser.selectedFile, "UTF-8")
                try {
                    out.print(grammar.value.get.toString)
                } finally {
                    out.close
                }
            }
            case _ => {}
        }
    }

    def top = new MainFrame {
        title = "Grammar Editor"

        menuBar = new MenuBar {
            contents += new Menu("File") {
                import org.nedervold.grammareditor.grammar.transformations.mkAccelerator
                val openAction = new Action("Open...") {
                    def apply = openCmd()
                    accelerator = mkAccelerator('O')
                    enabled = true;
                }
                val saveAction = new Action("Save As...") {
                    def apply = saveCmd()
                    accelerator = mkAccelerator('S')
                    enabled = true;
                }
                reactions += {
                    case ModelChangedEvent(`grammarIsValid`) => saveAction.enabled = grammarIsValid.value;
                }
                listenTo(grammarIsValid)
                saveAction.enabled = grammarIsValid.value
                contents += new MenuItem(openAction);
                contents += new MenuItem(saveAction);
            }

            contents += new Menu("Edit") {
                for (
                    xform <- List(Format, AlphabeticSort, DepthFirstSort)
                ) {
                    val action = new Action(xform.displayName) {
                        def apply = {
                            rawGrammarSource.value = xform(grammar.value.get).toString
                        }
                        accelerator = xform.accelerator
                        enabled = true;
                    }
                    reactions += {
                        case ModelChangedEvent(`grammarIsValid`) => action.enabled = grammarIsValid.value;
                    }
                    listenTo(grammarIsValid)
                    action.enabled = grammarIsValid.value
                    contents += new MenuItem(action)
                }
            }
        }

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
            layout(new TextAreaView(errorModel)) = South
        }

        size = new Dimension(800, 600)
    }
}
