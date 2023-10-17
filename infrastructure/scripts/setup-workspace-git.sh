#!/usr/bin/env bash
set -euo pipefail
# How to use this script:
# 1. Open a terminal in the Theia workspace (Menu → Terminal → New Terminal)
# 2. Download the script: "curl -O https://gitlab.com/scce/cinco-cloud/-/raw/main/infrastructure/scripts/setup-workspace-git.sh"
# 3. Make the script executable: "chmod +x setup-workspace-git.sh"
# 4. Run the script: "./setup-workspace-git.sh"
# 5. You can then delete the script from the workspace: "rm setup-workspace-git.sh"
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
git config --local core.sshCommand "ssh -i '$workspace/.git/ssh/id_rsa'"
git remote add origin "$remote"
echo Done!
echo After you configured the deploy key, run \`git push -u origin main\` and confirm the SSH fingerprint. From then on, you can use the Theia Git UI.
echo Note: When the workspace pod gets destroyed, the list of well-known SSH hosts get lost, which will result in an authentication failure during synchronization. In that case, run \`git push\` on the terminal and confirm the SSH fingerprint.
