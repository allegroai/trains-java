FROM mozilla/sbt:8u212_1.3.4 as SBT_DEV

FROM SBT_DEV as BUILDER

WORKDIR /app

COPY ./rootfs/app/build.sbt .
COPY ./rootfs/app/project ./project

RUN sbt update

COPY ./rootfs /
RUN sbt clean package
