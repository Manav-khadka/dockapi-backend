package com.manav.dockapimainserver;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.EcsClientBuilder;
import software.amazon.awssdk.services.ecs.model.*;

import java.util.List;

@Controller
@RestController
@RequiredArgsConstructor
public class MainController {
    Region region = Region.AP_SOUTH_1;
    String accessKeyId = "AKIAQ3EGUEAVW362GJ62"; // Replace with your Access Key ID
    String secretAccessKey = "AxDmifkNAlO64AgbEhztZJuQr+I1oK9GABpHvZ3x"; // Replace with your Secret Access Key
    String clusterName = "dockapi-builder-cluster"; // Replace with your ECS Cluster name
    String taskArn = "arn:aws:ecs:ap-south-1:058264395819:task-definition/dockapi-builder-task:1"; // Replace with your ECS Task ARN
    String taskDefinition = "dockapi-builder-task";
    List<String> subnets = List.of("subnet-0d205e13487806ead", "subnet-0f44d528632036407", "subnet-09c3d3937fcf77ad8"); // Replace with your subnets
    List<String> securityGroups = List.of("sg-05617ce35c5182626");

    final EcsClient ecsClient = EcsClient.builder()
            .region(region)
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
            .build();
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);





    @GetMapping("")
    public String home() {
        return "index";
    }
    @PostMapping("/upload-project")
    public String uploadProject(@RequestBody ProjectRequestBody projectRequestBody) {
        try {
            String slug = projectRequestBody.getProjectName()==null?Utility.generateSlug():projectRequestBody.getProjectName();
            logger.info("Generated slug: {}", slug);
            logger.info("Github Url: {}",projectRequestBody.getGithubUrl());
            logger.info("Project Name: {}",projectRequestBody.getProjectName());
           //  Configure network settings
            AwsVpcConfiguration vpcConfig = AwsVpcConfiguration.builder()
                    .assignPublicIp(AssignPublicIp.ENABLED)
                    .subnets(subnets)
                    .securityGroups(securityGroups)
                    .build();

            NetworkConfiguration networkConfig = NetworkConfiguration.builder()
                    .awsvpcConfiguration(vpcConfig)
                    .build();

            // Configure container overrides
            ContainerOverride containerOverride = ContainerOverride.builder()
                    .name("docker-builder-image")
                    .environment(
                            KeyValuePair.builder().name("GIT_REPOSITORY_URL").value(projectRequestBody.getGithubUrl()).build(),
                            KeyValuePair.builder().name("PROJECT_ID").value(slug).build()
                    )
                    .build();

            TaskOverride taskOverride = TaskOverride.builder()
                    .containerOverrides(containerOverride)
                    .build();

            // Create RunTaskRequest
            RunTaskRequest runTaskRequest = RunTaskRequest.builder()
                    .cluster(clusterName)
                    .taskDefinition(taskDefinition)
                    .launchType(LaunchType.FARGATE)
                    .count(1)
                    .networkConfiguration(networkConfig)
                    .overrides(taskOverride)
                    .build();

            // Run the task
            RunTaskResponse response = ecsClient.runTask(runTaskRequest);
            response.tasks().forEach(task -> System.out.println("Started Task ARN: " + task.taskArn()));

        } catch (EcsException e) {
            System.err.println("Failed to run ECS task: " + e.awsErrorDetails().errorMessage());
        }
        return "Success";
    }}


