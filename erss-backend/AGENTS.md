# Repository Guidelines

## Project Structure & Module Organization

This workspace contains two main projects. `erss/` is the uni-app frontend, with application code in `erss/src/`: pages in `src/pages/`, reusable components in `src/components/`, API clients in `src/api/`, stores in `src/store/`, hooks in `src/hooks/`, and utilities in `src/utils/`. Media lives under `src/static/` and `src/assets/`; vendored uni modules are in `src/uni_modules/`.

`erss-backend/` is the Spring Boot API. Java code lives in `src/main/java/com/erss/`, organized by `controller`, `service`, `mapper`, `domain/entity`, `dto`, `vo`, `common`, and `config`. SQL schema and seed files are in `erss-backend/database/`.

## Build, Test, and Development Commands

Use commands from each project directory:

- `cd erss && pnpm install`: install frontend dependencies from `pnpm-lock.yaml`.
- `cd erss && pnpm dev:h5`: run the H5 frontend locally.
- `cd erss && pnpm dev:mp-weixin`: build/watch the WeChat Mini Program target.
- `cd erss && pnpm build:h5`: create a production H5 bundle.
- `cd erss && pnpm lint`: run ESLint.
- `cd erss-backend && mvn spring-boot:run`: start the backend on port `8080`.
- `cd erss-backend && mvn test`: run backend tests.
- `cd erss-backend && mvn package`: compile and package the backend.

## Coding Style & Naming Conventions

Frontend code follows `erss/eslint.config.mjs` with `@antfu/eslint-config` and UnoCSS support. Use 2-space indentation for `.js`, `.vue`, and style files. Keep page entries as `index.vue` inside kebab-case route folders, for example `src/pages/my-shop/index.vue`.

Backend code targets Java 17. Use 4-space indentation, PascalCase class names, camelCase fields and methods, and package classes by responsibility. Keep request payloads in `dto`, responses in `vo`, persistence models in `domain/entity`, and web endpoints in `controller`.

## Testing Guidelines

The frontend currently has no dedicated automated test script; at minimum run `pnpm lint` and manually verify affected H5 or mini-program flows. Backend tests belong under `erss-backend/src/test/java/com/erss/` with `*Test` class names. Cover service logic, validation, mapper behavior, and controller errors when changing API behavior.

## Commit & Pull Request Guidelines

The frontend Git history uses Conventional Commit style, such as `docs: ...`, `fix: ...`, `feat: ...`, and `chore(main): release ...`. Keep commits focused and use the same prefixes. Pull requests should include a summary, affected area (`frontend`, `backend`, or both), validation steps, linked issues when relevant, and screenshots or recordings for UI changes.

## Security & Configuration Tips

Do not commit real credentials. The backend reads database and Redis settings from environment variables such as `DB_HOST`, `DB_PASSWORD`, `REDIS_HOST`, and `REDIS_PASSWORD`. Initialize local data with `erss-backend/database/init.sql`; put future database changes in `erss-backend/database/migrations/`.
