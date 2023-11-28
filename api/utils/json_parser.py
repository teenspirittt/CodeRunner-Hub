def parse_json(json_data):
    try:
        appointment_id = json_data['appointmentId']
        programming_language = json_data['language']
        code = json_data['code']
        func_name = json_data['funcName']

        return (
            {
                'appointmentId': appointment_id,
                'language': programming_language,
                'code': code,
                'funcName': func_name
            },
            200
        )
    except KeyError as e:
        return (
            {"error": f"Missing required field: {e}"},
            400
        )
