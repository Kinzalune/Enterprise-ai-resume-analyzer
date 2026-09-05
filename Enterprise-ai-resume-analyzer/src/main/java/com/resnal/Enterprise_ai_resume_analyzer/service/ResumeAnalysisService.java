package com.resnal.Enterprise_ai_resume_analyzer.service;

import com.resnal.Enterprise_ai_resume_analyzer.dto.ResumeAnalysisResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ResumeAnalysisService {

    private final ChatClient chatClient;

    public ResumeAnalysisService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * Sends the sanitized resume text and job description to Gemini 2.0 Flash
     * and maps the response directly to ResumeAnalysisResponse.
     */
    public ResumeAnalysisResponse analyzeResume(String sanitizedResumeText, String jobDescription) {
        String prompt = """
            You are an expert enterprise ATS (Applicant Tracking System) reviewer and hiring manager.
            
            Compare the following sanitized resume text against the provided job description:
            
            --- RESUME TEXT ---
            %s
            
            --- JOB DESCRIPTION ---
            %s
            
            Analyze the alignment between the candidate's experience and the job requirements.
            Provide:
            1. matchPercentage: Integer between 0 and 100.
            2. matchedSkills: List of key technical and soft skills present in both.
            3. missingSkills: List of important skills required by the JD that are absent in the resume.
            4. recommendations: Concrete, actionable bullet points using the STAR method to improve the resume.
            5. summary: A concise 2-3 sentence overview of the candidate's suitability.
            """.formatted(sanitizedResumeText, jobDescription);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .entity(ResumeAnalysisResponse.class);
    }
}