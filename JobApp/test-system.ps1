# Job Portal System Test Script
Write-Host "=== Job Portal System Test ===" -ForegroundColor Cyan
Write-Host ""

# Test 1: Check Backend Health
Write-Host "1. Testing Backend (http://localhost:8080)..." -ForegroundColor Yellow
try {
    $backendResponse = Invoke-WebRequest -Uri "http://localhost:8080/jobs" -Method GET -UseBasicParsing -ErrorAction Stop
    Write-Host "   ✓ Backend is running" -ForegroundColor Green
} catch {
    Write-Host "   ✗ Backend is not accessible: $_" -ForegroundColor Red
    Write-Host "   Please start the backend with: cd backend; mvn spring-boot:run" -ForegroundColor Yellow
}

# Test 2: Check Frontend
Write-Host ""
Write-Host "2. Testing Frontend (http://localhost:4200)..." -ForegroundColor Yellow
try {
    $frontendResponse = Invoke-WebRequest -Uri "http://localhost:4200" -Method GET -UseBasicParsing -ErrorAction Stop
    Write-Host "   ✓ Frontend is running" -ForegroundColor Green
} catch {
    Write-Host "   ✗ Frontend is not accessible: $_" -ForegroundColor Red
    Write-Host "   Please start the frontend with: cd frontend; ng serve" -ForegroundColor Yellow
}

# Test 3: Check AI Service
Write-Host ""
Write-Host "3. Testing AI Service (http://localhost:5001)..." -ForegroundColor Yellow
try {
    $aiResponse = Invoke-WebRequest -Uri "http://localhost:5001" -Method GET -UseBasicParsing -ErrorAction Stop -TimeoutSec 2
    Write-Host "   ✓ AI Service is running" -ForegroundColor Green
} catch {
    Write-Host "   ⚠ AI Service is not accessible (resume parsing may not work)" -ForegroundColor Yellow
    Write-Host "   To start: cd ai-service; python app.py" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "=== Test Complete ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "Manual Testing Steps:" -ForegroundColor Cyan
Write-Host "1. Open http://localhost:4200 in your browser" -ForegroundColor White
Write-Host "2. Register/Login as a Recruiter" -ForegroundColor White
Write-Host "3. Post a job with skills (e.g., 'Java, Python, Spring Boot')" -ForegroundColor White
Write-Host "4. Logout and Register/Login as a Candidate" -ForegroundColor White
Write-Host "5. Upload a resume (PDF/DOCX)" -ForegroundColor White
Write-Host "6. Browse jobs - you should see match scores and recommendations" -ForegroundColor White
Write-Host "7. Apply for a job" -ForegroundColor White
Write-Host "8. Check candidate dashboard - should show 'Awaiting communication'" -ForegroundColor White
Write-Host "9. Logout and login as Recruiter again" -ForegroundColor White
Write-Host "10. View applicants for your job - should see the candidate" -ForegroundColor White
Write-Host "11. Click 'Acknowledge' or 'Reject'" -ForegroundColor White
Write-Host "12. Logout and login as Candidate again" -ForegroundColor White
Write-Host "13. Check candidate dashboard - should show updated status" -ForegroundColor White

