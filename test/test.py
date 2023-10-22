import requests
import json

# URL вашего API
api_url = "http://localhost:5000/execute"

# JSON данные для POST запроса
data = {
    "student_id": 5,
        "problem_id": 101,
        "language": "c++",
        "code": "class Solution {\npublic:\n    int sum(int num1, int num2) {\n        \n    }\n};",
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

# Отправляем POST запрос
response = requests.post(api_url, json=data)

# Печатаем результат
print(f"Response status code: {response.status_code}")
print("Response text:")
print(response.text)
