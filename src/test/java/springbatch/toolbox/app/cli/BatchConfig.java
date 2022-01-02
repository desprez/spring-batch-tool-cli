package springbatch.toolbox.app.cli;

import javax.sql.DataSource;

import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchConfig {

	@Bean
	JobRepositoryTestUtils jobLauncherTestUtils(final DataSource dataSource, final JobRepository jobRepository) {
		return new JobRepositoryTestUtils(jobRepository, dataSource);
	}

}
