# 🎓 PlacementPortal — Campus Recruitment Android App

![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-SDK%2024+-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-Auth%20%7C%20Firestore%20%7C%20Storage-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)

A complete campus placement and recruitment management Android application built with **Kotlin** and **Firebase**. It bridges the gap between **Students**, **Companies (Recruiters)**, and **Placement Admins** through real-time job postings, profile management, and one-tap applications.

---

## 📱 App Screenshots

> Replace the placeholder `https://res.cloudinary.com/...` URLs below with your uploaded Cloudinary image links.

### 🔐 Authentication & Onboarding
| Role Selection | Login Screen | Student Registration |
| :---: | :---: | :---: |
| <img src="https://res.cloudinary.com/dqbv45r78/image/upload/v1795181288/Mode_selecttion_screen_cwaub8.png" width="230" alt="Role Selection"/> | <img src="https://res.cloudinary.com/dqbv45r78/image/upload/v1795181289/student_login_screen_f6vpv0.png" width="230" alt="Login Screen"/> | <img src="https://res.cloudinary.com/dqbv45r78/image/upload/v1795181288/Company_Registration_screen_zinjer.png" width="230" alt="Student Registration"/> |

### 🎓 Student Module
| Student Dashboard | Browse Jobs | Job Details & Apply |
| :---: | :---: | :---: | :---: |
| <img src="https://res.cloudinary.com/dqbv45r78/image/upload/v1795181288/dashboard_view_and_apply_kkxz4k.png" width="200" alt="Student Dashboard"/> | <img src="https://res.cloudinary.com/dqbv45r78/image/upload/v1795181287/company_profile_ake3bm.png" width="200" alt="Browse Jobs"/> | <img src="https://res.cloudinary.com/YOUR_CLOUD_NAME/image/upload/job_details.png" width="200" alt="Job Details"/> /> |

### 🏢 Company & Admin Modules
| Company Profile | Post New Job | View Applicants |
| :---: | :---: | :---: | :---: |
| <img src="https://res.cloudinary.com/dqbv45r78/image/upload/v1795181287/company_profilereal_sg6th5.png" width="200" alt="Company Profile"/> | <img src="https://res.cloudinary.com/dqbv45r78/image/upload/v1795181289/Post_a_new_job_st2ukz.png" width="200" alt="Post Job"/> | <img src="https://res.cloudinary.com/dqbv45r78/image/upload/v1795181289/Student_Dashboard_vsm09z.png" width="200" alt="View Applicants"/>/> |

---

## ✨ Key Features

### 👨‍🎓 For Students
* **Multi-Step Registration & Profile:** Build and update academic credentials, skills, and personal details.
* **Live Job Feed:** Browse active placement drives with role descriptions, eligibility criteria, and package details.
* **One-Tap Application & Tracking:** Apply directly to openings and track application status in the **Applied Jobs** tab.

### 🏢 For Companies
* **Company Registration & Profile:** Manage organization details and recruiter contact info.
* **Job Posting:** Publish new campus job openings with role requirements and deadlines.
* **Applicant Management:** Review candidates who applied to posted roles and inspect detailed student profiles.

### 🛡️ For Admins
* **Centralized Oversight:** Dedicated admin login and profile dashboard to oversee campus placement activity.

---

## 🛠️ Tech Stack

* **Language:** Kotlin
* **UI:** XML Layouts, Material Design Components, RecyclerView
* **Architecture:** Activity-based Multi-Role Flow
* **Backend & Database:** Firebase Authentication, Cloud Firestore / Realtime Database, Firebase Storage
* **Build System:** Gradle (Kotlin DSL `.kts`)

---

## 📂 Project Structure

```text
app/src/main/java/com/example/placementportal/
├── SelectionActivity.kt          # Entry point: Student / Company / Admin role chooser
├── Login_Activity.kt             # User authentication
├── Register_one_Activity.kt      # Multi-step student registration (Steps 1–3)
├── StudentDashboard.kt           # Student home & navigation hub
├── JobsActivity.kt               # Available job listings
├── JobDetailsActivity.kt         # Full job description & apply action
├── AppliedJobsActivity.kt        # History of jobs applied by the student
├── CompanyRegister.kt            # Recruiter registration
├── PostJobActivity.kt            # Create & publish job openings
├── ViewApplicantsActivity.kt     # List of applicants per job
└── AdminLoginActivity.kt         # Admin portal authentication