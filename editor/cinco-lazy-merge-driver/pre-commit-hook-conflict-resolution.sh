#!/usr/bin/env bash

# Enable stricter error handling
set -euo pipefail

# Set eager mode ("true" or "false")
eager="false"  # Change to "true" to enable eager mode

# Define the regex patterns for specific forbidden values
EAGER_CONFLICT_PATTERN='"tag"\s*:\s*"(eager-merge-conflict)"'
LAZY_CONFLICT_PATTERN='"tag"\s*:\s*"(choice|ghost)"'

# Get a list of all staged files (ignoring deleted files)
STAGED_FILES=$(git diff --cached --name-only --diff-filter=ACM)

# Loop through each staged file
for FILE in $STAGED_FILES; do
    # Get the staged diff (only the changes staged for commit)
    STAGED_DIFF=$(git diff --cached "$FILE")
    
    # Check for "eager-merge-conflict" regardless of eager mode
    if echo "$STAGED_DIFF" | grep -Pq "$EAGER_CONFLICT_PATTERN"; then
        echo "Error: Detected conflict indicator (\"eager-merge-conflict\") in file \"$FILE\"."
        echo "Commit aborted: Committing eager merge conflicts is not allowed."
        exit 1
    fi

    # If eager mode is enabled, also check for "choice" and "ghost"
    if [ "$eager" = "true" ]; then
        if echo "$STAGED_DIFF" | grep -Pq "$LAZY_CONFLICT_PATTERN"; then
            echo "Error: Detected conflict indicator (\"choice\" or \"ghost\") in file \"$FILE\"."
            echo "Commit aborted: In eager merging mode, committing lazy conflicts is not allowed."
            exit 1
        fi
    fi
done
