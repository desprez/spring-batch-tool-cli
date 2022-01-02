package springbatch.toolbox.app.cli.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.batch.core.JobParameters;
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
class ListJobCommandTest {

	@Autowired
	CommandLine.IFactory factory;

	@Autowired
	ListJobCommand listJobCommand;

	@Autowired
	JobRepository jobRepository;

	@Autowired
	JobRepositoryTestUtils jobRepositoryTestUtils;

	@Test
	void list_job_command_should_success()
			throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		// Given
		jobRepository.createJobExecution("jobTest", new JobParameters());

		final CommandLine cmd = new CommandLine(listJobCommand, factory);

		final StringWriter sw = new StringWriter();
		cmd.setOut(new PrintWriter(sw));

		// When
		final int exitCode = cmd.execute();

		// Then
		assertThat(exitCode).isEqualTo(0);
		assertThat(sw.toString()).isEqualToIgnoringNewLines("jobTest");
	}

	@AfterAll
	public void tearDown() {
		jobRepositoryTestUtils.removeJobExecutions();
	}

}
