# Build stage
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . ./
RUN npm run build

# Runtime stage
FROM node:20-alpine AS runtime
WORKDIR /app
COPY --from=builder /app/dist/frontend ./dist/frontend
EXPOSE 4000
ENV PORT=4000
CMD ["node", "dist/frontend/server/server.mjs"]
