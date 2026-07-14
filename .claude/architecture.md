# Architecture

Version: 1.0

---

# Architectural Style

The project follows:

- Domain Driven Design (DDD)
- Clean Architecture
- SOLID
- Clean Code

---

# Layers

Presentation

↓

Application

↓

Domain

↓

Infrastructure

Dependencies always point inward.

---

# Domain Layer

Contains:

- Entities
- Value Objects
- Aggregates
- Domain Services
- Repository Interfaces
- Domain Events
- Business Rules

This layer contains NO framework code.

No Spring.

No SQL.

No HTTP.

---

# Application Layer

Coordinates business use cases.

Responsibilities:

- Execute use cases
- Transaction management
- Validation
- Orchestration
- DTO Mapping

No business rules should live here.

---

# Infrastructure Layer

Contains:

- Spring Data
- JPA
- Database
- External APIs
- Email
- Cache
- Security
- File System

Infrastructure implements interfaces defined by the Domain.

---

# Presentation Layer

Contains:

- REST Controllers
- Request Validation
- Response Mapping

Controllers should remain thin.

Business logic never belongs here.

---

# Architectural Rules

Never skip layers.

Never call Infrastructure directly from Presentation.

Never expose Entities through the API.

Never place business rules inside Controllers.

Never place business rules inside Repositories.

---

# Dependency Rule

Domain

knows nothing.

Application

depends only on Domain.

Infrastructure

depends on Domain.

Presentation

depends on Application.

---

# Preferred Design

Rich Domain Model.

Behavior over Data.

Encapsulation over exposure.

Composition over inheritance.

Interfaces over implementations.