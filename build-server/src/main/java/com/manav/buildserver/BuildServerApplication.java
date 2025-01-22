package com.manav.buildserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

public class BuildServerApplication {
    // Create a logger instance
    private static final Logger logger = LoggerFactory.getLogger(BuildServerApplication.class);
    private static final S3Client s3 = S3Client.builder().credentialsProvider(
                    StaticCredentialsProvider.create(AwsBasicCredentials
                            .create(
                                    System.getenv("AWS_ACCESS_KEY_ID" ),
                                    System.getenv("AWS_SECRET_ACCESS_KEY" ))
                    ))

            .region(Region.AP_SOUTH_1)
            .build();
    private static final String BUCKET_NAME = "dockapi-build-server";
    private static final String PROJECT_ID = System.getenv("PROJECT_ID" );

    // Method to execute command synchronously
    public static Boolean executeCommand(String[] command, String workingDir) {
        try {
            printLog("Executing command: " + String.join(" ", command));
            // Create a ProcessBuilder to execute the command
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(new java.io.File(workingDir)); // Set the working directory

            // Start the process
            Process process = processBuilder.start();
            // print log

            // Read and handle standard output (stdout) from the process
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    logger.info(line); // Log the output as INFO level
                    printLog(line);   // Optionally, use a custom logging method
                }
            }

            // Read and handle error output (stderr) from the process
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = errorReader.readLine()) != null) {
                    logger.error(line); // Log error output as ERROR level
                    printLog(line);    // Optionally, use a custom logging method
                }
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                logger.error("Command failed with exit code: " + exitCode);
                printLog("Command failed with exit code: " + exitCode);
            } else {
                logger.info("Command executed successfully." );
                printLog("Command executed successfully." );
                return true;
            }

        } catch (IOException | InterruptedException e) {
            // Handle exceptions
            logger.error("Exception occurred: " + e.getMessage(), e);
            printLog("Exception occurred: " + e.getMessage());
        }
        return false;
    }

    // Method to simulate publishing logs (can be customized as needed)
    public static void printLog(String logMessage) {
        // Implement your actual logging system here, or just call logger
        logger.info("Published Log: " + logMessage);
    }

    public static void main(String[] args) {
        printLog("Welcome to dockapi ..." );
        init();
//		printLog("Starting the Build Server Application...");
    }

    private static void init() {
        printLog("Starting the Build Server Application..." );
        Path outputDirectoryPath = Paths.get("output" ).toAbsolutePath();
        printLog("Output directory: " + outputDirectoryPath);

        // Ensure the output directory exists
        if (!Files.exists(outputDirectoryPath)) {
            try {
                Files.createDirectories(outputDirectoryPath);
                logger.info("Created output directory at: " + outputDirectoryPath);
            } catch (IOException e) {
                logger.error("Failed to create output directory: " + e.getMessage(), e);
                return;
            }
        }

        // Commands to be executed
        String[] npmInstallCommand = {"npm", "install"};
        String[] npmBuildCommand = {"npm", "run", "build"};

        // Execute commands
        Boolean npmInstallSuccess = executeCommand(npmInstallCommand, outputDirectoryPath.toString());
        printLog("npm install command executed ..: " + npmInstallSuccess);
        Boolean npmBuildSuccess = executeCommand(npmBuildCommand, outputDirectoryPath.toString());
        printLog("npm build command executed ..: " + npmBuildSuccess);

        // Check if all commands executed successfully and get path of dist/ folder inside output directory
        if (npmInstallSuccess && npmBuildSuccess) {

            Path distDirectoryPath = outputDirectoryPath.resolve("dist");
            logger.info("Build completed successfully. Output directory: " + distDirectoryPath);
            printLog("Build completed successfully. Output directory: " + distDirectoryPath);

            // Get all files in dist/ directory recursively
            try {
                Files.walk(distDirectoryPath).forEach(filePath -> {
                    logger.info("File: " + filePath);
                    printLog("File: " + filePath);

                    // If the file is a directory, skip it
                    if (!Files.isDirectory(filePath)) {
                        try {
                            // Read the content of the file
                            String fileContent = Files.readString(filePath);

                            // Construct the S3 key using relative path, preserving directory structure
                            Path relativePath = distDirectoryPath.relativize(filePath);
                            String s3Key = "__outputs/" + PROJECT_ID + "/" + relativePath.toString().replace("\\", "/");

                            // Upload the file to S3
                            uploadFileToS3(filePath, s3Key, fileContent);
                        } catch (IOException e) {
                            logger.error("Failed to read file: " + filePath + " - " + e.getMessage(), e);
                            printLog("Failed to read file: " + filePath + " - " + e.getMessage());
                        }
                    }
                });

                printLog("All files uploaded to S3 successfully.");
            } catch (IOException e) {
                logger.error("Failed to list files in dist directory: " + e.getMessage(), e);
            }

        } else {
            logger.error("Build failed. Check the logs for details.");
            printLog("Build failed. Check the logs for details.");
        }

    }

	// upload file to s3 function
	private static void uploadFileToS3(Path path, String key, String fileContent) {
		try {
			String contentType = getContentType(path);
			s3.putObject(PutObjectRequest.builder()
					.bucket(BUCKET_NAME)
					.key(key)
							.contentType(contentType)
					.build(), RequestBody.fromString(fileContent));
			logger.info("Uploaded file to S3: " + key);
			printLog("Uploaded file to S3: " + key);
		} catch (S3Exception e) {
			logger.error("Failed to upload file to S3: " + e.getMessage(), e);
			printLog("Failed to upload file to S3: " + e.getMessage());
		}
	}

	private static String getContentType(Path path) {
		try {
			String contentType = Files.probeContentType(path);
			return contentType != null ? contentType : "application/octet-stream"; // Default MIME type
		} catch (Exception e) {
			e.printStackTrace();
			return "application/octet-stream"; // Default MIME type
		}
	}
}
