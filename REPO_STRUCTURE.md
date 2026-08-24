# 🌟 GlowUp AI - Complete Repository

This repository contains the **complete GlowUp AI platform** - both Android app and Backend API.

---

## 📂 Repository Structure

```
GlowUpAI/
├── app/                    # Android App (Kotlin + Jetpack Compose)
│   ├── src/main/
│   │   ├── java/com/glowup/ai/
│   │   │   ├── MainActivity.kt
│   │   │   ├── CameraScreen.kt
│   │   │   ├── ApiService.kt
│   │   │   └── ui/
│   │   └── res/
│   └── build.gradle.kts
│
├── backend/                # Backend API (Python + FastAPI)
│   ├── skinproof/          # Main backend application
│   │   ├── __init__.py
│   │   ├── main.py         # FastAPI app
│   │   ├── models.py       # Database models
│   │   ├── schemas.py      # Pydantic schemas
│   │   ├── api/            # API routes
│   │   └── services/       # Business logic
│   ├── tests/              # Backend tests
│   ├── docs/               # API documentation
│   ├── pyproject.toml      # Python dependencies
│   ├── Dockerfile          # Docker configuration
│   └── README.md           # Backend-specific docs
│
├── gradle/                 # Gradle wrapper (Android)
├── build.gradle.kts        # Android build config
├── settings.gradle.kts     # Android settings
├── gradlew                 # Gradle wrapper script
│
├── README.md               # Main project README
├── JOURNEY.md              # Development journey (15 milestones)
├── QUICK_SUMMARY.md        # 10-step executive summary
└── REPO_STRUCTURE.md       # This file
```

---

## 🚀 Quick Start

### **Android App**

**Prerequisites:**
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17+
- Android SDK 24+

**Setup:**
```bash
# 1. Open Android Studio
# 2. File → Open → Select this folder
# 3. Gradle will auto-sync
# 4. Run app on device/emulator
```

---

### **Backend API**

**Prerequisites:**
- Python 3.11+
- pip or poetry

**Setup:**
```bash
cd backend

# Install dependencies
pip install -e .

# Create .env file (copy from .env.example)
cp .env.example .env

# Run development server
uvicorn skinproof.main:app --reload --port 8000
```

**API will be available at:** `http://localhost:8000`

**API Docs:** `http://localhost:8000/docs`

---

## 🔗 Integration

The Android app connects to the backend API:

**Development:**
- Backend: `http://localhost:8000` (local testing)
- Update `ApiService.kt` with your backend URL

**Production:**
- Backend: Deploy to Railway/Render/AWS
- Update `BASE_URL` in `ApiService.kt`

---

## 👥 Team Workflow

### **For Android Developers:**
1. Work in root folder + `app/` directory
2. Update `ApiService.kt` for API changes
3. Test with local backend or mock data

### **For Backend Developers:**
1. Work in `backend/` directory
2. Update API routes in `backend/skinproof/api/`
3. Update docs when adding endpoints

### **For Both:**
1. Always pull latest changes: `git pull origin main`
2. Create feature branches: `git checkout -b feature/your-feature`
3. Push changes: `git push origin feature/your-feature`
4. Create Pull Request for review

---

## 🛠️ Tech Stack

**Android:**
- Kotlin
- Jetpack Compose
- CameraX + ML Kit
- Retrofit + OkHttp
- Firebase Auth

**Backend:**
- Python 3.11
- FastAPI
- PostgreSQL
- AWS S3/Cloudinary (images)
- HautAI/SkinProof APIs

---

## 📚 Documentation

- **Main README:** [README.md](README.md) - Project overview
- **Journey:** [JOURNEY.md](JOURNEY.md) - Complete development story (15 milestones)
- **Quick Summary:** [QUICK_SUMMARY.md](QUICK_SUMMARY.md) - 10-step overview
- **Backend Docs:** [backend/README.md](backend/README.md) - Backend-specific guide
- **API Docs:** Run backend and visit `http://localhost:8000/docs`

---

## 🚀 Deployment

**Android App:**
- Google Play Store (coming soon)
- Internal testing via Firebase App Distribution

**Backend:**
- Railway (recommended): `railway up`
- Render: Connect GitHub repo
- AWS: Use Dockerfile

---

## 🎯 Current Status

**Android App:** 85% feature-complete
- ✅ Camera + selfie capture
- ✅ AI skin analysis
- ✅ Progress tracking
- 🔄 Authentication (70% done)
- 🔄 Onboarding (80% done)

**Backend:** 90% feature-complete
- ✅ User management APIs
- ✅ Image upload/storage
- ✅ AI analysis integration
- ✅ JWT authentication
- ⏳ Production deployment pending

---

## 📞 Contact

**Saurabh Pandey** - Android Developer  
**Piyush** - Backend Developer

---

**Last Updated:** August 24, 2026

*Built with ❤️ for helping people feel confident in their skin.*
