# Repository Guidelines

## Project Structure & Module Organization

This workspace contains three active projects. `erss/` is the uni-app mobile client; source lives in `erss/src/`, with pages in `src/pages/`, shared components in `src/components/`, API modules in `src/api/`, Pinia stores in `src/store/`, and static assets in `src/assets/` or `src/static/`. `erss-backend/` is the Java 17 Spring Boot API; code is under `src/main/java/com/erss/`, configuration is in `src/main/resources/application.yml`, and SQL setup/migrations are in `database/`. `erss-admin-frontend/` is the React/Vite admin UI, with feature code in `src/features/`.

## Build, Test, and Development Commands

- `cd erss && pnpm install`: install mobile client dependencies.
- `cd erss && pnpm dev:h5`: run the H5 client locally.
- `cd erss && pnpm build:h5`: build the H5 production bundle.
- `cd erss && pnpm lint`: run the configured ESLint checks.
- `cd erss-backend && mvn spring-boot:run`: start the API on port `8080`.
- `cd erss-backend && mvn test`: run backend tests.
- `cd erss-admin-frontend && npm install`: install admin dependencies.
- `cd erss-admin-frontend && npm run dev`: run the admin UI on port `4173`.
- `cd erss-admin-frontend && npm run build`: build the admin UI.

## Coding Style & Naming Conventions

Use 2-space indentation for JavaScript, Vue, JSX, CSS, and config files. The uni-app client follows `erss/eslint.config.mjs`, based on `@antfu/eslint-config` with UnoCSS support; run `pnpm lint:fix` before committing client changes. Keep route folders kebab-case, for example `src/pages/my-shop/index.vue`. In the backend, use package names under `com.erss`, PascalCase classes, camelCase fields/methods, and suffix request/response objects with `Request` or `VO`.

## Testing Guidelines

Backend tests should live in `erss-backend/src/test/java/` and use Spring Boot test tooling. Frontend projects currently have no dedicated test suite, so validate with lint/build commands plus manual checks of affected pages. For API changes, verify Swagger at `http://localhost:8080/swagger-ui.html` and update SQL migrations when schema changes.

## Commit & Pull Request Guidelines

The visible Git history uses Conventional Commits such as `fix: ...`, `docs: ...`, and `chore(main): release ...`. Keep commits focused and imperative. PRs should include a short summary, affected module, test/build results, linked issue when available, and screenshots or recordings for UI changes.

## Security & Configuration Tips

Do not commit real database, Redis, or token secrets. Prefer environment variables for `DB_*`, `REDIS_*`, and frontend `VITE_API_*` settings, and keep local overrides in untracked `.env.*` files.
