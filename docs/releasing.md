# Release

The release workflow runs only on `main`, checks that a successful CI run exists for the exact main commit, and reserves a draft GitHub release and tag before publishing the supplied SemVer version to Maven Central. It publishes the GitHub release after Central succeeds. Do not dispatch it until code review, platform validation and visual review are complete.

Add `docs/releases/<version>.md` before dispatch; it becomes the published GitHub release notes.

The `release` environment needs `CENTRAL_USERNAME`, `CENTRAL_PASSWORD`, `MAVEN_SIGNING_KEY` and, for a password-protected key, `MAVEN_SIGNING_PASSWORD`. The Central namespace must be owned by the publisher and the signing public key must be available to Central. Secrets are never committed. Signing artifacts is separate from Git commit/tag signing.

Central coordinates are immutable. The workflow creates a draft release before upload; an existing draft blocks a new attempt. If an upload times out or the run is interrupted, inspect the Central Portal deployment and its state before retrying; do not blindly upload the same version again. If Central has already published the artifacts, verify them and finish the draft GitHub release for the same commit rather than rebuilding or publishing another bundle.

A local unsigned check is `./gradlew :pill:publishAllPublicationsToLocalFixtureRepository`, followed by `./gradlew -p consumer-fixture compileKotlinJvm`. These commands do not publish a release.
