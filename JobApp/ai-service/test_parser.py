import requests

url = 'http://localhost:5001/parse'
files = {'file': open('sample_resume.pdf', 'rb')}

try:
    print("Testing AI Resume Parser...")
    print(f"Sending request to {url}")
    response = requests.post(url, files=files)
    print(f"\nStatus Code: {response.status_code}")
    print(f"\nParsed Resume Data:")
    print("=" * 60)
    
    data = response.json()
    
    print(f"Name: {data.get('name', 'Not found')}")
    print(f"Email: {data.get('email', 'Not found')}")
    print(f"Phone: {data.get('phone', 'Not found')}")
    print(f"\nSkills ({len(data.get('skills', []))} found):")
    for skill in data.get('skills', []):
        print(f"  - {skill}")
    
    print(f"\nExperience ({len(data.get('experience', []))} entries):")
    for exp in data.get('experience', []):
        print(f"  - {exp}")
    
    print(f"\nEducation ({len(data.get('education', []))} entries):")
    for edu in data.get('education', []):
        print(f"  - {edu}")
    
    print(f"\nCertifications ({len(data.get('certifications', []))} found):")
    for cert in data.get('certifications', []):
        print(f"  - {cert}")
    
    print("\n" + "=" * 60)
    print("✅ AI Resume Parser is working!")
    
except requests.exceptions.ConnectionError:
    print("❌ Error: Could not connect to AI service at http://localhost:5001")
    print("Make sure the AI service is running: python app.py")
except Exception as e:
    print(f"❌ Error: {e}")
finally:
    files['file'].close()
