# Repository Guidelines

## Project Structure & Module Organization

Application code lives in `src/`. Keep route pages under `src/pages/`, reusable UI in `src/components/`, API layers in `src/api/`, Pinia stores in `src/store/`, and shared logic in `src/hooks/` and `src/utils/`. Routing and guards are split between `src/router/` and `src/permission/`. Use `src/assets/` for imported media and `src/static/` for platform static files. Build helpers and custom presets live in `helpers/`, while `pages.config.js`, `vite.config.js`, and `uno.config.js` define routing and build behavior.

## Build, Test, and Development Commands

Use `pnpm`; the lockfile and hooks are already configured for it.

- `pnpm install` installs dependencies.
- `pnpm dev:h5` runs the H5 target locally.
- `pnpm dev:mp-weixin` starts the WeChat Mini Program build.
- `pnpm build:h5` creates a production H5 bundle.
- `pnpm build:mp-weixin` builds the WeChat Mini Program target.
- `pnpm lint` runs ESLint across the project.
- `pnpm lint:fix` applies autofixes; the pre-commit hook runs this before commit.

## Coding Style & Naming Conventions

Follow `eslint.config.mjs`, which extends `@antfu/eslint-config` with UnoCSS support. Use 2-space indentation in `.js`, `.vue`, and style files. Prefer ESM imports and keep feature code colocated by folder. Page entry files should remain `index.vue` inside route folders such as `src/pages/search/index.vue`. Use kebab-case for folder names and route aliases like `/my-shop` and `/rentals/report`. Avoid editing vendored modules in `src/uni_modules/` unless the change is intentional and documented.

## Testing Guidelines

There is no dedicated automated test suite in the root project yet. Minimum validation is `pnpm lint`, followed by manual verification on the affected target with the matching `pnpm dev:*` command. When changing routes, stores, or permissions, verify navigation, persisted state, and platform-specific rendering.

## Commit & Pull Request Guidelines

Recent history uses Conventional Commits, for example `fix: ...`, `docs: ...`, and release chores such as `chore(main): release 3.1.1`. Keep subjects short, imperative, and scoped to one change. PRs should include a clear summary, linked issue when applicable, affected platforms, and screenshots or screen recordings for UI work.

## Configuration & Routing Notes

Keep secrets in local `.env.*` files, not in commits. When adding a page, update both `src/pages/` and `pages.config.js`; keep `aliasPath`, `name`, and folder structure aligned so router helpers and middleware keep working.
