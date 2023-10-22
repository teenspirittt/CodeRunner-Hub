from controllers.db_handler import establish_connection, save_to_mongodb, get_problem_by_student, close_connection
from json_parser import parse_json

def execute_code_controller(json_data):
    try:
        data = parse_json(json_data)
        client = establish_connection()
        saved = save_to_mongodb(client, data['student_id'], data['problem_id'], data['language'], data['code'])
        if not saved:
            close_connection(client)
            return {"error": "Failed to save data to MongoDB"}, 500
        result = "aboba"
        return {"message": result}, 200
    except Exception as e:
        return {"error": str(e)}, 500
    

    
def get_assignment_code(student_id, problem_id):
    try:
        client = establish_connection()
        assignment = get_problem_by_student(client, student_id, problem_id)
        close_connection(client)
        if assignment:
            return assignment["code"]
        else:
            return None
    except Exception as e:
        return None
