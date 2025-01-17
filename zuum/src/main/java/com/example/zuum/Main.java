package com.example.zuum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import com.example.zuum.Trip.TripModel;
import com.example.zuum.Trip.TripRepository;
import com.example.zuum.Trip.Dto.TripRequestNotificationDTO;
import com.example.zuum.User.UserModel;
import com.example.zuum.User.UserRepository;

@SpringBootApplication
public class Main implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}
	
	@Autowired
	UserRepository repo;
	@Autowired
	TripRepository tripRepo;

	@Bean
	public String firstData() {
		// UserModel passanger = new UserModel(null, "Bernardo", "bernardaoarcari@gmail.com", UserType.PASSANGER, "5554999962940", LocalDate.of(2004, 5, 5));
		// UserModel driver = new UserModel(null, "Jorge", "jorgesampaio@gmail.com", UserType.DRIVER, "5554999962940", LocalDate.of(2004, 5, 5));

		// repo.saveAll(List.of(passanger, driver));
		return "salvo";
	}

	@Override
	public void run(String... args) throws Exception {
		List<UserModel> users = repo.findAll();

		for (UserModel userModel : users) {
			System.out.println(userModel.getId());
		}

		// GeometryFactory geometryFactory = new GeometryFactory();
        // Coordinate originCoordinate = new Coordinate(15.0000, -30.0000);
        // Point origin = geometryFactory.createPoint(originCoordinate);

		// Coordinate destinyCoordinate = new Coordinate(16.5000, -31.5000);
        // Point destiny = geometryFactory.createPoint(destinyCoordinate);

		// BigDecimal valor = BigDecimal.valueOf(20.10);

		// TripModel novaTrip = new TripModel(9, valor, origin, destiny);
		// tripRepo.save(novaTrip);
	}

}

