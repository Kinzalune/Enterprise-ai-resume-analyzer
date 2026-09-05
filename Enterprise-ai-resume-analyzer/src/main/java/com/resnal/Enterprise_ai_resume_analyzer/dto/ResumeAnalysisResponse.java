package com.resnal.Enterprise_ai_resume_analyzer.dto;

import java.util.List;

public record ResumeAnalysisResponse(
    int matchPercentage,
    List<String> matchedSkills,
    List<String> missingSkills,
    List<String> recommendations,
    String Summary
){}
    