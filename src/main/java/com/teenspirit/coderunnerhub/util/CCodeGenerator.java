package com.teenspirit.coderunnerhub.util;

import com.teenspirit.coderunnerhub.dto.SolutionDTO;
import com.teenspirit.coderunnerhub.model.CodeRequest;

import java.io.*;
import java.util.UUID;

public class CCodeGenerator {

    public static File generateCCode(CodeRequest codeRequest) throws IOException {
        String uniqueFilename = "main_" + UUID.randomUUID().toString().replace("-", "");
        File tempFile = new File(System.getProperty("java.io.tmpdir"), uniqueFilename + ".c");

        try (PrintWriter writer = new PrintWriter(tempFile)) {
            writer.write("#include <math.h>\n");
            writer.write("#include <stdlib.h>\n");
            writer.write("#include <stdio.h>\n");
            writer.write("#include <string.h>\n");
            writer.write("#include <malloc.h>\n");

            writer.write(codeRequest.getCode());

            writer.write("\nint main(int argc, char *argv[]) {\n");
            for (int i = 0; i < codeRequest.getArguments().size(); i++) {
                SolutionDTO.ArgumentDTO argument = codeRequest.getArguments().get(i);
                writer.write("    " + argument.getType() + " " + argument.getName() + " = ");
                if ("int".equals(argument.getType())) {
                    writer.write("atoi(argv[" + (i + 1) + "])");
                } else if ("float".equals(argument.getType())) {
                    writer.write("(float)atof(argv[" + (i + 1) + "])");
                } else if ("string".equals(argument.getType())) {
                    writer.write("argv[" + (i + 1) + "]");
                }
                writer.write(";\n");
            }
            writer.println("    " + codeRequest.getReturnType() + " result = " + codeRequest.getFuncName() + "(");
            for (int i = 0; i < codeRequest.getArguments().size(); i++) {
                writer.print(codeRequest.getArguments().get(i).getName());
                if (i < codeRequest.getArguments().size() - 1) {
                    writer.print(", ");
                }
            }

            writer.println(");");
            writer.write("    FILE *outputFile = fopen(\"" + uniqueFilename + ".txt\", \"w\");\n");

            writer.write("    fprintf(outputFile, \"");
            if ("int".equals(codeRequest.getReturnType())) {
                writer.write("%d");
            } else if ("float".equals(codeRequest.getReturnType())) {
                writer.write("%f");
            } else if ("string".equals(codeRequest.getReturnType())) {
                writer.write("%s");
            }
            writer.write("\", result);\n");

            writer.write("    fclose(outputFile);\n");
            writer.write("    return 0;\n");
            writer.write("}\n");
        }

        return tempFile;
    }
}