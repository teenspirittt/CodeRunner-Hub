package com.teenspirit.coderunnerhub.util;

import com.teenspirit.coderunnerhub.dto.ProblemDTO;
import com.teenspirit.coderunnerhub.exceptions.BadRequestException;
import org.apache.commons.io.FileUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CAnalyzer {

    public static FunctionInfo analyzeCCode(String code, String functionName) throws IOException, InterruptedException {
        // Записываем код во временный файл
        String filename = "temp.c";
        FileUtils.writeStringToFile(new File(filename), code, StandardCharsets.UTF_8);

        // Выполняем анализ с использованием Clang
        ProcessBuilder processBuilder = new ProcessBuilder("clang", "-Xclang", "-ast-dump", filename);
        Process process = processBuilder.start();
        process.waitFor();

        // Читаем вывод
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            StringBuilder result = new StringBuilder();
            String line;
            boolean insideFunction = false;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                // Находим начало и конец нужной функции
                Matcher matcher = Pattern.compile("\\bFunctionDecl\\b.*\\b" + Pattern.quote(functionName) + "\\b").matcher(line);
                if (matcher.find()) {
                    insideFunction = true;
                } else if (line.equals("}") && insideFunction) {
                    break;
                }
                // Записываем строки функции
                if (insideFunction) {
                    result.append(line).append("\n");
                }
            }
            // Извлекаем информацию из AST
            return extractFunctionInfo(result.toString());
        }
    }

    private static FunctionInfo extractFunctionInfo(String functionCode) {
        FunctionInfo functionInfo = new FunctionInfo();

        // Регулярное выражение для извлечения типа возвращаемого значения, имени функции и аргументов
        String functionRegex = "-FunctionDecl .*? (\\w+) '(\\w+) \\((.*?)\\)'";
        Pattern functionPattern = Pattern.compile(functionRegex);
        Matcher functionMatcher = functionPattern.matcher(functionCode);
        if (functionMatcher.find()) {
            String functionType = functionMatcher.group(2);
            functionInfo.setReturnType(functionType);

            String parameterRegex = "\\|-ParmVarDecl .*? (\\w+) '(\\w+)'";
            Pattern parameterPattern = Pattern.compile(parameterRegex);
            Matcher parameterMatcher = parameterPattern.matcher(functionCode);

            while (parameterMatcher.find()) {
                String argumentName = parameterMatcher.group(1);
                String argumentType = parameterMatcher.group(2);

                functionInfo.addArgument(new ProblemDTO.ArgumentDTO(argumentType, argumentName));
            }
        } else {
            throw new BadRequestException("Function not found in code");
        }

        return functionInfo;
    }

    public static class FunctionInfo {

        public FunctionInfo() {
            this.arguments = new ArrayList<>();
        }

        private String returnType;
        private List<ProblemDTO.ArgumentDTO> arguments;

        public String getReturnType() {
            return returnType;
        }

        public void addArgument(ProblemDTO.ArgumentDTO arg) {
            arguments.add(arg);
        }

        public void setReturnType(String returnType) {
            this.returnType = returnType;
        }

        public List<ProblemDTO.ArgumentDTO> getArguments() {
            return arguments;
        }

        public void setArguments(List<ProblemDTO.ArgumentDTO> arguments) {
            this.arguments = arguments;
        }

        @Override
        public String toString() {
            return "FunctionInfo{" +
                    "returnType='" + returnType + '\'' +
                    ", arguments=" + arguments +
                    '}';
        }
    }
}