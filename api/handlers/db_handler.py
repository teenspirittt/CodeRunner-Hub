import pymongo
from utils.c_parser import parse_function


def establish_connection():
    client = pymongo.MongoClient("mongodb://mongo:27017/")
    return client


def close_connection(client):
    client.close()


def save_to_mongodb(client, appointment_id, programming_language, code, func_name):
    try:
        db = client["codes"]
        collection = db["problems"]

        existing_assignment = collection.find_one({"appointmentId": appointment_id})

        if programming_language in ["c", "cpp", "java"]:
            result = parse_function(code, func_name)
            if 'error' in result:
                return result, 400
            return_type, arguments = result['type'], result['arguments']
        else:
            return {"error": f"Unsupported programming language: {programming_language}"}, 400
        
        if existing_assignment:
            update_data = {
                "$set": {
                    "programmingLanguage": programming_language,
                    "code": code,
                    "func_name" : func_name
                }
            }
            collection.update_one({"_id": existing_assignment["_id"]}, update_data)
        else:
            assignment_data = {
                "appointmentId": appointment_id,
                "programmingLanguage": programming_language,
                "code": code,
                "functionName": func_name,
                "returnType": return_type,
                "arguments": arguments
            }
            collection.insert_one(assignment_data)

        return {"success": "The task was saved successfully"}, 200
    except Exception as e:
        return {"error": f"Error while saving to MongoDB: {str(e)}"}, 500



def get_problem_by_student(client, appointment_id):
    try:
        db = client["codes"]
        collection = db["problems"]
        problem = collection.find_one({"appointmentId": appointment_id})
        return problem
    
    except Exception as e:
        print(f"Error while getting problem from MongoDB: {str(e)}")
        return None
    