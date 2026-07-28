# ---- Stage 1: Install the Daml SDK and build the .dar ----
FROM eclipse-temurin:17-jdk-jammy AS damlbuild

RUN apt-get update && apt-get install -y --no-install-recommends curl ca-certificates \
    && rm -rf /var/lib/apt/lists/*

ENV TERM=xterm
RUN curl -sSL https://get.daml.com/ | sh
ENV PATH="/root/.daml/bin:${PATH}"

WORKDIR /daml-src
COPY daml.yaml .
COPY daml ./daml

RUN daml build -o /daml-src/output.dar

# ---- Stage 2: Slim runtime image that just uploads the dar to Canton ----
FROM eclipse-temurin:17-jre-jammy
WORKDIR /daml

RUN apt-get update && apt-get install -y --no-install-recommends bash netcat-openbsd \
    && rm -rf /var/lib/apt/lists/*

COPY --from=damlbuild /root/.daml /root/.daml
ENV PATH="/root/.daml/bin:${PATH}"

COPY --from=damlbuild /daml-src/output.dar /daml/reusable-kyc.dar
COPY upload-dar.sh /daml/upload-dar.sh
RUN chmod +x /daml/upload-dar.sh

ENTRYPOINT ["/daml/upload-dar.sh"]

