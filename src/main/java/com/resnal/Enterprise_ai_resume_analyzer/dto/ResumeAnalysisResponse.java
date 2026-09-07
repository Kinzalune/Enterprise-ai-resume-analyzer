package com.resnal.Enterprise_ai_resume_analyzer.dto;

import java.util.List;

public record ResumeAnalysisResponse(
    int matchScore,
    List<String> matchedSkills,
    List<String> missingSkills,
    String summary,
    List<String> actionableRecommendations,
    String cloudStoragePath
) {}