package springbatch.toolbox.app.cli.command;

import java.util.concurrent.Callable;

import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@Component
@Command(name = "list", description = "List the name of all executed jobs.")
public class ListJobCommand implements Callable<Integer> {

	@Spec
	CommandSpec spec;

	@Autowired
	private JobExplorer jobExplorer;

	@Override
	public Integer call() throws NoSuchJobException {
		for (final String jobName : jobExplorer.getJobNames()) {
			spec.commandLine().getOut().println(jobName);
		}
		return 0;
	}
}