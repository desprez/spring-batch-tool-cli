package springbatch.toolbox.app.cli.command;

import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;

import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

/**
 * This class bundles subcommands which manage jobs. Each subcommand is
 * implemented in a static sub-class.
 */
@Component
@Command(name = "jobs", subcommands = { HelpCommand.class, ListJobCommand.class, StopCommand.class,
		AbandonCommand.class }, description = "Manage executed jobs.", synopsisSubcommandLabel = "SUBCOMMAND")
public class JobsCommand implements Callable<Integer> {

	@Spec
	CommandSpec spec;

	@Override
	public Integer call() {
		// Subcommand required
		spec.commandLine().usage(System.out);
		return -1;
	}






}