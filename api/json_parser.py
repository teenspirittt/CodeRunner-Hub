def parse_json(json_data):
    try:
        student_id = json_data['student_id']
        problem_id = json_data['problem_id']
        programming_language = json_data['language']
        code = json_data['code']

        return {
            'student_id': student_id,
            'problem_id': problem_id,
            'language': programming_language,
            'code': code
        }
    except KeyError as e:
        raise Exception(f"Missing required field: {e}")
