from flask import Blueprint, request, jsonify
from controllers.assignment_controller import execute_code_controller, get_assignment_code

assignment_routes = Blueprint("assignment_routes", __name__)

@assignment_routes.route('/execute', methods=['POST'])
def execute_code():
    json_data = request.get_json()
    if not json_data:
        return jsonify({"error": "Invalid JSON data"}), 400

    result = execute_code_controller(json_data)
    return jsonify(result), 200


@assignment_routes.route('/get_code', methods=['GET'])
def get_assignment():
    student_id = request.args.get('student_id')
    problem_id = request.args.get('problem_id')

    code = get_assignment_code(student_id, problem_id)
    if code is None:
        return jsonify({"error": "Assignment not found"}), 404
    return jsonify({"code": code}), 200
