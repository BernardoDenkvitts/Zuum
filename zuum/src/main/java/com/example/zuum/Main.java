package com.example.zuum;

import java.math.BigDecimal;

import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.example.zuum.Driver.DriverRepository;
import com.example.zuum.Ride.RideRepository;
import com.example.zuum.User.UserModel;
import com.example.zuum.User.UserRepository;

@SpringBootApplication
@EnableScheduling
public class Main implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}
	
	@Autowired
	UserRepository repo;
	
	@Autowired
	DriverRepository driverRepo;

	@Autowired
	RideRepository tripRepo;

	@Bean
	public String firstData() {
		// UserModel passanger = new UserModel(null, "Bernardo", "bernardoarcari@gmail.com", UserType.PASSANGER, "5554999962940", LocalDate.of(2004, 5, 5), new ArrayList<>());
		// UserModel user = new UserModel(null, "Jorge", "jorge@gmail.com", UserType.DRIVER, "55549904231", LocalDate.of(1995, 10, 2), new ArrayList<>());

		// GeometryFactory geometryFactory = new GeometryFactory();
        // Coordinate originCoordinate = new Coordinate(15.0000, -30.0000);
        // Point currLocation = geometryFactory.createPoint(originCoordinate);
		
		// DriverModel driver = new DriverModel(null, "ABC12321", "Civic", "12321SAD", currLocation, user);

		// repo.save(passanger);
		// driverRepo.save(driver);

		return "saved";
	}

	@Override
	public void run(String... args) throws Exception {
		List<UserModel> users = repo.findAll();

		for (UserModel userModel : users) {
			System.out.println(userModel.getId());
		}

		GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate originCoordinate = new Coordinate(15.0000, -30.0000);
        Point origin = geometryFactory.createPoint(originCoordinate);

		Coordinate destinyCoordinate = new Coordinate(16.5000, -31.5000);
        Point destiny = geometryFactory.createPoint(destinyCoordinate);

		BigDecimal valor = BigDecimal.valueOf(20.10);

		// RideModel firstTrip = new RideModel(users.get(0), valor, origin, destiny);
		// RideModel secondTrip = new RideModel(users.get(2), valor, origin, destiny);
		// tripRepo.saveAll(List.of(firstTrip, secondTrip));
	}

}

