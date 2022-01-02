package springbatch.toolbox.app.cli.command;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;

import org.springframework.batch.core.repository.dao.AbstractJdbcBatchMetadataDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

@Component
@Command(name = "purgehistory", description = "Delete data from Spring Batch Metadata tables that are N months old.")
public class PurgeHistoryCommand implements Callable<Integer> {



	/**
	 * SQL statements removing step and job executions compared to a given date.
	 */
	private static final String SQL_DELETE_BATCH_STEP_EXECUTION_CONTEXT = "DELETE FROM %PREFIX%STEP_EXECUTION_CONTEXT WHERE STEP_EXECUTION_ID IN (SELECT STEP_EXECUTION_ID FROM %PREFIX%STEP_EXECUTION WHERE JOB_EXECUTION_ID IN (SELECT JOB_EXECUTION_ID FROM  %PREFIX%JOB_EXECUTION where CREATE_TIME < ?))";
	private static final String SQL_DELETE_BATCH_STEP_EXECUTION = "DELETE FROM %PREFIX%STEP_EXECUTION WHERE JOB_EXECUTION_ID IN (SELECT JOB_EXECUTION_ID FROM %PREFIX%JOB_EXECUTION where CREATE_TIME < ?)";
	private static final String SQL_DELETE_BATCH_JOB_EXECUTION_CONTEXT = "DELETE FROM %PREFIX%JOB_EXECUTION_CONTEXT WHERE JOB_EXECUTION_ID IN (SELECT JOB_EXECUTION_ID FROM  %PREFIX%JOB_EXECUTION where CREATE_TIME < ?)";
	private static final String SQL_DELETE_BATCH_JOB_EXECUTION_PARAMS = "DELETE FROM %PREFIX%JOB_EXECUTION_PARAMS WHERE JOB_EXECUTION_ID IN (SELECT JOB_EXECUTION_ID FROM %PREFIX%JOB_EXECUTION where CREATE_TIME < ?)";
	private static final String SQL_DELETE_BATCH_JOB_EXECUTION = "DELETE FROM %PREFIX%JOB_EXECUTION where CREATE_TIME < ?";
	private static final String SQL_DELETE_BATCH_JOB_INSTANCE = "DELETE FROM %PREFIX%JOB_INSTANCE WHERE JOB_INSTANCE_ID NOT IN (SELECT JOB_INSTANCE_ID FROM %PREFIX%JOB_EXECUTION)";

	/**
	 * Default value for the table prefix property.
	 */
	private static final String DEFAULT_TABLE_PREFIX = AbstractJdbcBatchMetadataDao.DEFAULT_TABLE_PREFIX;

	/**
	 * Default value for the data retention (in month)
	 */
	private static final Integer DEFAULT_RETENTION_MONTH = 6;

	private final String tablePrefix = DEFAULT_TABLE_PREFIX;

	@Spec
	CommandSpec spec;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Option(names = "--months", description = "the metadatas retention (in months)", defaultValue = "6")
	// @Parameters(index = "0", paramLabel = "historyRetentionMonth", description =
	// "the metadatas retention (in months")
	private Integer historyRetentionMonth;

	@Override
	public Integer call() throws Exception {

		final LocalDateTime dateTime = LocalDateTime.now().minusMonths(historyRetentionMonth);
		spec.commandLine().getOut().println("Remove the Spring Batch history before the " + dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

		int rowCount = jdbcTemplate.update(getQuery(SQL_DELETE_BATCH_STEP_EXECUTION_CONTEXT), dateTime);
		spec.commandLine().getOut().format("Deleted rows number from the BATCH_STEP_EXECUTION_CONTEXT table: %d %n", rowCount);

		rowCount = jdbcTemplate.update(getQuery(SQL_DELETE_BATCH_STEP_EXECUTION), dateTime);
		spec.commandLine().getOut().format("Deleted rows number from the BATCH_STEP_EXECUTION table: %d %n", rowCount);

		rowCount = jdbcTemplate.update(getQuery(SQL_DELETE_BATCH_JOB_EXECUTION_CONTEXT), dateTime);
		spec.commandLine().getOut().format("Deleted rows number from the BATCH_JOB_EXECUTION_CONTEXT table: %d %n", rowCount);

		rowCount = jdbcTemplate.update(getQuery(SQL_DELETE_BATCH_JOB_EXECUTION_PARAMS), dateTime);
		spec.commandLine().getOut().format("Deleted rows number from the BATCH_JOB_EXECUTION_PARAMS table: %d %n", rowCount);

		rowCount = jdbcTemplate.update(getQuery(SQL_DELETE_BATCH_JOB_EXECUTION), dateTime);
		spec.commandLine().getOut().format("Deleted rows number from the BATCH_JOB_EXECUTION table: %d %n", rowCount);

		rowCount = jdbcTemplate.update(getQuery(SQL_DELETE_BATCH_JOB_INSTANCE));
		spec.commandLine().getOut().format("Deleted rows number from the BATCH_JOB_INSTANCE table: %d %n", rowCount);

		return 0;
	}

	private String getQuery(final String base) {
		return StringUtils.replace(base, "%PREFIX%", tablePrefix);
	}

}
