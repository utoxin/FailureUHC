@echo off
call gradlew cleanCache
call gradlew setupDecompWorkspace --refresh-dependencies
call gradlew idea