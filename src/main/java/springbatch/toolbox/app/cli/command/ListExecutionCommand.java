package springbatch.toolbox.app.cli.command;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

@Component
@Command(name = "list", description = "List all executions for the job.")
public class ListExecutionCommand implements Callable<Integer> {

	@Spec
	CommandSpec spec;

	@Parameters(index = "0", paramLabel = "jobName", description = "The name of the job, which executions should managed.")
	private String jobName;

	@Option(names = "--printParams", description = "Print the parameters of the execution.")
	private boolean printParams;

	@Option(names = "--printExit", description = "Print exit status of the execution.")
	private boolean printExit;

	@Autowired
	private JobExplorer jobExplorer;

	@Override
	public Integer call() throws NoSuchJobException {

		spec.commandLine().getOut().print("ID\tStart Time\tEnd Time\tStatus");
		if (printParams) {
			spec.commandLine().getOut().print("\tParameters");
		}
		if (printExit) {
			spec.commandLine().getOut().print("\tExit status");
		}
		spec.commandLine().getOut().println();
		final List<JobExecution> jobExecutions = new LinkedList<>();
		for (final JobInstance jobInstance : jobExplorer.findJobInstancesByJobName(jobName, 0,
				jobExplorer.getJobInstanceCount(jobName))) {
			jobExecutions.addAll(jobExplorer.getJobExecutions(jobInstance));
		}

		jobExecutions
		.sort((final JobExecution e1, final JobExecution e2) -> e1.getStartTime().compareTo(e2.getStartTime()));
		for (final JobExecution jobExecution : jobExecutions) {
			spec.commandLine().getOut().print(jobExecution.getId());
			spec.commandLine().getOut().print("\t" + jobExecution.getStartTime());
			spec.commandLine().getOut().print("\t" + jobExecution.getEndTime());
			spec.commandLine().getOut().print("\t" + jobExecution.getStatus());
			if (printParams) {
				spec.commandLine().getOut().print("\t" + jobExecution.getJobParameters());
			}
			if (printExit) {
				spec.commandLine().getOut().print("\t" + jobExecution.getExitStatus());
			}
			spec.commandLine().getOut().println();
		}

		return 0;
	}
}