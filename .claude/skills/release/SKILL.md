---
name: release
description: Create a new Speed of Sound app release.
argument-hint: <version> (e.g. 0.7.0)
disable-model-invocation: true
allowed-tools: Read, Write, Edit, Glob, Grep, Bash(git *), Bash(gh *)
---

Create a new release for Speed of Sound version $ARGUMENTS.

Follow these steps exactly:

## 1. Validate input

Confirm the version argument looks like `X.Y.Z` (semantic version, no leading `v`).
If not provided or invalid, stop and ask the user for the correct version.

Set `RELEASE_VERSION` = `$ARGUMENTS`.

## 2. Create a release branch

```bash
git checkout main && git pull
git checkout -b release/v$ARGUMENTS
```

## 3. Update the version files

**a. `VERSION`** — set contents to exactly `RELEASE_VERSION` with a trailing newline.
(`meson.build` and `build.gradle.kts` both read from this file automatically.)

**b. `data/io.speedofsound.App.metainfo.xml.in`** — add a new `<release>` entry inside `<releases>`.

To build the release notes:

   1. Find the most recent release tag: `git describe --tags --abbrev=0`
2. Get the tag date and list PRs merged since then:
   ```bash
   TAG_DATE=$(git for-each-ref --format='%(creatordate:short)' refs/tags/<previous-tag>)
   gh pr list --state merged --base main \
     --search "merged:>=$TAG_DATE" \
     --json number,title,body --limit 50
   ```
3. Read the metainfo file to understand the existing `<release>` format.
4. Prepend a new entry using today's date and a concise, user-facing summary derived from the PR titles and bodies.

The entry must follow this exact format (see existing entries for reference):
```xml
<release version="RELEASE_VERSION" date="YYYY-MM-DD">
    <url type="details">https://github.com/zugaldia/speedofsound/releases/tag/vRELEASE_VERSION</url>
    <description>
        <p>
            User-facing summary here.
        </p>
    </description>
</release>
```

## 4. Stage and commit

```bash
git add VERSION data/io.speedofsound.App.metainfo.xml.in
git commit -m "chore: release v$ARGUMENTS"
git push origin release/v$ARGUMENTS
```

## 5. Open a pull request

```bash
gh pr create \
  --base main \
  --title "chore: release v$ARGUMENTS" \
  --body "Release v$ARGUMENTS

- Updates \`VERSION\` to \`$ARGUMENTS\`
- Adds release notes to \`metainfo.xml.in\`

After merging, the \`tag-release\` workflow will automatically create and push the \`v$ARGUMENTS\` tag,
triggering the CI release build."
```

## 6. Post-merge steps (remind the user)

After the PR is merged, the `tag-release` workflow will automatically create
and push the `v$ARGUMENTS` tag, which triggers the release build.

Remind the user to:
1. Monitor the workflows at: https://github.com/zugaldia/speedofsound/actions
2. Verify the release at: https://github.com/zugaldia/speedofsound/releases

## Error recovery

If any step fails, do NOT retry blindly. Show the error to the user and ask how to proceed.
