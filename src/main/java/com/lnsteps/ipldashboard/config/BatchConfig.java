package com.lnsteps.ipldashboard.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import com.lnsteps.ipldashboard.batch.bean.MatchInputBean;
import com.lnsteps.ipldashboard.batch.listener.JobCompletionNotificationListener;
import com.lnsteps.ipldashboard.batch.processor.MatchDataProcessor;
import com.lnsteps.ipldashboard.dao.MatchRepository;
import com.lnsteps.ipldashboard.model.MatchEntity;

@Configuration
public class BatchConfig {
	private final String[] FIELD_NAMES = new String[] { "id", "city", "date", "player_of_match", "venue",
			"neutral_venue", "team1", "team2", "toss_winner", "toss_decision", "winner", "result", "result_margin",
			"eliminator", "method", "umpire1", "umpire2" };

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	MatchRepository matchRepository;

	@Bean
	public FlatFileItemReader<MatchInputBean> reader() {
		return new FlatFileItemReaderBuilder<MatchInputBean>().name("MatchItemReader")
				.resource(new ClassPathResource("match-data.csv")).linesToSkip(1).delimited().names(FIELD_NAMES)
				.fieldSetMapper(new BeanWrapperFieldSetMapper<MatchInputBean>() {
					{
						setTargetType(MatchInputBean.class);
					}
				}).build();
	}

	@Bean
	public MatchDataProcessor processor() {
		return new MatchDataProcessor();
	}

	@Bean
	public RepositoryItemWriter<MatchEntity> writer() {
		return new RepositoryItemWriterBuilder<MatchEntity>().repository(matchRepository).methodName("save").build();
	}

	@Bean
	public Job importUserJob(JobCompletionNotificationListener listener) {
		return jobBuilderFactory.get("importUserJob").incrementer(new RunIdIncrementer()).listener(listener)
				.flow(step1()).end().build();
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").<MatchInputBean, MatchEntity>chunk(10).reader(reader())
				.processor(processor()).writer(writer()).taskExecutor(taskExecutor()).build();
	}

	@Bean
	public TaskExecutor taskExecutor() {
		SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
		asyncTaskExecutor.setConcurrencyLimit(5); // spawn 5 threads to perform that step (divide the task to 5 people)
		return asyncTaskExecutor;
	}

}
