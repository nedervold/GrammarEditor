package java_cup;

import static java_cup.Main.build_end;
import static java_cup.Main.build_parser;
import static java_cup.Main.check_end;
import static java_cup.Main.check_unused;
import static java_cup.Main.dump_end;
import static java_cup.Main.dump_grammar;
import static java_cup.Main.dump_machine;
import static java_cup.Main.dump_tables;
import static java_cup.Main.emit_end;
import static java_cup.Main.expect_conflicts;
import static java_cup.Main.final_time;
import static java_cup.Main.input_file;
import static java_cup.Main.locations;
import static java_cup.Main.lr_values;
import static java_cup.Main.no_summary;
import static java_cup.Main.opt_do_debug;
import static java_cup.Main.opt_dump_grammar;
import static java_cup.Main.opt_dump_states;
import static java_cup.Main.opt_dump_tables;
import static java_cup.Main.opt_show_timing;
import static java_cup.Main.parse_args;
import static java_cup.Main.parse_end;
import static java_cup.Main.plural;
import static java_cup.Main.prelim_end;
import static java_cup.Main.print_progress;
import static java_cup.Main.show_times;
import static java_cup.Main.start_time;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringBufferInputStream;
import java.lang.reflect.Field;

import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;

/**
 * A driver of the java-cup engine, used by the GrammarEditor program.
 * 
 * @author nedervold
 * 
 */
@SuppressWarnings("deprecation")
public class NewMain {

	private static String summary;

	/**
	 * Overridden from Main because of the hardcoding of the output stream.
	 * 
	 * @param output_produced
	 */
	protected static void emit_summary(final boolean output_produced) {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final PrintStream ps = new PrintStream(out);

		inner_emit_summary(ps, output_produced);
		ps.close();
		summary = out.toString();
	}

	static private void exit(final int code) throws Exception {
		// System.exit(code);
		throw new Exception("System.exit(" + code + ")");
	}

	static private boolean hasErrors() {
		return ErrorManager.getManager().getErrorCount() != 0;
	}

	protected static void inner_emit_summary(final PrintStream out,
			final boolean output_produced) {
		final_time = System.currentTimeMillis();

		if (no_summary) {
			return;
		}

		out.println("------- " + version.title_str
				+ " Parser Generation Summary -------");

		/* error and warning count */
		out.println("  " + ErrorManager.getManager().getErrorCount() + " error"
				+ plural(ErrorManager.getManager().getErrorCount()) + " and "
				+ ErrorManager.getManager().getWarningCount() + " warning"
				+ plural(ErrorManager.getManager().getWarningCount()));

		/* basic stats */
		out.print("  " + terminal.number() + " terminal"
				+ plural(terminal.number()) + ", ");
		out.print(non_terminal.number() + " non-terminal"
				+ plural(non_terminal.number()) + ", and ");
		out.println(production.number() + " production"
				+ plural(production.number()) + " declared, ");
		out.println("  producing " + lalr_state.number()
				+ " unique parse states.");

		/* unused symbols */
		out.println("  " + emit.unused_term + " terminal"
				+ plural(emit.unused_term) + " declared but not used.");
		out.println("  " + emit.unused_non_term + " non-terminal"
				+ plural(emit.unused_term) + " declared but not used.");

		/* productions that didn't reduce */
		out.println("  " + emit.not_reduced + " production"
				+ plural(emit.not_reduced) + " never reduced.");

		/* conflicts */
		out.println("  " + emit.num_conflicts + " conflict"
				+ plural(emit.num_conflicts) + " detected" + " ("
				+ expect_conflicts + " expected).");

		/* code location */
		if (output_produced) {
			out.println("  Code written to \"" + emit.parser_class_name
					+ ".java\", and \"" + emit.symbol_const_class_name
					+ ".java\".");
		} else {
			out.println("  No code produced.");
		}

		if (opt_show_timing) {
			show_times();
		}

		out.println("---------------------------------------------------- ("
				+ version.title_str + ")");
	}

	static private String makeOutput() {
		return summary;
	}

	/**
	 * Overridden from Main because Lexer assumes input comes from System.in.
	 * 
	 * @throws java.lang.Exception
	 */
	protected static void parse_grammar_spec(final InputStream input)
			throws Exception {
		printErrLn("in parse_grammar_spec");

		/* create a parser and parse with it */
		final ComplexSymbolFactory csf = new ComplexSymbolFactory();
		final Lexer lexer = new Lexer(input) {
			@Override
			public Symbol next_token() throws IOException {
				printErrLn("in next_token()");
				return super.next_token();
			}
		};
		setSymbolFactoryFieldOfLexer(lexer, csf);
		final parser parser_obj = new parser(lexer, csf) {
			@Override
			protected void init_actions() {
				printErrLn("in init_actions()");
				super.init_actions();
			}

			@Override
			public Symbol scan() throws Exception {
				/*
				 * TODO What's up here? When I comment this out, the program
				 * hangs on scan(). But I just cut-and-pasted the code from
				 * parser so that I could add debugging messages.
				 */
				printErrLn("in scan()");
				try {
					final Symbol sym = getScanner().next_token();
					printErrLn("got symbol " + sym);
					return sym != null ? sym : getSymbolFactory().newSymbol(
							"END_OF_FILE", EOF_sym());

				} finally {
					printErrLn("leaving scan()");
				}
			}

			@Override
			public void user_init() throws Exception {
				printErrLn("in user_init()");
				super.user_init();
				printErrLn("leaving user_init()");
			}
		};

		printErrLn("about to parse");
		try {
			if (opt_do_debug) {
				parser_obj.debug_parse();
			} else {
				parser_obj.parse();
			}
		} catch (final Exception e) {
			printErrLn("parse exception: " + e.getMessage());
			/*
			 * something threw an exception. catch it and emit a message so we
			 * have a line number to work with, then re-throw it
			 */
			ErrorManager.getManager().emit_error(
					"Internal error: Unexpected exception");
			throw e;
		}
	}

	static private void printErrLn(final String line) {
		// System.err.println(line);
	}

	static public synchronized int reduceReduceCount() {
		return 0;
	}

	static public synchronized String run(final String input) throws Exception {
		printErrLn("in NewMain.run");
		runMain(new StringBufferInputStream(input));
		return makeOutput();
	}

	/**
	 * Runs a modified version of the java_cup.Main.main function, with a lock
	 * on all the data-structures, since Main is chock-full of globals.
	 * 
	 * @throws Exception
	 */
	static private void runMain(final InputStream input) throws Exception {
		final String[] argv = new String[] {};

		boolean did_output = false;

		start_time = System.currentTimeMillis();

		/**
		 * clean all static members, that contain remaining stuff from earlier
		 * calls
		 */
		terminal.clear();
		production.clear();
		production.clear();
		emit.clear();
		non_terminal.clear();
		parse_reduce_row.clear();
		parse_action_row.clear();
		lalr_state.clear();

		/* process user options and arguments */
		parse_args(argv);

		/*
		 * frankf 6/18/96 hackish, yes, but works
		 */
		emit.set_lr_values(lr_values);
		emit.set_locations(locations);
		/* open output files */
		if (print_progress) {
			printErrLn("Opening files...");
		}
		/* use a buffered version of standard input */
		input_file = new BufferedInputStream(input);

		prelim_end = System.currentTimeMillis();

		/* parse spec into internal data structures */
		if (print_progress) {
			printErrLn("Parsing specification from standard input...");
		}
		parse_grammar_spec(input);

		parse_end = System.currentTimeMillis();

		/* don't proceed unless we are error free */
		if (!hasErrors()) {
			/* check for unused bits */
			if (print_progress) {
				printErrLn("Checking specification...");
			}
			check_unused();

			check_end = System.currentTimeMillis();

			/* build the state machine and parse tables */
			if (print_progress) {
				printErrLn("Building parse tables...");
			}
			build_parser();

			build_end = System.currentTimeMillis();

			/* output the generated code, if # of conflicts permits */
			if (hasErrors()) {
				// conflicts! don't emit code, don't dump tables.
				opt_dump_tables = false;
			} else { // everything's okay, emit parser.
				if (print_progress) {
					printErrLn("Writing parser...");
				}
				// open_files();
				// emit_parser();
				did_output = true;
			}
		}
		/* fix up the times to make the summary easier */
		emit_end = System.currentTimeMillis();

		/* do requested dumps */
		if (opt_dump_grammar) {
			dump_grammar();
		}
		if (opt_dump_states) {
			dump_machine();
		}
		if (opt_dump_tables) {
			dump_tables();
		}

		dump_end = System.currentTimeMillis();

		/* close input/output files */
		if (print_progress) {
			printErrLn("Closing files...");
		}
		// close_files();

		/* produce a summary if desired */
		if (!no_summary) {
			emit_summary(did_output);
		}

		/*
		 * If there were errors during the run, exit with non-zero status
		 * (makefile-friendliness). --CSA
		 */
		if (hasErrors()) {
			exit(100);
		}
	}

	/**
	 * Breaks access controls and set the private symbolFactory field of Lexer.
	 * 
	 * @param lexer
	 * @param csf
	 */
	private static void setSymbolFactoryFieldOfLexer(final Lexer lexer,
			final ComplexSymbolFactory sf) throws Exception {
		final Field symbolFactoryField = Lexer.class
				.getDeclaredField("symbolFactory");
		symbolFactoryField.setAccessible(true);
		symbolFactoryField.set(lexer, sf);
	}

	static public synchronized int shiftReduceCount() {
		return 0;
	}

}
