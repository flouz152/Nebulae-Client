# Target ESP LabyMod 3 Addon

Готовый проект аддона для LabyMod 3, который повторяет поведение и визуальное оформление оригинального TargetESP. Проект можно собирать через Gradle и он автоматически подхватывает необходимые текстуры из клиента.

## Структура

- `src/main/java` — исходный код аддона.
- `src/main/resources` — описание аддона (`addon.json`).
- `build.gradle` / `settings.gradle` — конфигурация Gradle.

## Сборка

```bash
cd addons/targetesp
gradle build
```

Собранный JAR появится в `build/libs/targetesp-addon-1.0.0.jar` и готов к установке в LabyMod 3.

## Текстуры

| Где взять | Куда положить |
| --- | --- |
| `../../src/assets/minecraft/night/image/target/Quad.png` | `assets/minecraft/night/image/target/Quad.png` внутри JAR |
| `../../src/assets/minecraft/night/image/target/Quad2.png` | `assets/minecraft/night/image/target/Quad2.png` внутри JAR |
| `../../src/assets/minecraft/night/image/glow.png` | `assets/minecraft/night/image/glow.png` внутри JAR |

Gradle-скрипт автоматически копирует эти файлы в итоговый архив, поэтому дополнительных действий не требуется.
