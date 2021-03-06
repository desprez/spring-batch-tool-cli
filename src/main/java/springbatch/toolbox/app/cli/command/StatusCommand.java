package springbatch.toolbox.app.cli.command;

import java.util.concurrent.Callable;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.stereotype.Component;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

@Component
@Command(name = "status", description = "Set the status of the given execution.")
public class StatusCommand extends AbstractSubCommand implements Callable<Integer> {

	@Spec
	CommandSpec spec;

	@Parameters(index = "0", paramLabel = "executionId", description = "ID of the execution to modify.")
	private Long executionId;

	@Parameters(index = "1", paramLabel = "status", description = "The new status you wish the execution to set to.")
	private BatchStatus status;

	@Override
	public Integer call() throws Exception {

		final JobExecution jobExecution = jobExplorer.getJobExecution(executionId);
		if (jobExecution == null) {
			spec.commandLine().getOut().println(
					"No execution with ID " + executionId + " found. Use list command to find existing executions.");
			return -1;
		}
		jobExecution.setStatus(status);
		jobRepository.update(jobExecution);
		spec.commandLine().getOut().println("Done.");
		return 0;
	}

}