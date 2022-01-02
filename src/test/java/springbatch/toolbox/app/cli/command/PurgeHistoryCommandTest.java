package springbatch.toolbox.app.cli.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
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
@Sql({ "/purge-history-h2.sql" })
class PurgeHistoryCommandTest {

	@Autowired
	private JobExplorer jobExplorer;

	@Autowired
	CommandLine.IFactory factory;

	@Autowired
	PurgeHistoryCommand purgeHistoryCommand;

	@Autowired
	JobRepositoryTestUtils jobRepositoryTestUtils;

	@Test
	void purge_history_command_should_success() {
		// Given
		final CommandLine cmd = new CommandLine(purgeHistoryCommand, factory);

		List<JobInstance> jobInstances = jobExplorer.getJobInstances("jobTest", 0, 5);
		assertThat(jobInstances.size()).isEqualTo(2);

		// When
		final int exitCode = cmd.execute();

		// Then
		assertThat(exitCode).isEqualTo(0);
		jobInstances = jobExplorer.getJobInstances("jobTest", 0, 5);
		assertThat(jobInstances.size()).isEqualTo(1);
		final JobInstance jobInstance = jobInstances.get(0);
		assertThat(jobInstance.getId()).isEqualTo(Long.valueOf(-102));
	}

	@AfterAll
	public void tearDown() {
		jobRepositoryTestUtils.removeJobExecutions();
	}

}
