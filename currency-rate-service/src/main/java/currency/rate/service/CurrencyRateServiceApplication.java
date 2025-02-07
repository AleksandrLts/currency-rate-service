package currency.rate.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class CurrencyRateServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurrencyRateServiceApplication.class, args);
	}

}
