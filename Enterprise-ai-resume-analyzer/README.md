# Enterprise Cloud-Native AI Resume Analyzer

An enterprise-grade, cloud-native REST API built with **Java 21**, **Spring Boot**, and **Google Cloud GenAI / Gemini**. This system ingests resumes, enforces strict PII data redaction for governance and privacy, evaluates candidate profiles against specific job descriptions, and returns structured gap analysis and actionable recommendations.

---

## 🏛 Architecture & Data Flow

```text
[ Candidate PDF ]
  |
  v
[ Spring Boot REST Controller ]
  |
  +--> [ Google Cloud Storage (GCS) ] (Raw Document Archival & Fallback)
  |
  v
[ Apache PDFBox 3.x ] (Stream Extraction)
  |
  v
[ PII Redaction Layer ] (Regex-driven masking of emails, phones, identifiers)
  |
  v
[ Spring AI Client ] ----> [ Google Gemini 3.6 Flash ]
  |
  v
[ Strongly Typed DTO Schema ]
  |
  v
[ JSON Output: Match %, Skills, STAR Bullet Recommendations ]
```

---

## 🚀 Key Engineering Highlights

* **Privacy-First Data Governance:** Implemented a regex-based PII scrubbing service (`PiRedactionService`) that strips sensitive personally identifiable information (emails, phone numbers) prior to upstream LLM transmission.
* **Strong Type Safety:** Leveraged Spring AI's structured entity mapping to deserialize Gemini output directly into immutable Java DTO records (`ResumeAnalysisResponse`), eliminating arbitrary text parsing.
* **Stream-Safe PDF Extraction:** Processed incoming documents via Apache PDFBox using stream buffers to optimize garbage collection and thread memory consumption.
* **Robust Global Exception Architecture:** Centralized error handling across all endpoints via `@RestControllerAdvice`, delivering consistent RFC-compliant JSON responses for bad payloads, missing parameters, and document unprocessability.
* **Cloud-Native Automation:** Automated GCP bucket provisioning and API binding through Infrastructure-as-Code scripts (`infra/setup.sh`).

---

## 🛠 Tech Stack

* **Language & Core:** Java 21, Spring Boot 3 / 4, Maven
* **Cloud & Storage:** Google Cloud Storage (GCS), Google Cloud CLI
* **AI & LLM Services:** Google Gemini 3.6 Flash via Spring AI Starter
* **Document Processing:** Apache PDFBox 3.x
* **Data Scaffolding & Logging:** Project Lombok, Jakarta Servlet API

---

## 📡 API Reference

### Analyze Resume
Evaluates a candidate's resume against a targeted job description.

* **Endpoint:** `POST /api/v1/resumes/analyze`
* **Content-Type:** `multipart/form-data`

#### Request Parameters
| Parameter | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `file` | File (`.pdf`) | Yes | The candidate's resume document |
| `jobDescription` | String | Yes | Plain text target job description requirements |

#### Sample Request (`curl`)
```bash
curl.exe -X POST "http://localhost:8085/api/v1/resumes/analyze" \
  -F "file=@/path/to/resume.pdf" \
  -F "jobDescription=Looking for a Backend Java Engineer experienced in Spring Boot, REST APIs, and Cloud Infrastructure."
```

#### Sample Response (200 OK)

```json
{
  "matchPercentage": 78,
  "matchedSkills": [
    "Java",
    "Spring Boot",
    "REST APIs",
    "System Design"
  ],
  "missingSkills": [
    "Docker",
    "Cloud Infrastructure (GCP/AWS)"
  ],
  "recommendations": [
    "Include specific STAR-format bullets describing cloud deployment experience.",
    "Detail your performance tuning work with database queries under load."
  ],
  "summary": "Candidate exhibits strong core Java capabilities but requires greater visibility into automated container deployment and cloud infra."
}
```

## Local Development Setup

### 1. Prerequisites

- Java 21 JDK
- Maven Wrapper (`./mvnw` or `mvnw.cmd`)
- Google Cloud CLI (`gcloud`) authenticated
- Google Gemini API key from Google AI Studio

### 2. Infrastructure Initialization

Run the provisioning script to set your GCP project context and create the storage bucket:

```bash
chmod +x infra/setup.sh
./infra/setup.sh
```

### 3. Application Properties

Configure your credentials in `src/main/resources/application.properties`:

```properties
server.port=8085
spring.ai.google.genai.api-key=YOUR_GEMINI_API_KEY
spring.ai.google.genai.chat.options.model=gemini-3.6-flash
spring.cloud.gcp.project-id=YOUR_PROJECT_ID
gcp.storage.bucket-name=YOUR_BUCKET_NAME
```

### 4. Build and Run

```bash
./mvnw clean compile
./mvnw spring-boot:run
```