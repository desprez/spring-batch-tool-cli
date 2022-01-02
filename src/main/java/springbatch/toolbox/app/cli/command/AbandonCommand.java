package springbatch.toolbox.app.cli.command;

import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.JobExecutionNotStoppedException;
import org.springframework.stereotype.Component;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Component
@Command(name = "abandon", description = "Abandon all stopped executions for the job.")
public class AbandonCommand extends AbstractSubCommand implements Callable<Integer> {

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