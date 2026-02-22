# Releasing

## How versioning works

`speedofsound.version` in `gradle.properties` is used for local development builds (e.g. `0.1.0-dev`).
The git tag determines the actual release version via the `releaseVersion` Gradle property set by CI.

The version priority in `app/build.gradle.kts`:
1. CI release: `-PreleaseVersion=X.Y.Z` (from git tag)
2. Local property: `speedofsound.version` (from gradle.properties)
3. Fallback: `0.0.0-dev`

BuildConfig.VERSION is generated from these sources and displayed in the About dialog.

## Steps

1. Create a release branch and open a PR to bump the version:
   - Update `speedofsound.version` in `gradle.properties` to the *next* development version (e.g. `0.2.0-dev`)

   ```bash
   git checkout -b release/v0.1.0
   # Edit gradle.properties, then:
   git add gradle.properties
   git commit -m "chore: bump version to 0.2.0-dev"
   git push origin release/v0.1.0
   # Open a PR and merge it into main
   # Note: no need to delete the branch manually — GitHub is configured to auto-delete merged branches
   ```

2. Tag the merge commit on `main` and push the tag:
   ```bash
   git checkout main && git pull
   git tag v0.1.0
   git push origin v0.1.0
   ```

3. GitHub Actions runs automatically: builds, tests, and creates a GitHub Release.
   The release build will use version `0.1.0` extracted from the `v0.1.0` tag.

4. Verify the release on the [GitHub Releases](https://github.com/zugaldia/speedofsound/releases) page.
   The workflow automatically attaches `speedofsound.jar` (shadow JAR with all dependencies) as a release asset.

5. Profit.
