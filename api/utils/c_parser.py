import re

def parse_c_code(code):
    # Паттерн для поиска функций в коде C
    pattern = r'\b(\w+(\s*\**))\s+(\w+)\s*\(([^)]*)\)'
    
    match = re.search(pattern, code)
    if match:
        return_type = match.group(1)
        function_name = match.group(3)
        arguments = [arg.strip() for arg in match.group(4).split(',')]
        return return_type, function_name, arguments
    else:
        return None
        


#c_code = "class Hello {\npublic:\n    int sum(int num1, int num2) {\n        \n    }\n};"
#return_type, function_name, arguments = parse_c_code(c_code)
#print(f"Return Type: {return_type}")
#print(f"Function Name: {function_name}")
#print(f"Arguments: {[arg for arg in arguments]}")