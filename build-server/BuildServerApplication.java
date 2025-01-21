import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BuildServerApplication {
	// Create a logger instance
	private static final Logger logger = LoggerFactory.getLogger(BuildServerApplication.class);

	// Method to execute command synchronously
	public static Boolean executeCommand(String[] command, String workingDir) {
		try {
			// Create a ProcessBuilder to execute the command
			ProcessBuilder processBuilder = new ProcessBuilder(command);
			processBuilder.directory(new java.io.File(workingDir)); // Set the working directory

			// Start the process
			Process process = processBuilder.start();

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
				logger.info("Command executed successfully.");
				printLog("Command executed successfully.");
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
		init();
	}

	private static void init() {
		printLog("Starting the Build Server Application...");
		Path outputDirectoryPath = Paths.get("output").toAbsolutePath();

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
		Boolean npmBuildSuccess = executeCommand(npmBuildCommand, outputDirectoryPath.toString());

		// Check if all commands executed successfully and get path of dist/ folder inside output directory
		if (npmInstallSuccess && npmBuildSuccess) {
			Path distDirectoryPath = outputDirectoryPath.resolve("dist");
			logger.info("Build completed successfully. Output directory: " + distDirectoryPath);
			printLog("Build completed successfully. Output directory: " + distDirectoryPath);
		} else {
			logger.error("Build failed. Check the logs for details.");
			printLog("Build failed. Check the logs for details.");
		}
	}
}
