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
import java.io.File
import org.nedervold.grammareditor.models.VarModel
import org.nedervold.grammareditor.models.Fmap2Model
import javax.swing.undo.UndoManager
import org.nedervold.grammareditor.models.PolledModel
import org.nedervold.grammareditor.grammar.transformations.StandardBottomUpForm
import org.nedervold.grammareditor.grammar.transformations.StandardTopDownForm
import org.nedervold.grammareditor.grammar.transformations.StandardBottomUpForm
import java_cup.JavaCupAnalyzer

object Main extends SimpleSwingApplication {
    System.setProperty("apple.laf.useScreenMenuBar", "true")

    val rawGrammarSource = new DocumentAdapter
    val grammarSource = new DebouncingModel(rawGrammarSource, 500, TimeUnit.MILLISECONDS)
    val grammar: Model[Try[Grammar]] = new FmapModel(GrammarParser.parseGrammar(_: String), grammarSource)
    val grammarIsValid: Model[Boolean] = new FmapModel((_: Try[Grammar]).isSuccess, grammar)

    val documentPath: VarModel[Option[File]] = new VarModel(None)

    val dirty = new VarModel(false)
    reactions += {
        case ModelChangedEvent(`rawGrammarSource`) => dirty.value = true;
    }
    listenTo(rawGrammarSource)

    def mkTitle(optFile: Option[File], dirty: Boolean): String = {
        (optFile match {
            case Some(file) => "GrammarEditor — " + file.getName
            case None => "Grammar Editor"
        }) + (if (dirty) " •" else "")
    }
    val titleModel = new Fmap2Model(mkTitle, documentPath, dirty)

    /**
     * Parses the grammar and if it's successful, displays the productions' info
     *
     * @param source the source of the grammar
     * @return display of the productions
     */
    def productionsDisplay(gram: Try[Grammar]): String = {
        def displayProductions(g: Grammar): String = {
            val prods = g.productions
            val undefs = g.productions.count(!_.isDefined)
            prods.size + " productions (" + undefs + " undefined)"
        }
        gram.map(displayProductions).getOrElse("")
    }

    /**
     * Parses the grammar and if it's successful, displays the nonterminals
     *
     * @param source the source of the grammar
     * @return display of the nonterminals
     */
    def nonterminalsDisplay(gram: Try[Grammar]): String = {
        def displayNonterminals(g: Grammar): String = {
            val nondefs = g.undefinedNonterminals
            val nts = g.nonterminals
            nts.size + " nonterminals (" + nondefs.size + " undefined):\n" + nts.mkString(", ")
        }
        gram.map(displayNonterminals).getOrElse("")
    }

    /**
     * Parses the grammar and if it's successful, displays the terminals
     *
     * @param source the source of the grammar
     * @return display of the terminals
     */
    def terminalsDisplay(gram: Try[Grammar]): String = {
        def displayTerminals(g: Grammar): String = {
            val ts = g.terminals
            val lits = ts.filter(_.name.charAt(0) == '"')
            ts.size + " terminals (" + lits.size + " literals; " + (ts.size - lits.size) + " free-format):\n" + ts.mkString(", ")
        }
        gram.map(displayTerminals).getOrElse("")
    }

    def lalr1Display(gram: Try[Grammar]): String = {
        def topDown(g: Grammar): String = {
            JavaCupAnalyzer.analyze(StandardTopDownForm(g)).toString
        }
        gram.map(topDown).getOrElse("")
    }
    /**
     * Models a string describing the nonterminals.  This function is temporary scaffolding.
     *
     * @return a string describing the nonterminals
     */
    val productionsModel: Model[String] = new FmapModel(productionsDisplay, grammar)

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
     * Models a string describing the LALR(1) analysis of the grammar.  This function is
     * temporary scaffolding.
     *
     * @return a string describing the LALR(1) analysis
     */
    val lalr1Model: Model[String] = new FmapModel(lalr1Display, grammar)

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
                val file = fileChooser.selectedFile
                val src = Source.fromFile(file, "UTF-8")
                rawGrammarSource.value = src.mkString
                documentPath.value = Some(file)
                dirty.value = false
            }
            case _ => {}
        }
    }

    import org.nedervold.grammareditor.grammar.transformations.mkAccelerator
    val openAction = new Action("Open...") {
        def apply = openCmd()
        accelerator = mkAccelerator('O')
        enabled = true;
    }

    /**
     * Saves the (pretty-printed) contents of the edit panel into the file
     */
    def saveCmd() = {
        documentPath.value match {
            case Some(file) => writeToFile(file)
            case None => saveAsCmd()
        }
    }

    val saveAction = new Action("Save") {
        def apply = saveCmd()
        accelerator = mkAccelerator('S')
        enabled = false
    }

    /**
     * Opens a user-selected file and puts the (pretty-printed) contents
     * of the edit panel into it.
     */
    def saveAsCmd() = {
        assert(grammarIsValid.value)
        val fileChooser: FileChooser = new FileChooser( /* dir */ ) {
            title = "Save As..."
            fileFilter = new FileNameExtensionFilter("Grammar as text", "txt");
        }
        val res = fileChooser.showSaveDialog(null);

        res match {
            case FileChooser.Result.Approve => {
                val file = fileChooser.selectedFile
                writeToFile(file)
            }
            case _ => {}
        }
    }

    val saveAsAction = new Action("Save As...") {
        def apply = saveAsCmd()
        enabled = false;
    }

    reactions += {
        case ModelChangedEvent(`grammarIsValid`) => {
            saveAction.enabled = grammarIsValid.value;
            saveAsAction.enabled = grammarIsValid.value;
        }
    }
    listenTo(grammarIsValid)
    saveAction.enabled = grammarIsValid.value
    saveAsAction.enabled = grammarIsValid.value

    val undoManager = new UndoManager
    val undoAction = new Action("Undo") {
        def apply = if (undoManager.canUndo) undoManager.undo else println("can't undo")
        accelerator = mkAccelerator('Z')
        enabled = true
    }
    val redoAction = new Action("Redo") {
        def apply = if (undoManager.canRedo) undoManager.redo else println("can't redo")
        accelerator = mkAccelerator('Y')
        enabled = true
    }

    def top = new MainFrame {
        reactions += {
            case ModelChangedEvent(`titleModel`) => title = titleModel.value
        }
        listenTo(titleModel)
        title = titleModel.value

        menuBar = new MenuBar {
            contents += new Menu("File") {
                contents += new MenuItem(openAction);
                contents += new MenuItem(saveAction);
                contents += new MenuItem(saveAsAction);
            }

            contents += new Menu("Edit") {
                contents += new MenuItem(undoAction)
                contents += new MenuItem(redoAction)
                for (
                    xform <- List(Format, AlphabeticSort, DepthFirstSort, StandardTopDownForm, StandardBottomUpForm)
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
            val docView = new DocumentViewController(rawGrammarSource)
            docView.peer.getDocument.addUndoableEditListener(undoManager)
            viewportView = docView
            verticalScrollBarPolicy = ScrollPane.BarPolicy.Always
        }

        val infoPanel = new ScrollPane {
            contents = new BoxPanel(Orientation.Vertical) {
                val productionsView = new TextAreaView(productionsModel) {
                    lineWrap = true; wordWrap = true
                    border = Swing.TitledBorder(Swing.LineBorder(Color.BLACK), "Productions")
                }
                contents += productionsView

                val terminalsView = new TextAreaView(terminalsModel) {
                    lineWrap = true; wordWrap = true
                    border = Swing.TitledBorder(Swing.LineBorder(Color.BLACK), "Terminals")
                }
                contents += terminalsView

                val nonterminalsView = new TextAreaView(nonterminalsModel) {
                    lineWrap = true; wordWrap = true
                    border = Swing.TitledBorder(Swing.LineBorder(Color.BLACK), "Nonterminals")
                }
                contents += nonterminalsView

                val lalr1View = new TextAreaView(lalr1Model) {
                    lineWrap = true; wordWrap = true
                    border = Swing.TitledBorder(Swing.LineBorder(Color.BLACK), "LALR(1) Analysis")
                }
                contents += lalr1View
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

    private def writeToFile(file: java.io.File): Unit = {
        val out = new PrintWriter(file, "UTF-8")
        try {
            out.print(grammar.value.get.toString)
            documentPath.value = Some(file)
            dirty.value = false
        } finally {
            out.close
        }
    }
}
