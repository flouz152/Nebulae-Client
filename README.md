# Nebulae Click GUI Forge Mod

Этот репозиторий содержит отдельный мод для Forge 1.16.5, который переносит визуальное оформление ClickGUI из клиента Nebulae. Интерфейс открывается по клавише **Right Shift**, включает анимации появления/закрытия, скролл списков и описание модулей при наведении курсора. Внутренние функции модулей отсутствуют — реализован только интерфейс.

## Структура
- `clickgui-mod/` — полноценный Gradle-проект Forge 1.16.5.
  - `build.gradle`, `settings.gradle`, `gradle.properties` — конфигурация сборки.
  - `gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.properties` — скрипты Gradle Wrapper (под Gradle 7.6.1).
  - `src/main/java/com/nebulae/clickgui/...` — код клиента и GUI.
  - `src/main/resources/META-INF/mods.toml`, `pack.mcmeta` — метаданные и ресурсы мода.

## Сборка
1. Установите JDK 17.
2. Убедитесь, что у вас есть Gradle 7.6.1 (можно поставить локально или воспользоваться Gradle Wrapper из проекта).
   - Если `gradle-wrapper.jar` отсутствует, выполните `gradle downloadWrapperJar` из каталога `clickgui-mod` (подойдет любая установленная версия Gradle 7.x). Скрипт в `build.gradle` скачает нужный архив и извлечёт JAR в `gradle/wrapper/`.
   - После этого можно пользоваться `./gradlew` / `gradlew.bat` без дополнительной настройки.
3. В каталоге `clickgui-mod` выполните `./gradlew runClient` для запуска среды разработки или `./gradlew build` для сборки JAR (результат появится в `build/libs/`).

## Запуск в игре
1. Поместите собранный JAR в папку `mods` сборки Forge 1.16.5.
2. Запустите игру и нажмите **Right Shift**, чтобы открыть ClickGUI.

## Бинарные файлы
путь до файла который нужен | куда положить этот файл
--- | ---
clickgui-mod/gradle/wrapper/gradle-wrapper.jar | clickgui-mod/gradle/wrapper/gradle-wrapper.jar
