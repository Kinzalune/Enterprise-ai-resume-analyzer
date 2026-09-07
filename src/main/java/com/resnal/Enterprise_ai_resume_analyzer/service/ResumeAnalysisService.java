package com.resnal.Enterprise_ai_resume_analyzer.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.resnal.Enterprise_ai_resume_analyzer.dto.ResumeAnalysisResponse;

@Service
public class ResumeAnalysisService {

    private final ChatClient chatClient;

    public ResumeAnalysisService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public ResumeAnalysisResponse analyzeResume(String redactedResumeText, String targetRole, String cloudPath) {
        String prompt = """
            You are an expert enterprise technical recruiter and ATS auditor.
            Evaluate the following sanitized candidate resume for the role: '%s'.
            
            Sanitized Resume Text:
            \"\"\"%s\"\"\"
            
            Cloud Storage Reference: %s
            
            Provide the assessment matching these fields:
            1. matchScore: integer between 0 and 100 representing role fit.
            2. matchedSkills: list of technical skills found on the resume matching the role.
            3. missingSkills: list of missing skills essential for the role.
            4. summary: a concise professional summary of the candidate.
            5. actionableRecommendations: concrete bullet points using the STAR method for improvements.
            6. cloudStoragePath: string pointing to the cloud location.
            """.formatted(targetRole, redactedResumeText, cloudPath);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .entity(ResumeAnalysisResponse.class);
    }
}