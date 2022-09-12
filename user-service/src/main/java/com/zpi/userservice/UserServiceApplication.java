package com.zpi.userservice;

import com.zpi.userservice.user.AppUser;
import com.zpi.userservice.user.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootApplication
public class UserServiceApplication {

	private final Integer threadPoolSize;
	private final Integer taskQueueSize;

	@Autowired
	public UserServiceApplication(
			@Value("${app.threadPoolSize:10}") Integer threadPoolSize,
			@Value("${app.taskQueueSize:100}") Integer taskQueueSize
	) {
		this.threadPoolSize = threadPoolSize;
		this.taskQueueSize = taskQueueSize;
	}

	public static void main(String[] args) {
		var context = SpringApplication.run(UserServiceApplication.class, args);

		var repo = context.getBean(AppUserRepository.class);
		repo.save(new AppUser("BoBa", "test1", "test", "test", null, LocalDateTime.now(), null));
		repo.save(new AppUser("BoBa", "test2", "test", "test", null, LocalDateTime.now(), null));
		repo.save(new AppUser("BoBa", "test3", "test", "test", null, LocalDateTime.now(), null));


	}

	@Bean
	public Scheduler jdbcScheduler() {
		return Schedulers.newBoundedElastic(threadPoolSize,
											taskQueueSize, "jdbc-pool");
	}

}
