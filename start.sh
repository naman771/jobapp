#!/bin/bash

# Fix PATH for homebrew on macOS
export PATH="/opt/homebrew/bin:/usr/local/bin:$PATH"

echo "========================================="
echo "  JobPortal — Starting all services..."
echo "========================================="

# 1. Backend (Spring Boot + H2)
echo "[1/3] Starting Backend on :8080..."
(cd backend && mvn spring-boot:run) &
BACKEND_PID=$!

# 2. AI Service (Flask)
echo "[2/3] Starting AI Service on :5001..."
(
  cd ai-service
  if [ ! -d "venv" ]; then
      python3 -m venv venv
  fi
  source venv/bin/activate
  pip install -q -r requirements.txt
  python app.py
) &
AI_PID=$!

# 3. Frontend (Vite + React)
echo "[3/3] Starting Frontend on :5173..."
(cd frontend && npm run dev -- --port 5173) &
FRONTEND_PID=$!

echo ""
echo "========================================="
echo "  All services starting..."
echo "  Frontend:  http://localhost:5173"
echo "  Backend:   http://localhost:8080"
echo "  AI Parser: http://localhost:5001"
echo "========================================="
echo "Press Ctrl+C to stop all services."

cleanup() {
    echo ""
    echo "Stopping all services..."
    kill $BACKEND_PID $AI_PID $FRONTEND_PID 2>/dev/null
    wait $BACKEND_PID $AI_PID $FRONTEND_PID 2>/dev/null
    exit 0
}

trap cleanup SIGINT SIGTERM
wait
