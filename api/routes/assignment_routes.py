from flask import Blueprint, request, jsonify
from controllers.assignment_controller import execute_code_controller, get_assignment_code
from controllers.assignment_controller import get_all_assignments_data

assignment_routes = Blueprint("assignment_routes", __name__)

@assignment_routes.route('/codes/execute', methods=['POST'])
def execute_code():
    try:
        json_data = request.get_json()
        if not json_data:
            return jsonify({"error": "Invalid JSON data"}), 400

        result, status_code = execute_code_controller(json_data)
        return jsonify(result), status_code
    except Exception as e:
        return jsonify({"error": str(e)}), 500


@assignment_routes.route('/codes/<int:appointment_id>', methods=['GET'])
def get_assignment(appointment_id):
    try:
        code = get_assignment_code(appointment_id)
        if code is None:
            return jsonify({"error": f"Appointment not found {appointment_id}"}), 404
        return jsonify(code), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500


@assignment_routes.route('/codes', methods=['GET'])
def get_all_assignments():
    try:
       
        assignments = get_all_assignments_data()
      
        if assignments:
            return jsonify(assignments), 200
        else:
            return jsonify({"error": "No assignments found"}), 404
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    