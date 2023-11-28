from handlers.db_handler import establish_connection, save_to_mongodb, get_problem_by_student, close_connection
from utils.json_parser import parse_json


def execute_code_controller(json_data):
    try:
        data, status_code = parse_json(json_data)
        if 'error' in data:
            return data, status_code
        
        client = establish_connection()
        save_result, status_code = save_to_mongodb(client, data['appointmentId'], data['language'], data['code'], data['funcName'])
        close_connection(client)

        return save_result, status_code
    except Exception as e:
        return {"error": str(e)}, 500


def get_assignment_code(appointment_id):
    try:
        client = establish_connection()
        assignment = get_problem_by_student(client, appointment_id)
        close_connection(client)
        assignment_data = {}
        if assignment:
            assignment_data = {
                "language" : assignment["programmingLanguage"],
                "code": assignment["code"],
                "functionName" : assignment["functionName"],
                "returnType" : assignment["returnType"],
                "arguments" : assignment["arguments"]
            }
            return assignment_data
        else:
            return None
    except Exception as e:
        return None


def get_all_assignments_data():
    try:
        client = establish_connection()
        db = client["codes"]
        collection = db["problems"]
        assignments = list(collection.find())
        close_connection(client)

        assignments_data = []
        for assignment in assignments:
            appointment_id = assignment["appointmentId"]
            assignment_data = {
                "appointmentId": appointment_id,
                "code": assignment["code"],
                "language" : assignment["programmingLanguage"]
            }
            assignments_data.append(assignment_data)

        return assignments_data
    except Exception as e:
        print(f"Error while getting all assignments from MongoDB: {str(e)}")
        return None
