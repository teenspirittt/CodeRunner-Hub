import pymongo

def establish_connection():
    client = pymongo.MongoClient("mongodb://mongo:27017/")
    return client

def save_to_mongodb(client, student_id, problem_id, programming_language, code):
    try:
        db = client["codes"]
        collection = db["problems"]
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

def close_connection(client):

    client.close()
