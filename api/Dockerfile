FROM python:3.8

WORKDIR /
COPY requirements.txt .
RUN pip install -r requirements.txt

EXPOSE 5000

CMD ["python", "api.py"]
