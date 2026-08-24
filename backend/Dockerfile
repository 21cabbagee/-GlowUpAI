FROM python:3.12-slim

WORKDIR /app
COPY pyproject.toml README.md ./
COPY skinproof ./skinproof

RUN pip install --no-cache-dir .

ENV PYTHONUNBUFFERED=1
ENV PORT=8000
EXPOSE 8000

CMD ["sh", "-c", "uvicorn skinproof.api:app --host 0.0.0.0 --port \${PORT}"]
