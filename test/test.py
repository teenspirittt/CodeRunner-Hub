import requests
import json

api_url = "http://localhost:5000/execute"

data = {
    "student_id": 8,
        "problem_id": 111,
        "language": "cpp",
        "code": "class Hello {\npublic:\n    int sum(int num1, int num2) {\n        \n    }\n};",
}

response = requests.post(api_url, json=data)


print(f"Response status code: {response.status_code}")
print("Response text:")
print(response.text)
