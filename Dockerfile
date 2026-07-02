# ============================================
# Stage 1: Maven 构建
# ============================================
FROM maven:3.9.8-eclipse-temurin-21 AS builder

WORKDIR /app

# 先复制 pom.xml 利用 Docker 缓存层
COPY pom.xml .
RUN mvn dependency:resolve -B -q

# 复制源码并打包
COPY src ./src
RUN mvn package -DskipTests -B -q

# ============================================
# Stage 2: 运行镜像
# ============================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 创建非 root 用户 + 安装 curl（用于健康检查）
RUN addgroup -g 1000 appgroup && \
    adduser -u 1000 -G appgroup -D appuser && \
    apk add --no-cache curl

# 复制 jar
COPY --from=builder /app/target/*.jar app.jar

# 切换到非 root 用户
USER appuser

# 健康检查
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD curl -sf http://localhost:8080/api/test/hello || exit 1

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]
