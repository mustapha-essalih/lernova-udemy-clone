
## Online Learning Management System (Udemy Clone)

This personal full-stack project is a production-ready Online Learning Management System (LMS) designed to handle high concurrency and large data volumes, replicating core features of major platforms like Udemy. It demonstrates expertise in microservices architecture, cloud storage, real-time performance optimization, and secure, role-based access control.

**Technologies Used:**
Java, Spring Boot, Reactive Programming (Spring WebFlux), Next.js, PostgreSQL, Redis, Elasticsearch, AWS S3, Keycloak, Docker, Stripe

---

### Requirements

The application was built with comprehensive features and distinct workflows for four user roles: **Student, Instructor, Manager, and Admin**.

---

### 1. Core Platform & Backend Architecture

* **Scalable Backend:** Developed reactive microservices using **Java Spring Boot with WebFlux** for efficient handling of large file uploads and content retrieval.
* **Authentication & Security:** Implemented secure authentication and authorization via **Keycloak**, utilizing **Role-Based Access Control (RBAC)** for students, instructors, managers, and admins.
* **Data & Performance:**

  * Integrated **Elasticsearch** for advanced course search, filtering, autocomplete, and recommendations.
  * Implemented **Redis caching** to enhance performance and reduce database load on frequently accessed data.
* **Payments:** Integrated **Stripe** for secure payment workflows, covering purchases, refunds, and instructor payouts.
* **Cloud Storage:** Used **AWS S3** for scalable storage and secure delivery of all course content (videos, documents, images).
* **Frontend:** Built a responsive and dynamic front-end with **Next.js**, supporting a seamless learning and management experience.

---

### 2. Role-Based Workflows

* **Student / Learner Workflow**

  * Advanced course browsing with search and filtering.
  * Secure checkout with coupon code support.
  * Lesson progress tracking, Q&A sections.

* **Instructor Workflow**

  * Dedicated dashboard for managing course creation and publishing submissions for review.
  * Performance analytics (sales, enrollment, reviews).
  * Tools for managing student interactions (Q&A).

* **Manager Workflow**

  * Dashboard to review pending course submissions.
  * Approve or reject courses, provide feedback to instructors, and publish approved content.

* **Admin Workflow**

  * Full platform control with system-wide analytics.
  * User management (edit/suspend accounts) and content moderation.
  * Category management, system logs, and platform settings.

* **Dashboards**

  * Distinct dashboards for each role (student, instructor, manager, admin) to manage enrollments, content, analytics, and reviews.
