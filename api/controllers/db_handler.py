import pymongo

def establish_connection():
    client = pymongo.MongoClient("mongodb://mongo:27017/")
    return client


def close_connection(client):
    client.close()


def save_to_mongodb(client, student_id, problem_id, programming_language, code):
    try:
        db = client["codes"]
        collection = db["problems"]

        existing_assignment = collection.find_one({"student_id": student_id, "problem_id": problem_id})
 
        if existing_assignment:
            update_data = {
                "$set": {
                    "programming_language": programming_language,
                    "code": code
                }
            }
            collection.update_one({"_id": existing_assignment["_id"]}, update_data)
        else:
            assignment_data = {
                "student_id": student_id,
                "problem_id": problem_id,
                "programming_language": programming_language,
                "code": code
            }
            collection.insert_one(assignment_data)

        return True
    except Exception as e:
        print(f"Error while saving to MongoDB: {str(e)}")
        return False


def get_problem_by_student(client, student_id, problem_id):
    try:
        db = client["codes"]
        collection = db["problems"]
        problem = collection.find_one({"student_id": student_id, "problem_id": problem_id})
        return problem
    
    except Exception as e:
        print(f"Error while getting problem from MongoDB: {str(e)}")
        return None
    