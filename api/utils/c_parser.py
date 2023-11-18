import re

def parse_c_code(code):
    # Паттерн для поиска функций в коде C
    pattern = r'\b(\w+(\s*\**))\s+(\w+)\s*\(([^)]*)\)'
    
    match = re.search(pattern, code)
    if match:
        return_type = match.group(1)
        function_name = match.group(3)

        # Изменения здесь, чтобы получить более подробную информацию об аргументах
        arguments_str = match.group(4)
        arguments_list = []
        for arg in arguments_str.split(','):
            arg = arg.strip()
            arg_parts = arg.split()
            if len(arg_parts) == 2:
                arg_type, arg_name = arg_parts
                arguments_list.append({"type": arg_type, "name": arg_name})
            else:
                # Обработка случаев, когда аргумент не соответствует ожидаемому формату
                arguments_list.append({"type": "unknown", "name": arg})
        
        return return_type, function_name, arguments_list
    else:
        return None
        


#c_code = "class Hello {\npublic:\n    int sum(int num1, int num2) {\n        \n    }\n};"
#return_type, function_name, arguments = parse_c_code(c_code)
#print(f"Return Type: {return_type}")
#print(f"Function Name: {function_name}")
#print(f"Arguments: {[arg for arg in arguments]}")