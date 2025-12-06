
import re

def extract_skills(text):
    # Expanded skill list
    common_skills = [
        "Java", "Python", "C++", "C#", "JavaScript", "TypeScript", "Angular", "React", "Vue", 
        "Spring Boot", "Node.js", "Django", "Flask", "SQL", "NoSQL", "MongoDB", "PostgreSQL", 
        "HTML", "CSS", "AWS", "Azure", "GCP", "Docker", "Kubernetes", "Git", "CI/CD",
        "Machine Learning", "AI", "Deep Learning", "NLP", "Rust", "Go", "Swift", "Kotlin",
        "Ruby", "PHP", "Terraform", "Ansible", "Jenkins", "Linux", "Unix"
    ]
    found_skills = []
    for skill in common_skills:
        # Escape the skill for regex
        escaped_skill = re.escape(skill)
        # Use a pattern that handles boundaries correctly for special chars
        # If skill starts with word char, use \b, else allow any non-word char before
        # If skill ends with word char, use \b, else allow any non-word char after
        
        start_bound = r'\b' if re.match(r'\w', skill[0]) else r'(?:^|[\s\(\[\{,])'
        # Allow punctuation after the skill if it ends with a symbol
        end_bound = r'\b' if re.match(r'\w', skill[-1]) else r'(?:$|[\s\.,;:\?!])'
        
        pattern = start_bound + escaped_skill + end_bound
        
        if re.search(pattern, text, re.IGNORECASE):
            found_skills.append(skill)
    return found_skills

test_text = """
I am a software engineer with experience in Java, Python, and C++.
I also know Rust and Go.
I have worked with Spring Boot and React.
"""

print("Text:", test_text)
extracted = extract_skills(test_text)
print("Extracted:", extracted)

missing = []
expected = ["Java", "Python", "C++", "Rust", "Go", "Spring Boot", "React"]
for skill in expected:
    if skill not in extracted:
        missing.append(skill)

print("Missing expected skills:", missing)

# Test Email and Phone Extraction
def extract_email(text):
    email_pattern = r'[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}'
    match = re.search(email_pattern, text)
    return match.group(0) if match else "Not Found"

def extract_phone(text):
    phone_pattern = r'(\+?\d{1,3}[-.\s]?)?(\(?\d{3}\)?[-.\s]?)?\d{3}[-.\s]?\d{4}'
    match = re.search(phone_pattern, text)
    return match.group(0) if match else "Not Found"

test_text_with_contact = """
I am a software engineer with experience in Java, Python, and C++.
Contact me at test.user@example.com or (555) 123-4567.
"""

print("\nTesting Contact Extraction:")
email = extract_email(test_text_with_contact)
phone = extract_phone(test_text_with_contact)
print(f"Email: {email}")
print(f"Phone: {phone}")

if email == "test.user@example.com" and phone == "(555) 123-4567":
    print("Contact extraction verified successfully.")
else:
    print("Contact extraction failed.")
