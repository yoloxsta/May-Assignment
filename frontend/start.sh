#!/bin/sh

# Replace placeholder with actual backend URL (for production use)
# In development, we use localhost:8080 directly
BACKEND_URL=${BACKEND_URL:-"http://localhost:8080"}

# Inject backend URL into JavaScript
sed -i "s|window.BACKEND_URL || \"http://localhost:8080\"|\"$BACKEND_URL\"|g" /usr/share/nginx/html/app.js

# Start nginx
exec nginx -g "daemon off;"
