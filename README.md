# Enterprise Cloud-Native AI Resume Analyzer

An enterprise-grade, cloud-native REST API built with **Java 21**, **Spring Boot**, and **Google Cloud GenAI / Gemini**. This system ingests resumes, enforces strict PII data redaction for governance and privacy, evaluates candidate profiles against specific job descriptions, and returns structured gap analysis and actionable recommendations.

---

## 🏛 Architecture & Data Flow

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

#### Sample JSON Response (200 OK)

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

- Java 21 JDK installed.
- Google Cloud CLI (`gcloud`) authenticated.
- Google Gemini API Key from Google AI Studio.

### 2. Infrastructure Initialization

Run the infrastructure provisioning script to bind your GCP project and provision the regional GCS bucket:

```bash
chmod +x infra/setup.sh
./infra/setup.sh
```

### 3. Configure Properties

Ensure `src/main/resources/application.properties` contains your credentials:

```properties
server.port=8085
spring.ai.google.genai.api-key=YOUR_ACTUAL_API_KEY
spring.ai.google.genai.chat.options.model=gemini-3.6-flash
spring.cloud.gcp.project-id=YOUR_GCP_PROJECT_ID
gcp.storage.bucket-name=YOUR_GCS_BUCKET_NAME
```

### 4. Build and Run

```bash
./mvnw clean compile
./mvnw spring-boot:run
```

## Step 2: Commit the Documentation to Git

In your terminal:

```powershell
git add README.md
git commit -m "docs: add enterprise architecture documentation and API specification"
```

To build a professional, enterprise-grade AI Resume Analyzer, we will follow a high-standard engineering workflow. This ensures your GitHub repository demonstrates not just coding skills, but also your understanding of Cloud Architecture, Security, and DevOps.

🛠️ Phase 1: GitHub & Local Environment Setup
Before writing code, we set up the "Command Center."

Create a New GitHub Repository:

Name: enterprise-ai-resume-analyzer

Visibility: Public (to showcase your work).

Initialize: Add a .gitignore (choose the Java template) and a README.md.

Local Clone & Branching:

Bash
git clone https://github.com/your-username/enterprise-ai-resume-analyzer.git
cd enterprise-ai-resume-analyzer
git checkout -b feature/initial-setup
Spring Initializr (The Blueprint):
Go to start.spring.io and configure:

Project: Maven

Language: Java (Version 21)

Spring Boot: 3.4.x (Latest Stable)

Dependencies:

Spring Web: For REST APIs.

Lombok: To keep code clean.

GCP Support & GCP Storage: For Cloud Storage integration.

Spring AI Vertex AI: To connect to Gemini.

☁️ Phase 2: Google Cloud Infrastructure
Since you are showcasing your Google Cloud certifications, we use the gcloud CLI to automate setup.

Authentication:

Bash
gcloud auth application-default login
Project & API Configuration:
Create a script named infra/setup.sh in your project root to document this for recruiters:

Bash
# Enable necessary APIs
gcloud services enable aiplatform.googleapis.com # Vertex AI
gcloud services enable storage.googleapis.com   # Cloud Storage

# Create a bucket for resumes
gsutil mb -l us-central1 gs://your-unique-resume-bucket-name/
🏗️ Phase 3: Project Structure & First Service
Organize your code using a Layered Architecture, which is standard for enterprise Java.

1. Folder Structure
Plaintext
src/main/java/com/yourname/resumeanalyzer/
├── controller/     # API Endpoints (Routing only)
├── service/        # Business Logic (The "Brain")
├── model/          # Data objects (DTOs)
├── config/         # Cloud & AI configurations
└── repository/     # Data persistence (optional for RAG later)
2. The Storage Service (Java 21 + GCS)
Create ResumeStorageService.java. Using the Spring Resource abstraction is the professional way to handle cloud files.

Java
@Service
@RequiredArgsConstructor
public class ResumeStorageService {
    private final Storage storage;

    @Value("${gcp.bucket.name}")
    private String bucketName;

    public String uploadResume(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
        storage.create(blobInfo, file.getBytes());
        return fileName;
    }
}
