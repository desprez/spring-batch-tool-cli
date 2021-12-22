package springbatch.toolbox.app.cli.command;

import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobExecutionNotStoppedException;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;
import springbatch.toolbox.app.cli.command.JobsCommand.AbandonCommand;
import springbatch.toolbox.app.cli.command.JobsCommand.ListCommand;
import springbatch.toolbox.app.cli.command.JobsCommand.StopCommand;

/**
 * This class bundles subcommands which manage jobs. Each subcommand is
 * implemented in a static sub-class.
 */
@Component
@Command(name = "jobs", subcommands = { HelpCommand.class, ListCommand.class, StopCommand.class,
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

	@Component
	@Command(name = "list", description = "List the name of all executed jobs.")
	static class ListCommand implements Callable<Integer> {

		@Autowired
		private JobExplorer jobExplorer;

		@Override
		public Integer call() throws NoSuchJobException {
			for (final String jobName : jobExplorer.getJobNames()) {
				System.out.println(jobName);
			}
			return 0;
		}
	}

	@Component
	@Command(name = "stop", description = "Stop all running executions for the job.")
	static class StopCommand extends AbstractSubCommand implements Callable<Integer> {

		@Parameters(index = "0", paramLabel = "jobName", description = "The name of the job, which should stopped.")
		protected String jobIdentifier;

		@Override
		public Integer call() throws Exception {

			final List<JobExecution> jobExecutions = findJobExecutionsWithStatus(jobIdentifier, BatchStatus.STARTED);
			if (jobExecutions == null) {
				throw new JobExecutionNotRunningException("No running execution found for job=" + jobIdentifier);
			}
			for (final JobExecution jobExecution : jobExecutions) {
				jobExecution.setStatus(BatchStatus.STOPPING);
				jobRepository.update(jobExecution);
			}
			return 0;
		}
	}

	@Component
	@Command(name = "abandon", description = "Abandon all stopped executions for the job.")
	static class AbandonCommand extends AbstractSubCommand implements Callable<Integer> {

		@Parameters(index = "0", paramLabel = "jobName", description = "The name of the job, which should abandoned.")
		protected String jobIdentifier;

		@Override
		public Integer call() throws Exception {

			final List<JobExecution> jobExecutions = findJobExecutionsWithStatus(jobIdentifier, BatchStatus.COMPLETED);
			if (jobExecutions == null) {
				throw new JobExecutionNotStoppedException("No stopped execution found for job=" + jobIdentifier);
			}
			for (final JobExecution jobExecution : jobExecutions) {
				jobExecution.setStatus(BatchStatus.ABANDONED);
				jobRepository.update(jobExecution);
			}
			return 0;
		}
	}

}