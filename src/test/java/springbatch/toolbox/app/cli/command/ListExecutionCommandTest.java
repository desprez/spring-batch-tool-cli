package springbatch.toolbox.app.cli.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
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
class ListExecutionCommandTest {

	@Autowired
	CommandLine.IFactory factory;

	@Autowired
	ListExecutionCommand listExecutionCommand;

	@Autowired
	JobRepository jobRepository;

	@Autowired
	JobExplorer jobExplorer;

	@Autowired
	JobRepositoryTestUtils jobRepositoryTestUtils;

	@Sql({ "/list-execution-h2.sql" })
	@Test
	void list_executions_command_should_success()
			throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		// Given
		final CommandLine cmd = new CommandLine(listExecutionCommand, factory);

		//jobRepository.createJobExecution("jobTest", new JobParameters());

		final StringWriter sw = new StringWriter();
		cmd.setOut(new PrintWriter(sw));

		// When
		final int exitCode = cmd.execute("jobTest", "--printParams", "--printExit");

		// Then
		assertThat(exitCode).isEqualTo(0);
		assertThat(sw.toString())
		.isEqualToIgnoringNewLines(
				"ID	Start Time	End Time	Status	Parameters	Exit status\r\n" +
				"-1	2021-05-01 00:00:00.0	2021-05-01 00:00:00.0	COMPLETED	{param=value}	exitCode=COMPLETED;exitDescription=\r\n");
	}

	@AfterAll
	public void tearDown() {
		jobRepositoryTestUtils.removeJobExecutions();
	}

}
