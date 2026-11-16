package com.gym.crm;

import com.gym.crm.model.Trainee;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Date;

@Configuration
@ComponentScan(basePackages = "com.gym.crm")
@PropertySource("classpath:application.properties")
public class Main {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Main.class);

        System.out.println("\n--- SPRING CONTAINER SUCCESSFULLY STARTED ---\n");

        TraineeService traineeService = context.getBean(TraineeService.class);
        TrainerService trainerService = context.getBean(TrainerService.class);

        Trainee trainee1 = traineeService.createTrainee("John", "Smith", new Date(), "123 Main St");
        System.out.println("Created trainee:");
        System.out.println("Name: " + trainee1.getFirstName() + " " + trainee1.getLastName());
        System.out.println("Username: " + trainee1.getUsername());
        System.out.println("Password: " + trainee1.getPassword());

        System.out.println("\n----------------------------------\n");

        Trainee trainee2 = traineeService.createTrainee("John", "Smith", new Date(), "456 Oak St");
        System.out.println("Created second trainee with the same name:");
        System.out.println("Name: " + trainee2.getFirstName() + " " + trainee2.getLastName());
        System.out.println("Username: " + trainee2.getUsername());
        System.out.println("Password: " + trainee2.getPassword());

        context.close();
    }
}