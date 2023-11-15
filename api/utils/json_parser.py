def parse_json(json_data):
    try:
        appointment_id = json_data['appointmentId']
        programming_language = json_data['language']
        code = json_data['code']

        return {
            'appointmentId': appointment_id,
            'language': programming_language,
            'code': code
        }
    except KeyError as e:
        raise Exception(f"Missing required field: {e}")
