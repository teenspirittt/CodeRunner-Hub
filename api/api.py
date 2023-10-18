from flask import Flask
from api.routes.assignment_routes import assignment_routes

app = Flask(__name__)

# Регистрируем маршруты из assignment_routes
app.register_blueprint(assignment_routes)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
    