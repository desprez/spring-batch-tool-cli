package springbatch.toolbox.app.cli.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import picocli.CommandLine;
import springbatch.toolbox.app.cli.SpringBatchCli;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = NONE, classes = SpringBatchCli.class)
class RootCommandTest {

	@Autowired
	CommandLine.IFactory factory;

	@Autowired
	RootCommand rootCommand;

	@Test
	void testUsageHelp() {
		final String expected = String.format("\r\nThe general form of execution is:\r\n"
				+ "java -Dspring.datasource.url= -jar batch-toolbox.jar COMMAND SUBCOMMAND\r\n" + "\r\n"
				+ "To get further help for the desired COMMAND or SUBCOMMAND use the HELP command\r\n"
				+ "as follows:\r\n" + "\r\n" + "1. java -D... -jar ... help COMMAND\r\n"
				+ "2. java -D... -jar ... help COMMAND SUBCOMMAND\r\n" + "\r\n"
				+ "1. will list available subcommands for the given command.\r\n"
				+ "2. will show available parameters for the given subcommand.\r\n" + "\r\n" + "Example\r\n" + "\r\n"
				+ "List all jobs, using H2 database\r\n"
				+ "java -D'spring.datasource.url=jdbc:h2:~/mts;AUTO_SERVER=TRUE' -jar\r\n"
				+ "batch-toolbox.jar jobs list\r\n" + "\r\n" + "Commands:\r\n"
				+ "  help          Displays help information about the specified command\r\n"
				+ "  jobs          Manage executed jobs.\r\n" + "  executions    Manage single execution instances.\r\n"
				+ "  purgehistory  Delete data from Spring Batch Metadata tables that are N months\r\n"
				+ "                  old.\r\n");
		final String actual = new CommandLine(rootCommand, factory).getUsageMessage(CommandLine.Help.Ansi.OFF);
		assertThat(actual).isEqualTo(expected);
	}

}
