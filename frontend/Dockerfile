# Use nginx to serve static files
FROM nginx:alpine

# Copy frontend files to nginx html directory
COPY . /usr/share/nginx/html/

# Copy startup script
COPY start.sh /start.sh
RUN chmod +x /start.sh

# Expose port 80
EXPOSE 80

# Start with environment variable substitution
CMD ["/start.sh"]
