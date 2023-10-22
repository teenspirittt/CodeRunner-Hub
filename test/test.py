import requests
import json

api_url = "http://localhost:5000/execute"

data = {
    "student_id": 5,
        "problem_id": 110,
        "language": "c++",
        "code": "class Hello {\npublic:\n    int sum(int num1, int num2) {\n        \n    }\n};",
        "function_name": "sum",
        "arguments": [
            {
                "name": "num1",
                "type": "int"
            },
            {
                "name": "num2",
                "type": "int"
            }
        ],
        "return_type": "int"
}

response = requests.post(api_url, json=data)


print(f"Response status code: {response.status_code}")
print("Response text:")
print(response.text)
