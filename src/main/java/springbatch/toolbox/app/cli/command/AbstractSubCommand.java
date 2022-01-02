package springbatch.toolbox.app.cli.command;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class provides some basic functionality for commands.
 */
abstract class AbstractSubCommand {

	@Autowired
	protected JobRepository jobRepository;

	@Autowired
	protected JobExplorer jobExplorer;

	/**
	 * Find all executions for the job identified by jobIdentifier.
	 *
	 * @param jobIdentifier The name of the job
	 * @return Returns a list of all executions for the given job.
	 * @throws NoSuchJobException If the job does not exists.
	 */
	protected List<JobExecution> findAllExecutions(final String jobIdentifier) throws NoSuchJobException {

		final List<JobExecution> jobExecutions = new ArrayList<>();
		for (final JobInstance jobInstance : jobExplorer.findJobInstancesByJobName(jobIdentifier, 0,
				jobExplorer.getJobInstanceCount(jobIdentifier))) {
			jobExecutions.addAll(jobExplorer.getJobExecutions(jobInstance));
		}
		return jobExecutions;
	}

	/**
	 * Find all executions for the job identified by jobIdentifier which match the
	 * given status.
	 *
	 * @param jobIdentifier The name of the job
	 * @param batchStatus   The status to look for
	 * @return Returns a list of all executions for the given job with the given
	 *         status.
	 * @throws NoSuchJobException If the job does not exists.
	 */
	protected List<JobExecution> findJobExecutionsWithStatus(final String jobIdentifier, final BatchStatus batchStatus)
			throws NoSuchJobException {

		final List<JobExecution> result = new ArrayList<>();
		for (final JobExecution jobExecution : findAllExecutions(jobIdentifier)) {
			if (jobExecution.getStatus() != batchStatus) {
				result.add(jobExecution);
			}
		}
		return result.isEmpty() ? null : result;
	}

}