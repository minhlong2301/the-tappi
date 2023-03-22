package vn.project.nfc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NfcApplication {

	public static void main(String[] args) {
		SpringApplication.run(NfcApplication.class, args);

	}

//	@Bean
//	public WebMvcConfigurer corsConfigurer() {
//		{
//			return new WebMvcConfigurer() {
//				@Override
//				public void addCorsMappings(CorsRegistry registry) {
//					registry.addMapping("/**").allowedOrigins("http://localhost:3000");
//				}
//			};
//		}
//	}
}