package springbatch.toolbox.app.cli.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import picocli.CommandLine;
import springbatch.toolbox.app.cli.SpringBatchCli;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = NONE, classes = SpringBatchCli.class)
@TestInstance(Lifecycle.PER_CLASS)
class StatusCommandTest {

	@Autowired
	CommandLine.IFactory factory;

	@Autowired
	StatusCommand statusCommand;

	@Autowired
	JobRepository jobRepository;

	@Autowired
	JobExplorer jobExplorer;

	@Autowired
	JobRepositoryTestUtils jobRepositoryTestUtils;

	@Test
	void status_executions_command_should_success()
			throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		// Given
		final CommandLine cmd = new CommandLine(statusCommand, factory);

		final JobExecution newJobExecution = jobRepository.createJobExecution("jobTest", new JobParameters());

		// When
		final int exitCode = cmd.execute(newJobExecution.getId().toString(), "FAILED");

		// Then
		assertThat(exitCode).isEqualTo(0);
		final JobExecution found = jobExplorer.getJobExecution(newJobExecution.getId());
		assertThat(found.getStatus()).isEqualTo(BatchStatus.FAILED);
	}

	@AfterAll
	public void tearDown() {
		jobRepositoryTestUtils.removeJobExecutions();
	}
}
