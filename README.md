# HUM Foundation Donation Tracker

## Team
- **Harri** — Frontend
- **Suhaan** — Admin actions, volunteer tracking, summary reports
- **Hemanth** — Monthly data trends

## Setup (one-time)

1. Install **Java 17+**: https://adoptium.net/
2. Download **JavaFX SDK 21** for Windows: https://gluonhq.com/products/javafx/
   - Extract it, then copy all `.jar` files from its `lib/` folder into this project's `lib/` folder.

## Run

Open a terminal in this folder, then:

```
compile.bat
run.bat
```

Data is saved automatically to a `data/` folder (created on first run).

## Git setup

```
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/YOUR_USERNAME/hum-tracker.git
git push -u origin main
```

> Note: `lib/*.jar` and `data/` are gitignored — teammates must download JavaFX SDK themselves.
