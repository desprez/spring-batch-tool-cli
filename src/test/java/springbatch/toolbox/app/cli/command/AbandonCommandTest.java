package springbatch.toolbox.app.cli.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import picocli.CommandLine;
import springbatch.toolbox.app.cli.SpringBatchCli;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = NONE, classes = SpringBatchCli.class)
@TestInstance(Lifecycle.PER_CLASS)
@Sql({ "/abandon-h2.sql" })
class AbandonCommandTest {

	@Autowired
	CommandLine.IFactory factory;

	@Autowired
	AbandonCommand abandonCommand;

	@Autowired
	JobRepository jobRepository;

	@Autowired
	JobExplorer jobExplorer;

	@Autowired
	JobRepositoryTestUtils jobRepositoryTestUtils;

	@Test
	void abandon_job_command_should_success()
			throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		// Given
		final CommandLine cmd = new CommandLine(abandonCommand, factory);

		// When
		final int exitCode = cmd.execute("jobTest");

		// Then
		assertThat(exitCode).isEqualTo(0);
		final JobExecution found = jobExplorer.getJobExecution(-1L);
		assertThat(found.getStatus()).isEqualTo(BatchStatus.ABANDONED);

	}

	@AfterAll
	public void tearDown() {
		jobRepositoryTestUtils.removeJobExecutions();
	}
}
