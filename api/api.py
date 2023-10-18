from flask import Flask, request, jsonify
from json_parser import parse_json
from response_builder import build_response
from mongodb.handler import establish_connection, save_to_mongodb, close_connection

app = Flask(__name__)

@app.route('/execute', methods=['GET'])
def execute_code():
    try:
        json_data = request.get_json()
        if not json_data:
            return jsonify({"error": "Invalid JSON data"}), 400

        data = parse_json(json_data)
        client = establish_connection()

        saved = save_to_mongodb(data['student_id'], data['problem_id'], data['programming_language'], data['assignment_code'])
        

        if not saved:
            close_connection(client)
            return jsonify({"error": "Failed to save data to MongoDB"}), 500
        
        return jsonify("meow"), 200

    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
