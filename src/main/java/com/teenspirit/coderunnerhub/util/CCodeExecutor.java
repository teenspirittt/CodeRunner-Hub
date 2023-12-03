package com.teenspirit.coderunnerhub.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teenspirit.coderunnerhub.dto.ProblemDTO;
import com.teenspirit.coderunnerhub.model.CodeRequest;
import com.teenspirit.coderunnerhub.model.ExecuteResponse;

import java.io.*;

public class CCodeExecutor {
    private final ObjectMapper objectMapper = new ObjectMapper();
    public ExecuteResponse executeCCode(CodeRequest codeRequest) {
        try {
            File tempFile = File.createTempFile("temp", ".c");
            try (PrintWriter writer = new PrintWriter(tempFile)) {
                writer.write("#include <math.h>\n");
                writer.write("#include <stdlib.h>\n");
                writer.write("#include <stdio.h>\n");
                writer.write("#include <string.h>\n");
                writer.write("#include <malloc.h>\n");

                writer.write(codeRequest.getCode());

                writer.write("\nint main() {\n");
                for (ProblemDTO.ArgumentDTO argument : codeRequest.getArguments()) {
                    writer.write("    " + argument.getName() + " " + argument.getType() + " = " + getRandomValue(argument.getName()) + ";\n");
                }
                writer.println("    " + codeRequest.getReturnType() + " result = " + codeRequest.getFuncName() + "(");
                for (int i = 0; i < codeRequest.getArguments().size(); i++) {
                    writer.print(codeRequest.getArguments().get(i).getType());
                    if (i < codeRequest.getArguments().size() - 1) {
                        writer.print(", ");
                    }
                }
                writer.println(");");
                writer.write("    printf(\"");
                if ("int".equals(codeRequest.getReturnType())) {
                    writer.write("%d");
                } else if ("float".equals(codeRequest.getReturnType())) {
                    writer.write("%f");
                } else if ("string".equals(codeRequest.getReturnType())) {
                    writer.write("%s");
                }
                writer.write("\", result);\n");
                writer.write("    return 0;\n");
                writer.write("}\n");
            }
            System.out.println(tempFile);

            ProcessBuilder compileProcessBuilder = new ProcessBuilder("gcc", "-o", tempFile.getAbsolutePath().replace(".c", ""), tempFile.getAbsolutePath());
            compileProcessBuilder.redirectErrorStream(true);
            Process compileProcess = compileProcessBuilder.start();

            StringBuilder compileOutput = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(compileProcess.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    compileOutput.append(line).append("\n");
                }
            }
            System.out.println();
            System.out.println(compileOutput.toString());
            System.out.println();

            int compilationResult = compileProcess.waitFor();

            if (compilationResult != 0) {
                return generateJsonResponse(false, "Execution error", compileOutput.toString(), "Error executing C code");
            }

            return generateJsonResponse(true, "Code executed successfully", compileOutput.toString(), null);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return generateJsonResponse(false, "Error executing C code", null, "Error executing C code");
        }
    }

    private String getRandomValue(String type) {
        if ("int".equals(type)) {
            return Integer.toString((int) (Math.random() * 100));
        } else if ("float".equals(type)) {
            return Float.toString((float) (Math.random() * 100.0));
        } else if ("string".equals(type)) {
            return "\"random_string\"";
        } else {
            return "0";
        }
    }

    private ExecuteResponse generateJsonResponse(boolean success, String message, String output, String error) {
        return new ExecuteResponse(success, message, output, error);
    }
}
