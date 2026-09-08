# Enterprise Cloud-Native AI Resume Analyzer

An enterprise-grade, cloud-native REST API built with **Java 21**, **Spring Boot**, and **Google Gemini 3.6 Flash**. This system ingests PDF resumes, enforces strict PII data redaction for compliance, and returns structured skill analysis with actionable recommendations using generative AI.

---

## 🏛 Architecture & Data Flow

```
[ Candidate PDF ]
│
▼
[ Spring Boot REST Controller ]
│
├──► [ Google Cloud Storage (GCS) ] (Raw Document Archival & Fallback)
│
▼
[ Apache PDFBox 3.x ] (Stream Extraction)
│
▼
[ PII Redaction Layer ] (Regex-driven masking of emails, phones, identifiers)
│
▼
[ Spring AI Client ] ──► [ Google Gemini 3.6 Flash ]
│
▼
[ Strongly Typed DTO Schema ]
│
▼
[ JSON Output: Match %, Skills, STAR Bullet Recommendations ]
```

---

## 🚀 Key Engineering Features

* **Privacy-First Data Governance:** Built-in regex-based PII scrubbing service (`PiiRedactionService`) that strips emails, phone numbers, and personally identifiable information before sending to LLM. All masking patterns are configurable.

* **Strong Type Safety:** Structured entity mapping deserializes Gemini JSON responses directly into immutable Java DTO records (`ResumeAnalysisResponse`). Eliminates untyped string parsing.

* **Stream-Safe PDF Processing:** Apache PDFBox 3.x processes incoming documents via buffered streams, optimizing garbage collection and reducing memory footprint for large batches.

* **Global Exception Handling:** Centralized error handling via `@RestControllerAdvice` returns RFC-compliant JSON error responses with specific HTTP status codes (400, 404, 500) and descriptive messages.

* **Cloud-Native Infrastructure:** Infrastructure-as-Code setup via `infra/setup.sh` automates GCP project configuration, API enablement, and GCS bucket provisioning.

---

## 🛠 Tech Stack

| Component | Technology |
| --- | --- |
| **Language & Framework** | Java 21, Spring Boot 3.4.x, Maven |
| **Cloud & Storage** | Google Cloud Storage (GCS), Google Cloud CLI |
| **AI & LLM** | Google Gemini 3.6 Flash via Spring AI Starter |
| **Document Processing** | Apache PDFBox 3.x |
| **Code Quality & Logging** | Project Lombok, Jakarta Servlet API, SLF4J |

---

## 📡 API Reference

### Analyze Resume
Evaluates a candidate's resume against a targeted job description and returns skill match analysis.

**Endpoint:** `POST /api/v1/resumes/analyze`  
**Content-Type:** `multipart/form-data`

#### Request Parameters
| Parameter | Type | Required | Description |
| --- | --- | --- | --- |
| `file` | File (`.pdf`) | Yes | Candidate's resume (PDF format) |
| `jobDescription` | String | Yes | Target job description in plain text |

#### Example Request
```bash
curl -X POST "http://localhost:8085/api/v1/resumes/analyze" \
  -F "file=@resume.pdf" \
  -F "jobDescription=Backend Engineer with Java, Spring Boot, REST APIs, Docker, and AWS experience required"
```

#### Example Response (200 OK)
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
    "AWS / Cloud Infrastructure"
  ],
  "recommendations": [
    "Add specific STAR-format bullets describing containerization and orchestration work.",
    "Include quantifiable metrics for performance tuning and database optimization projects."
  ],
  "summary": "Strong backend Java fundamentals demonstrated. Cloud infrastructure skills should be expanded with hands-on Docker and AWS experience."
}
```

---

## 🚀 Local Development Setup

### Prerequisites

- **Java 21 JDK** installed
- **Google Cloud CLI** (`gcloud`) authenticated with appropriate GCP project
- **Google Gemini API Key** from [Google AI Studio](https://aistudio.google.com)

### Step 1: Infrastructure Setup

Run the provisioning script to enable APIs and create the GCS bucket:

```bash
chmod +x infra/setup.sh
./infra/setup.sh
```

This script:
- Enables Vertex AI and Cloud Storage APIs
- Creates a regional GCS bucket for resume storage

### Step 2: Configure Application Properties

Edit `src/main/resources/application.properties`:

```properties
server.port=8085
spring.ai.google.genai.api-key=YOUR_ACTUAL_API_KEY
spring.ai.google.genai.chat.options.model=gemini-3.6-flash
spring.cloud.gcp.project-id=YOUR_GCP_PROJECT_ID
gcp.storage.bucket-name=YOUR_GCS_BUCKET_NAME
```

### Step 3: Build and Run

```bash
./mvnw clean compile
./mvnw spring-boot:run
```

The API will start on `http://localhost:8085`.

---

## 📋 Project Structure

```
src/main/java/com/yourname/resumeanalyzer/
├── controller/        # REST API endpoints
├── service/           # Business logic (PII redaction, Gemini calls)
├── model/             # DTOs and response schemas
├── config/            # Spring configuration for GCP and AI
└── exception/         # Global exception handlers
```

---

## 🔒 Security & Compliance

- **PII Masking:** All emails, phone numbers, and sensitive identifiers are redacted before LLM processing
- **Data Retention:** Uploaded PDFs are stored in GCS with automatic lifecycle policies
- **API Authentication:** Ready for Spring Security OAuth2 integration (Phase 2)
- **Error Responses:** Never expose stack traces or sensitive configuration in API responses

---

## 📝 License

This project is provided as-is for educational and commercial use.

---

## 👤 Author

**Kinzalune** – Enterprise Java & Cloud Architecture  
GitHub: [@Kinzalune](https://github.com/Kinzalune)
