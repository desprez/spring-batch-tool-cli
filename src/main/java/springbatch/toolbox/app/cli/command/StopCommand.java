package springbatch.toolbox.app.cli.command;

import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.stereotype.Component;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Component
@Command(name = "stop", description = "Stop all running executions for the job.")
public class StopCommand extends AbstractSubCommand implements Callable<Integer> {

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