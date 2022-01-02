package springbatch.toolbox.app.cli.command;
import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;

/**
 * This class bundles subcommands which manage executions. Each subcommand is implemented in a static sub-class.
 */
@Component
@Command(name = "executions", subcommands = { HelpCommand.class, ListExecutionCommand.class,
		StatusCommand.class }, description = { "Manage single execution instances.",
				"This is an advanced feature, normally managing your job via 'jobs' command should be sufficient.",
"Enter 'executions help SUBCOMMAND' to find out parameters for the specified subcommand." }, synopsisSubcommandLabel = "SUBCOMMAND")
public class ExecutionsCommand implements Callable<Integer> {

	@Override
	public Integer call() {
		// Subcommand required
		new CommandLine(this).usage(System.out);
		return -1;
	}





}