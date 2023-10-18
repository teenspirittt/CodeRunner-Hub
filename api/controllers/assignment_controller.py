from mongodb.handler import establish_connection, save_to_mongodb, get_assignment_by_student, close_connection
from json_parser import parse_json

def execute_code(json_data):
    try:
        data = parse_json(json_data)
        client = establish_connection()
        saved = save_to_mongodb(client, data['student_id'], data['problem_id'], data['programming_language'], data['assignment_code'])
        if not saved:
            close_connection(client)
            return {"error": "Failed to save data to MongoDB"}, 500
        return {"message": "Code executed successfully"}, 200
    except Exception as e:
        return {"error": str(e)}, 500

def get_assignment_code(student_id, problem_id):
    try:
        client = establish_connection()
        assignment = get_assignment_by_student(client, student_id, problem_id)
        close_connection(client)
        if assignment:
            return assignment["code"]
        else:
            return None
    except Exception as e:
        return None
