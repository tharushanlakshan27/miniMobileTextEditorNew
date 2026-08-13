# Modern Mobile Text Editor with Incremental Version Control

A Kotlin-based Android text editor designed for developers and technical writers. The application provides a practical mobile editing experience together with local persistence, automatic draft recovery, syntax highlighting, and incremental version control.

## Project Overview

The **Modern Mobile Text Editor** allows users to create, open, edit, and save text files directly from an Android device.

The application goes beyond basic text editing by providing:

- Kotlin and Markdown syntax highlighting
- New File, Open File, Save and Save As
- UTF-8 and other encoding options
- Undo and Redo
- Search and Search & Replace
- Word Wrap
- Read-only mode
- Recent Files
- Automatic draft saving
- Crash/unsaved-draft recovery
- Room database persistence
- Version History
- Incremental diff/patch based version control
- Diff Viewer
- Version rollback/restore
- Settings and Dark Mode preference

## Main Technologies

| Technology | Purpose |
|---|---|
| Kotlin | Main programming language |
| Android | Mobile application platform |
| MVVM | Application architecture |
| Room Database | Persistent file and version metadata |
| Kotlin Coroutines | Background/asynchronous operations |
| SharedPreferences | Recent files and settings |
| Internal Storage | Files, drafts and version bases |
| java-diff-utils | Diff generation and patch application |
| Material Components | User interface |
| ViewBinding | View access and UI binding |

## Architecture

The application follows an MVVM-oriented structure.

```text
User Interface
     │
     ▼
Activities / UI
     │
     ▼
ViewModels
     │
     ▼
Repositories / Managers
     │
     ├──────────────► Room Database
     │
     └──────────────► Internal File Storage
                              │
                              ├── MyFiles
                              ├── Drafts
                              └── VersionBases
```

### Main Components

- `MainActivity` — application entry point and main navigation
- `EditorActivity` — main text editing screen
- `EditorViewModel` — editor state and operations
- `VersionHistoryActivity` — displays saved versions
- `DiffViewerActivity` — displays changes between versions
- `VersionHistoryViewModel` — version history state
- `FileRepository` — file database operations
- `VersionRepository` — version database operations
- `FileStorageManager` — local file, draft and version-base management
- `AutosaveManager` — automatic draft saving and recovery
- `VersionManager` — version creation, reconstruction and restoration
- `DiffHelper` — diff generation and patch application
- `UndoRedoManager` — undo/redo state management
- `KotlinSyntaxHighlighter` — Kotlin syntax highlighting
- `MarkdownSyntaxHighlighter` — Markdown syntax highlighting

## Editor Features

### File Management

The editor supports:

- Create a new text file
- Open an existing file
- Save the current file
- Save As
- File encoding selection

The Android document APIs are used for file selection and creation.

### Editing Tools

The editor provides:

- Undo
- Redo
- Search
- Search & Replace
- Word Wrap
- Read-only mode
- Character count
- Line count

Undo history is maintained using stacks and is limited to 100 states.

### Syntax Highlighting

The editor supports syntax highlighting for:

- **Kotlin**
- **Markdown**

The appropriate highlighter is selected based on the file extension.

## Automatic Save and Recovery

The application includes an automatic draft-saving mechanism.

For editable files:

1. The user edits the document.
2. The application monitors changes.
3. After approximately 10 seconds, changed content can be saved as a draft.
4. Drafts are stored in the application's internal `Drafts` directory.
5. When an unsaved draft is detected, the user can restore or discard it.

This helps reduce the risk of losing work after an unexpected interruption.

## Incremental Version Control

The project implements a lightweight incremental version-control system.

Instead of storing a complete copy of every version:

```text
Base Version
     │
     ├── Patch 1
     │
     ├── Patch 2
     │
     ├── Patch 3
     │
     └── ...
```

The first saved version provides the base content. Later versions store differences/patches.

### Version Workflow

```text
Current File
     │
     ▼
Save Version
     │
     ▼
Compare Previous Version
     │
     ▼
Generate Unified Diff
     │
     ▼
Store Patch in Room
     │
     ▼
Version History
     │
     ├── View Diff
     │
     └── Restore Version
```

### Diff Viewer

The Diff Viewer displays changes as:

- `ADDED`
- `REMOVED`
- `UNCHANGED`

The diff functionality is implemented using `java-diff-utils`.

### Rollback / Restore

When a previous version is restored:

1. The base version is loaded.
2. Required patches are applied chronologically.
3. The selected version is reconstructed.
4. The reconstructed content is written back.
5. The editor can continue working from the restored state.

## Database

The application uses Room Database for persistent metadata.

### File Entity

The file record contains information such as:

- File ID
- File name
- File path
- Created date
- Modified date
- Read-only state
- Encoding

### Version Entity

The version record contains:

- Version ID
- Associated file ID
- Version name
- Timestamp
- Diff/patch data

## Local Storage

The application maintains separate internal storage areas for:

```text
MyFiles/
Drafts/
VersionBases/
```

This separates normal application files, temporary recovery drafts, and base content used by incremental version control.

## Recent Files

Recent file information is maintained using `SharedPreferences`.

The recent-file list stores information such as:

- File name
- File path
- Last-opened date

The application keeps the latest 20 recent files.

## Project Structure

The project is organized around the Android application source and resource files.

Important areas include:

```text
app/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/mini/
│       └── res/
│           ├── layout/
│           ├── drawable/
│           ├── menu/
│           └── values/
├── build.gradle.kts
└── ...
```

The exact package and folder structure can be viewed directly in the source code.

## How to Run

### Requirements

- Android Studio
- Android SDK
- Kotlin support
- An Android emulator or physical Android device

### Steps

1. Clone this repository.

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
```

2. Open the project in **Android Studio**.

3. Allow Gradle to synchronize and download the required dependencies.

4. Connect an Android device or start an Android emulator.

5. Select the application configuration.

6. Click **Run** in Android Studio.

## Basic Usage

### Create a File

1. Open the application.
2. Select **New File**.
3. Enter the file content.
4. Save the document.

### Open a File

1. Select **Open File**.
2. Choose a text/Markdown file.
3. Edit the content.

### Save a Version

1. Open a file.
2. Make changes.
3. Select the version-saving option.
4. Enter a version name.
5. Open **Version History** to view saved versions.

### View Changes

1. Open Version History.
2. Select a saved version.
3. Choose **View Diff**.
4. Review added, removed and unchanged lines.

### Restore a Version

1. Open Version History.
2. Select the required version.
3. Choose **Restore**.
4. Confirm the restoration.

## Team Contributions

### Member 1 — N.T. Lakshan — 24020567

Main responsibilities:

- Project foundation
- Basic UI
- Application navigation
- Initial MVVM structure
- Home/editor application flow

### Member 2 — P.H.H. Rashmika — 24020852

Main responsibilities:

- Core editor interface
- File operations
- New/Open/Save/Save As
- Encoding support
- Word Wrap
- Read-only controls
- Undo/Redo
- Search and Replace
- Kotlin syntax highlighting
- Markdown syntax highlighting

### Member 3 — K.D. Punsara — 24020826

Main responsibilities:

- Room Database
- Repository/data layer
- Local storage management
- Automatic saving
- Draft recovery
- Version management
- Incremental version control
- Diff/Patch generation and application
- Version History
- Diff Viewer
- Rollback/Restore

## Project Demonstration Flow

For a project demonstration, the recommended order is:

1. Open the application
2. Show Home screen
3. Create or open a file
4. Demonstrate editing
5. Show syntax highlighting
6. Demonstrate Undo/Redo
7. Demonstrate Search and Replace
8. Demonstrate Save / Save As
9. Demonstrate Auto-save / Recovery
10. Save a version
11. Open Version History
12. View the Diff
13. Restore an earlier version

## Academic Project

This project was developed as part of the **Mobile Application Development (MAD)** coursework.

The project demonstrates practical knowledge of:

- Android application development
- Kotlin programming
- MVVM architecture
- Local persistence
- Database management
- File handling
- Text editor implementation
- Syntax highlighting
- Automatic recovery
- Incremental version control
- Diff and patch processing
- Team-based software development

## License

This project was developed for academic purposes.
