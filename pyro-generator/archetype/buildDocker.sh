#sh
echo "STEP 1: login into registry..."
docker login registry.gitlab.com -u gitlab+deploy-token-579599 -p _G_twHbYhm7vuKYC7Hp8
echo "STEP 2: building docker-image"
docker build -t editor .
echo "FINISHED"