import requests
import json

api_url = "http://localhost:5000/execute"

data = {
    "student_id": 45,
    "problem_id": 123,
    "language": "cpp",
    "code": "class stid {\n    public int artem(int num1, int num2) {\n        return num1 + num2;\n    }\n}",
}

response = requests.post(api_url, json=data)


print(f"Response status code: {response.status_code}")
print("Response text:")
print(response.text)
