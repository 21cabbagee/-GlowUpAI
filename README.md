# 🌟 GlowUp AI - Your Personal Skin Transformation Journey

> **Active development has moved to [piyushxpc7/Skinproof](https://github.com/piyushxpc7/Skinproof)**,
> which now contains both a more complete Android app and the backend, in one
> repo. This repo (`-GlowUpAI`) is kept for history but is no longer where new
> work happens. (Aug 30, 2026)

> **AI-powered skincare tracking that shows real results**

GlowUp AI is a mobile app that helps you achieve your skin goals through daily selfie tracking, AI-powered skin analysis, and beautiful progress visualization.

---

## 📱 What is GlowUp AI?

**The Problem:** Skincare is a journey, not a destination. But without tracking, it's hard to see if your routine is actually working.

**The Solution:** GlowUp AI makes skin progress visible. Take a daily selfie, get AI analysis, and watch your skin improve over time with beautiful charts and before/after comparisons.

**Target Users:** Anyone serious about skincare (ages 18-35, primarily India, 80% Android users).

---

## ✨ Key Features

### **🤳 Daily Selfie Tracking**
- One selfie a day, front camera with face detection guides
- Lighting validation (warns if too dark)
- Streak tracking (build the habit!)
- Offline support (local caching)

### **🧠 AI Skin Analysis**
- **8 metrics tracked:** Acne, dark spots, wrinkles, skin tone, moisture, pore size, texture, overall score
- Heatmap visualization showing problem areas
- Severity scores (0-100) for each metric
- Powered by third-party skin analysis APIs (SkinProof/HautAI)

### **📊 Progress Visualization**
- Side-by-side before/after comparison
- Line charts showing metric trends over time
- 7-day, 30-day, 90-day views
- Milestone celebrations ("Your acne reduced by 30%!" 🎉)
- Before/after slider for visual comparison

### **💎 Premium Features ($19.99/month)**
- Advanced analysis (8+ metrics vs 3 in free)
- 30-day & 90-day progress tracking (free gets 7 days)
- Personalized skincare routine recommendations
- Export progress reports (PDF)
- Priority support
- Ad-free experience

---

## 🛠️ Tech Stack

**Android App:**
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (modern, reactive UI)
- **Camera:** CameraX API + ML Kit (face detection)
- **Networking:** Retrofit + OkHttp
- **Auth:** Firebase Authentication (Google Sign-In + Email/Password)
- **State Management:** ViewModel + StateFlow
- **Storage:** Encrypted SharedPreferences (tokens), Room (offline cache)

**Backend:**
- **Framework:** Python + FastAPI
- **Database:** PostgreSQL (user data, selfie metadata)
- **Storage:** AWS S3 / Cloudinary (selfie images)
- **AI:** Third-party skin analysis APIs (HautAI, SkinProof)
- **Auth:** JWT tokens
- **Monitoring:** Sentry (errors) + Mixpanel (analytics)

---

## 📂 Project Structure

```
GlowUp/
├── app/
│   ├── src/main/
│   │   ├── java/com/glowup/ai/
│   │   │   ├── MainActivity.kt          # Entry point
│   │   │   ├── CameraScreen.kt          # Selfie capture
│   │   │   ├── ApiService.kt            # Backend API client
│   │   │   └── ui/
│   │   │       ├── screens/             # All app screens
│   │   │       ├── components/          # Reusable UI components
│   │   │       └── theme/               # Colors, typography
│   │   └── res/                         # Resources (icons, strings)
│   └── build.gradle.kts
├── JOURNEY.md                           # 15-milestone development story
└── README.md                            # This file
```

---

## 🚀 Getting Started

### **Prerequisites:**
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17+
- Android SDK 24+ (API Level 24+)

### **Setup:**

1. **Clone the repo:**
   ```bash
   git clone https://github.com/YOUR_USERNAME/GlowUp.git
   cd GlowUp
   ```

2. **Open in Android Studio:**
   - File → Open → Select `GlowUp` folder

3. **Sync Gradle:**
   - Android Studio will auto-sync dependencies
   - If not, click "Sync Now" in the banner

4. **Configure Backend URL:**
   - Open `ApiService.kt`
   - Update `BASE_URL` to point to your backend (or use mock data for testing)

5. **Run the app:**
   - Connect Android device or start emulator
   - Click "Run" (green play button) or press Shift+F10

---

## 📸 Screenshots

*(Coming soon after Play Store assets are created)*

---

## 📈 Current Status

**Development Progress:** 85% feature-complete, 60% launch-ready

**Milestones:**
- ✅ **5 Complete:** Backend integration, camera, AI analysis, progress tracking, premium UI
- 🔄 **3 In Progress:** Onboarding flow, authentication, app icon/splash
- ⏳ **7 Pending:** Backend deployment, monetization, analytics, Play Store launch

**Next Steps:**
1. Finish authentication (1-2 days)
2. Complete app icon/splash (1 day)
3. Deploy backend (1 day)
4. Alpha testing (1 week)
5. Play Store launch (Early September 2026)

---

## 🎯 Roadmap

### **Phase 1: Launch (Sep 2026)**
- ✅ Core features (selfie, analysis, progress)
- 🔄 Authentication & onboarding
- ⏳ Play Store launch
- **Goal:** 100 downloads, first paying user

### **Phase 2: Growth (Oct-Nov 2026)**
- 🎯 Freemium monetization
- 🎯 Viral marketing (TikTok, Reddit)
- 🎯 Landing page + waitlist
- **Goal:** $2K MRR (100 paying users)

### **Phase 3: Scale (Q4 2026 - Q1 2027)**
- 🎯 iOS app
- 🎯 Social features (share progress)
- 🎯 Gamification (challenges, rewards)
- **Goal:** $20K MRR (1,000 paying users)

### **Phase 4: Fundraise (Q2 2027)**
- 🎯 Seed round ($2-5M)
- 🎯 Team expansion (10 people)
- 🎯 Advanced AI (personalized routines)
- **Goal:** $100K MRR (5,000 paying users)

---

## 💰 Monetization

**Free Tier:**
- Daily selfie + basic analysis
- 7-day progress tracking
- 3 metrics (acne, dark spots, skin tone)

**Premium Tier ($19.99/month or $149/year):**
- Advanced analysis (8+ metrics)
- 30-day & 90-day tracking
- Personalized skincare recommendations
- Export reports (PDF)
- Ad-free + priority support

**Revenue Projections:**
- **Month 1:** $200 (10 paying users)
- **Month 3:** $2,000 (100 paying users)
- **Month 6:** $10,000 (500 paying users)
- **Year 1:** $100,000 MRR (5,000 paying users)

---

## 👥 Team

**Saurabh Pandey** - Android Developer
- Built entire Android app from scratch (4 days)
- Kotlin expert, Jetpack Compose pro
- Product thinking + execution speed

**Co-Founder** - Backend Developer
- Built backend API (Python FastAPI)
- Integrated AI skin analysis APIs
- Database & cloud infrastructure

---

## 📄 License

**Proprietary - All Rights Reserved**

This is a closed-source commercial project. Unauthorized copying, distribution, or use of this code is prohibited.

---

## 📞 Contact

**Email:** TBD  
**Website:** TBD  
**Twitter:** TBD

---

## 📚 Documentation

- [JOURNEY.md](JOURNEY.md) - Complete 15-milestone development story

---

**Last Updated:** August 24, 2026

*Built with ❤️ by two builders who want to help people feel confident in their skin.*
