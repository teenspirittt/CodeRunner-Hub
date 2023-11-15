import pymongo
from utils.c_parser import parse_c_code


def establish_connection():
    client = pymongo.MongoClient("mongodb://mongo:27017/")
    return client


def close_connection(client):
    client.close()


def save_to_mongodb(client, appointment_id, programming_language, code):
    try:
        db = client["codes"]
        collection = db["problems"]

        existing_assignment = collection.find_one({"appointmentId": appointment_id})

        if programming_language in ["c", "cpp", "java"]:
            return_type, function_name, arguments = parse_c_code(code)
        else:
            print(f"Unsupported programming language: {programming_language}")
            return False
 
        if existing_assignment:
            update_data = {
                "$set": {
                    "programmingLanguage": programming_language,
                    "code": code
                }
            }
            collection.update_one({"_id": existing_assignment["_id"]}, update_data)
        else:
            assignment_data = {
                "appointmentId": appointment_id,
                "programmingLanguage": programming_language,
                "code": code,
                "functionName": function_name,
                "returnType": return_type,
                "arguments": arguments
            }
            collection.insert_one(assignment_data)

        return True
    except Exception as e:
        print(f"Error while saving to MongoDB: {str(e)}")
        return False


def get_problem_by_student(client, appointment_id):
    try:
        db = client["codes"]
        collection = db["problems"]
        problem = collection.find_one({"appointmentId": appointment_id})
        return problem
    
    except Exception as e:
        print(f"Error while getting problem from MongoDB: {str(e)}")
        return None
    