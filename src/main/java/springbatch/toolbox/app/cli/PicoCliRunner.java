package springbatch.toolbox.app.cli;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.IFactory;
import springbatch.toolbox.app.cli.command.RootCommand;

/**
 * Spring Application Runner, which executes our CLI application via PicoCli.
 */
@Profile("!test")
@Component
public class PicoCliRunner implements ExitCodeGenerator, CommandLineRunner {

	@Autowired
	private RootCommand topCommand;

	@Autowired
	private IFactory factory;

	private int exitCode;

	@Override
	public void run(final String... args) throws Exception {
		exitCode = new CommandLine(topCommand, factory).execute(args);
	}

	@Override
	public int getExitCode() {
		return exitCode;
	}

}