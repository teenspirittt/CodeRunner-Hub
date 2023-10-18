def parse_json(json_data):
    try:
        student_id = json_data['student_id']
        problem_id = json_data['problem_id']
        programming_language = json_data['language']
        code = json_data['code']
        function_name = json_data['function_name']
        arguments = json_data['arguments']
        return_type = json_data['return_type']

        return {
            'student_id': student_id,
            'problem_id': problem_id,
            'language': programming_language,
            'code': code,
            'function_name': function_name,
            'arguments': arguments,
            'return_type': return_type
        }
    except KeyError as e:
        raise Exception(f"Missing required field: {e}")
