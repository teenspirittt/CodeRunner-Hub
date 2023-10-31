from flask import Flask
from routes.assignment_routes import assignment_routes
from flask_swagger_ui import get_swaggerui_blueprint


def create_app():
    app = Flask(__name__)
    app.register_blueprint(assignment_routes)
    return app

app = create_app()

SWAGGER_URL = '/docs'
API_URL = '/static/swagger.json'
SWAGGERUI_BLUEPRINT = get_swaggerui_blueprint(
    SWAGGER_URL,
    API_URL,
    config={
        'app_name': "API-MongoDB"
    }
)


app.register_blueprint(SWAGGERUI_BLUEPRINT,url_prefix=SWAGGER_URL)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
