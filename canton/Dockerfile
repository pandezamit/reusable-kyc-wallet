# Dockerfile for Canton Open Source
# Build context must contain:
#   - canton-open-source-3.5.10.jar
#   - my-node.conf
#   - (optional) bootstrap.canton  -- the console script shown in your message
#
# Build:
#   docker build -t canton-node:3.5.10 .
#
# Run (adjust ports/volumes as needed):
#   docker run --rm -it \
#     -v $(pwd)/my-node.conf:/canton/my-node.conf:ro \
#     -p 10011-10012:10011-10012 \
#     canton-node:3.5.10

FROM eclipse-temurin:17-jre-jammy

# Canton 3.5.x is built and tested against JDK 17 (Temurin) — adjust the tag
# above if your Canton version requires a different major JDK version.

ARG CANTON_VERSION=3.5.10
ARG CANTON_USER=canton
ARG CANTON_HOME=/canton

# Create an unprivileged user to run the JVM as
RUN groupadd --system ${CANTON_USER} \
    && useradd --system --gid ${CANTON_USER} --home-dir ${CANTON_HOME} --create-home ${CANTON_USER}

WORKDIR ${CANTON_HOME}

# Copy the jar and its configuration into the image.
# (You can also mount these as volumes at runtime instead of baking them in.)
COPY canton-open-source-${CANTON_VERSION}.jar ${CANTON_HOME}/canton.jar
COPY my-node.conf ${CANTON_HOME}/my-node.conf

# Optional: bootstrap script that runs the console commands you listed
# (bootstrap.synchronizer, sequencers.local, mediators.local, participants.local, connect_local, ...)
# Comment this line out if you don't have such a file.
COPY bootstrap.canton ${CANTON_HOME}/bootstrap.canton

RUN chown -R ${CANTON_USER}:${CANTON_USER} ${CANTON_HOME}

USER ${CANTON_USER}

# Common Canton ports (adjust to match what your my-node.conf actually binds):
#   Participant Ledger API   : 10011
#   Participant Admin API    : 10012
#   Sequencer Public API     : 10018
#   Sequencer Admin API      : 10019
#   Mediator Admin API       : 10022
# EXPOSE 10011 10012 10018 10019 10022 
EXPOSE 5011 5012 5013 5018 5019 5020

# If you have a bootstrap script, invoke Canton with --bootstrap so the
# synchronizer/participant setup commands run automatically on startup:
#   java -jar canton.jar --config my-node.conf --bootstrap bootstrap.canton
#
# Without a bootstrap script, this just starts the interactive/daemon node
# using the given config.
ENTRYPOINT ["java", "-jar", "canton.jar", "--config", "my-node.conf", "--bootstrap", "bootstrap.canton"]
