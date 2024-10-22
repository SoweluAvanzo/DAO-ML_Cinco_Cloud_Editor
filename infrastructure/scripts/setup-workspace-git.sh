#!/usr/bin/env bash
set -euo pipefail
# How to use this script:
# 1. Open a terminal in the Theia workspace (Menu → Terminal → New Terminal)
# 2. Download the script: "curl -O https://gitlab.com/scce/cinco-cloud/-/raw/main/infrastructure/scripts/setup-workspace-git.sh"
# 3. Make the script executable: "chmod +x setup-workspace-git.sh"
# 4. Run the script: "./setup-workspace-git.sh"
echo This script sets up a Git repository with an SSH-authenticated remote with all config files fully containted within the .git directory in the workspace, to persist the config across pods in the current cluster setup.
echo Be aware that this is a workaround until Git setup is properly supported in Cinco Cloud, and workspaces set up with this script will need to be migrated manually at that point.
echo -n "Enter your email for Git commits: "; read email
echo -n "Enter your name for Git commits: "; read name
echo -n "Enter the SSH URL of your remote repository: "; read remote
echo Running setup script…
workspace=/editor/workspace
cd "$workspace"
git init -b main
git config --local user.email "$email"
git config --local user.name "$name"
mkdir .git/ssh
ssh-keygen -f .git/ssh/id_rsa -N ""
echo Register the following SSH public key \(what\'s between the --------\) as a deploy key with write access in your repository:
echo WARNING: Do not add this key as a personal SSH key to your forge account to avoid Cinco Cloud having access to all your personal repositories!
echo --------------------------------------------------------------------------------
cat .git/ssh/id_rsa.pub
echo --------------------------------------------------------------------------------
git config --local core.sshCommand "ssh -i '$workspace/.git/ssh/id_rsa' -o UserKnownHostsFile='$workspace/.git/ssh/known_hosts'"
git remote add origin "$remote"
read -p 'Setup deploy key now. Grant write permissions! Press enter to continue.'
git fetch
git merge origin/main
git push -u origin main
rm setup-workspace-git.sh
echo Done!
