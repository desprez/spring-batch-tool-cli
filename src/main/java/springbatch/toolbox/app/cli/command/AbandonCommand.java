package springbatch.toolbox.app.cli.command;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.Callable;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.stereotype.Component;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Component
@Command(name = "abandon", description = "Abandon all stopped executions for the job.")
public class AbandonCommand extends AbstractSubCommand implements Callable<Integer> {

	@Parameters(index = "0", paramLabel = "jobName", description = "The name of the job, which should abandoned.")
	protected String jobIdentifier;

	@Option(names = "--hours", description = "duration beyond which the running job will be abandoned (in hours)", defaultValue = "12")
	private Integer hoursThreshold;

	@Override
	public Integer call() throws Exception {

		final Set<JobExecution> jobExecutions = jobExplorer.findRunningJobExecutions(jobIdentifier);
		if (jobExecutions == null) {
			throw new JobExecutionNotRunningException("No running execution found for job=" + jobIdentifier);
		}
		for (final JobExecution jobExecution : jobExecutions) {
			final long duration = Duration.between(jobExecution.getStartTime().toInstant(), Instant.now()).toHours();

			if (duration > hoursThreshold) {
				jobExecution.setStatus(BatchStatus.ABANDONED);
				jobRepository.update(jobExecution);
			}
		}
		return 0;
	}
}