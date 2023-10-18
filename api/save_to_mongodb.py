import pymongo

def save_to_mongodb(student_id, problem_id, programming_language, code):
    try:
        client = pymongo.MongoClient("mongodb://mongo:27017/")
        db = client["codes"]

        collection = db["problems"]

        assignment_data = {
            "student_id": student_id,
            "problem_id": problem_id,
            "programming_language": programming_language,
            "code": code
        }

        collection.insert_one(assignment_data)

        client.close()

        return True
    except Exception as e:
        print(f"Error while saving to MongoDB: {str(e)}")
        return False
