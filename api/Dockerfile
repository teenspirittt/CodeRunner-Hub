FROM python:3.8

WORKDIR /
RUN apt-get update && \
    apt-get install -y clang libclang-14-dev
ENV LD_LIBRARY_PATH /usr/lib/llvm-14/lib


COPY requirements.txt .
RUN pip install -r requirements.txt

EXPOSE 5000

CMD ["python", "api.py"]