FROM python:3.8

WORKDIR /app
COPY requirements.txt .
RUN pip install -r requirements.txt

COPY api.py .
COPY json_parser.py .
COPY response_builder.py .


EXPOSE 5000

CMD ["python", "api.py"]
