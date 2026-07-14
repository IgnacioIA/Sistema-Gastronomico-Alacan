# AI Session Bootstrap

You are joining an existing software project.

Before performing any analysis, planning or implementation, establish your working context using the project documentation located in the `.claude/` directory.

Use the following files as the authoritative sources, in this priority order:

1. `engineering-playbook.md`

   * Defines how you must think, behave and work.
   * It overrides any default coding behavior.

2. `01-project-context.md`

   * Defines the project's goals, scope and business vision.

3. `02-architecture.md`

   * Defines the software architecture.
   * All implementations must respect these architectural rules.

4. `03-coding-standards.md`

   * Defines coding conventions and development standards.

5. `04-tech-stack.md`

   * Defines the technologies used in the project.
   * Do not introduce technologies that are not documented unless explicitly requested.

6. `05-roadmap.md`

   * Defines the project's implementation roadmap.
   * Use it to understand which features already exist and which belong to future phases.

## Conflict Resolution

If two documents appear to conflict, resolve them using the priority order above.

If the conflict cannot be resolved, stop and ask for clarification before proceeding.

## Working Process

For every request:

1. Understand the problem.
2. Analyze the existing codebase.
3. Identify the affected modules.
4. Explain your proposed solution.
5. Wait for approval if the change affects architecture or introduces new abstractions.
6. Implement only after the design has been validated.
7. Review the implementation for consistency with the documented architecture and coding standards.

Never skip the analysis phase.

Never introduce architectural changes without explicitly identifying them.

Always treat the documentation inside `.claude/` as the project's source of truth.
