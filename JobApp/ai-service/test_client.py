import requests

url = 'http://localhost:5001/parse'
files = {'file': open('test_resume.pdf', 'rb')}
try:
    response = requests.post(url, files=files)
    print(response.status_code)
    print(response.json())
except Exception as e:
    print(e)
