# We use the node image to install the http-server
FROM node:alpine

# Set the working directory
WORKDIR /app

# Copy the frontend files to the /app directory
COPY ./ /app

# Install the http-server
RUN npm install -g http-server

# Set the environment variable for the API URL
ENV API_URL=http://localhost:8080

# Expose the port for the server
EXPOSE 80

# Start the http-server to serve ALL frontend files
CMD ["http-server", "/app", "-p", "80", "--no-cache"]

