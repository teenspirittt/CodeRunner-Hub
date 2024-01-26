package com.teenspirit.coderunnerhub.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teenspirit.coderunnerhub.dto.ProblemDTO;
import com.teenspirit.coderunnerhub.exceptions.BadRequestException;
import com.teenspirit.coderunnerhub.exceptions.InternalServerErrorException;
import com.teenspirit.coderunnerhub.model.CodeRequest;
import com.teenspirit.coderunnerhub.model.ExecuteResponse;

import java.io.*;

public class CCodeExecutor {

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
                    writer.write("    " + argument.getType() + " " + argument.getName() + " = " + getRandomValue(argument.getType()) + ";\n");
                }
                writer.println("    " + codeRequest.getReturnType() + " result = " + codeRequest.getFuncName() + "(");
                for (int i = 0; i < codeRequest.getArguments().size(); i++) {
                    writer.print(codeRequest.getArguments().get(i).getName());
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
                writer.write("    fflush(stdout);\n");
                writer.write("    return 0;\n");
                writer.write("}\n");
            }

            File outputTempFile = File.createTempFile("output", ".txt");

            ProcessBuilder compileProcessBuilder = new ProcessBuilder("gcc", "-o", tempFile.getAbsolutePath().replace(".c", ""), tempFile.getAbsolutePath());

            compileProcessBuilder.redirectOutput(outputTempFile);
            compileProcessBuilder.redirectErrorStream(true);
            Process compileProcess = compileProcessBuilder.start();


            int compilationResult = compileProcess.waitFor();
            String outputContent = readOutputFile(outputTempFile);

            if (compilationResult != 0) {
                return new ExecuteResponse(false, "Error while execute code", null, outputContent, 0, 0);
            }

            return new ExecuteResponse(true, "Code executed successfully", outputContent, null, 0, 0);

        } catch (IOException | InterruptedException e) {
            throw new InternalServerErrorException(e.getMessage());
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

    private String readOutputFile(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
}
