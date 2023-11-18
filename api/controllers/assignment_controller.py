from handlers.db_handler import establish_connection, save_to_mongodb, get_problem_by_student, close_connection
from utils.json_parser import parse_json


def execute_code_controller(json_data):
    try:
        data = parse_json(json_data)
        client = establish_connection()
        saved = save_to_mongodb(client, data['appointmentId'], data['language'], data['code'])
        if not saved:
            close_connection(client)
            return {"error": "Failed to save data to MongoDB"}, 500
        result = "aboba";
        return {"message": result}, 200
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
                "programmingLanguage" : assignment["programmingLanguage"],
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
                "programmingLanguage" : assignment["programmingLanguage"]
            }
            assignments_data.append(assignment_data)

        return assignments_data
    except Exception as e:
        print(f"Error while getting all assignments from MongoDB: {str(e)}")
        return None
