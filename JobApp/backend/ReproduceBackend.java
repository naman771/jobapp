
import java.util.*;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class ReproduceBackend {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        String jobSkillsCsv = "Java, React";
        // "JavaScript" contains "Java", so simple string matching might fail if not careful
        // But the issue described is "Java" matching "JavaScript" (false positive)
        
        // Let's simulate the current implementation
        String parsedJson = "{\"skills\": [\"JavaScript\", \"React\"]}";
        
        System.out.println("Job Skills: " + jobSkillsCsv);
        System.out.println("Parsed JSON: " + parsedJson);
        
        double score = computeMatchScore(jobSkillsCsv, parsedJson);
        System.out.println("Score: " + score);
        
        if (score > 50 && score < 100) {
             // Expecting 50.0 because only React matches. Java != JavaScript
            System.out.println("Fix Verified: Java did NOT match JavaScript. Score is 50.0 (only React matched).");
        } else if (score == 100.0) {
            System.out.println("Issue Reproduced: Java matched JavaScript!");
        } else {
            System.out.println("Score: " + score);
        }
    }

    // Improved scoring: parse JSON and match exact skills
    private static double computeMatchScore(String jobSkillsCsv, String parsedJson) {
        if (jobSkillsCsv == null || jobSkillsCsv.isBlank() || parsedJson == null) return 0.0;
        
        Set<String> required = Arrays.stream(jobSkillsCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
                
        if (required.isEmpty()) return 0.0;

        Set<String> candidateSkills = new HashSet<>();
        try {
            // Parse the JSON to get the "skills" list
            Map<String, Object> data = objectMapper.readValue(parsedJson, new TypeReference<Map<String, Object>>(){});
            if (data.containsKey("skills")) {
                List<String> skillsList = (List<String>) data.get("skills");
                if (skillsList != null) {
                    candidateSkills = skillsList.stream()
                            .map(String::trim)
                            .map(String::toLowerCase)
                            .collect(Collectors.toSet());
                }
            }
        } catch (Exception e) {
            // Fallback or log error
            System.err.println("Error parsing resume JSON: " + e.getMessage());
            return 0.0;
        }

        int matched = 0;
        for (String req : required) {
            if (candidateSkills.contains(req)) {
                matched++;
            }
        }
        
        return (double) matched / required.size() * 100.0;
    }
}
